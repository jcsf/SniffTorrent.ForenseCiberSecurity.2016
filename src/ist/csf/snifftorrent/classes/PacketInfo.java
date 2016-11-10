package ist.csf.snifftorrent.classes;

import java.io.Serializable;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.Date;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.*;
import org.jnetpcap.protocol.lan.Ethernet;

public class PacketInfo implements Serializable {

    /* ---| INFRACTIONS TYPE |--- */
    public static final int BITTORRENT_HANDSHAKE = 0;
    public static final int BITTORRENT_PROTOCOL = 1;
    public static final int UTORRENT_PACKAGE = 2;

    /* ---| CLASS VARIABLES |--- */
    private int infraction_type;
    private String packetType;
    private String infractor_IP, infractor_MAC;
    private Date timeStamp;
    private byte [] packet;
    private int packetHash;

    public PacketInfo (int type, PcapPacket packet) {
        byte [] packetBytes = new byte [packet.getTotalSize()];
        packet.transferStateAndDataTo(packetBytes);

        this.infraction_type = type;
        this.packet = packetBytes;


        if (packet.hasHeader(new Tcp())) {
            packetType = "TCP";
        } else {
            packetType = "UDP";
        }

        Ethernet ethernetInfo = new Ethernet();
        packet.getHeader(ethernetInfo);

        String sourceIP = getSourceIP();
        String destinationIP = getDestinationIP();

        int pingSource = Ping.pingTime(sourceIP);
        int pingDestination = Ping.pingTime(destinationIP);

        if (pingDestination < pingSource) {
            this.infractor_IP = destinationIP;
            this.infractor_MAC = FormatUtils.mac(ethernetInfo.destination());
        } else {
            this.infractor_IP = sourceIP;
            this.infractor_MAC = FormatUtils.mac(ethernetInfo.source());
        }

        this.timeStamp = new Date(packet.getCaptureHeader().timestampInMillis());

        this.packetHash = java.util.Arrays.hashCode(packetBytes);
    }

    public int getInfractionType() { return this.infraction_type; };

    public String getPacketType() { return packetType; }

    public String getInfractor_IP() { return this.infractor_IP; }

    public String getInfractor_MAC() { return this.infractor_MAC; }

    public Date getTimeStamp() { return this.timeStamp; }

    public int getHash() {return this.packetHash; }

    public PcapPacket getPacket() {
        return new PcapPacket (this.packet);
    }

    public String getSourceIP() {
        PcapPacket packet = getPacket();
        Ip4 ipInfo = new Ip4();
        packet.getHeader(ipInfo);

        return FormatUtils.ip(ipInfo.source());
    }

    public String getDestinationIP() {
        PcapPacket packet = getPacket();
        Ip4 ipInfo = new Ip4();
        packet.getHeader(ipInfo);

        return FormatUtils.ip(ipInfo.destination());
    }

    public int getSourcePort() {
        PcapPacket packet = getPacket();

        if (packetType.equals("TCP")) {
            Tcp tcpInfo = new Tcp();
            packet.getHeader(tcpInfo);
            return tcpInfo.source();
        } else {
            Udp udpInfo = new Udp();
            packet.getHeader(udpInfo);
            return udpInfo.source();
        }
    }

    public int getDestinationPort() {
        PcapPacket packet = getPacket();

        if (packetType.equals("TCP")) {
            Tcp tcpInfo = new Tcp();
            packet.getHeader(tcpInfo);
            return tcpInfo.destination();
        } else {
            Udp udpInfo = new Udp();
            packet.getHeader(udpInfo);
            return udpInfo.destination();
        }
    }

    public BitTorrentHandshake getBitTorrentHandshake() {
        if (this.infraction_type == BITTORRENT_HANDSHAKE) {
            return new BitTorrentHandshake(getHexRawFromPackage(getPacket()));
        }

        return null;
    }

    @Override
    public String toString() {
        return "---| PACKET INFO |---\n" + "Infraction Type: " + getInfractionTypeDescription() + "\n"
                + "Infractor IP: " + infractor_IP + "\nInfractor MAC: " + infractor_MAC + "\nTimeStamp: " + timeStamp
                + "\n-----------------------------------\n";
    }

    public String toStringWithPacketDetails() {
        return "---| PACKET INFO |---\n" + "Infraction Type: " + getInfractionTypeDescription() + "\n"
                + "Infractor IP: " + infractor_IP + "\nInfractor MAC: " + infractor_MAC + "\nTimeStamp: " + timeStamp
                + "Packet Details:\n" + getPacket().toString() + "\n-----------------------------------\n";
    }

    public String getInfractionTypeDescription() {
        switch (this.infraction_type) {
            case BITTORRENT_HANDSHAKE:
                return "BITTORRENT HANDSHAKE";
            case BITTORRENT_PROTOCOL:
                return "BITTORRENT PROTOCOL";
            case UTORRENT_PACKAGE:
                return "UTORRENT PACKAGE";
        }

        return "UNKNOW";
    }

    public static String getHexRawFromPackage(PcapPacket packet) {
        String packetHexDump = packet.toHexdump(1024, false, false, true);

        return packetHexDump.replaceAll("\\s+","");
    }
}


class Ping {

    public static int pingTime(String ip) {
        String dataS = "";
        try {
            Process p = null;

            if(System.getProperty("os.name").contains("Windows")) {
                p = Runtime.getRuntime().exec("ping " + ip + " -n 1");
            } else {
                p = Runtime.getRuntime().exec("ping " + ip);
            }

            BufferedReader inputStream = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String inputS;

            // Reading Output from Stream Command
            while ((inputS = inputStream.readLine()) != null) {
                dataS = inputS;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Get Average Time
        if(System.getProperty("os.name").contains("Windows")) { // WINDOWS
            if (dataS.contains("Average =")) {
                return Integer.parseInt(dataS.substring(dataS.indexOf("Average = ") + 10, dataS.lastIndexOf("ms")));
            } else {
                return Integer.MAX_VALUE;
            }
        } else { // UNIX
            // TODO
            return Integer.MAX_VALUE;
        }
    }

}
