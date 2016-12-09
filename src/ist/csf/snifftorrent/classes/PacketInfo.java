package ist.csf.snifftorrent.classes;

import java.io.Serializable;

import java.util.Date;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.tcpip.*;

public class PacketInfo implements Serializable {

    /* ---| INFRACTIONS TYPE |--- */
    public static final int BITTORRENT_HANDSHAKE = 0;
    public static final int BITTORRENT_PROTOCOL = 1;
    public static final int UTORRENT_PACKAGE = 2;
    public static final int VUZE_PACKAGE = 3;
    public static final int DELUGE_PACKAGE = 4;

    public static final int UDP_PACKAGE = 5;

    /* ---| CLASS VARIABLES |--- */
    private int infraction_type;
    private String packetType;
    private String sourceIP, sourceMAC;
    private String destinationIP, destinationMAC;
    private int sourcePort, destinationPort;
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

        String [] sourceDestinationIP =  Connection.getSourceAndDestinationIP(packet);
        String [] sourceDestinationMAC =  Connection.getSourceAndDestinationMAC(packet);
        int [] sourceDestinationPort = getSourceDestinationPorts();

        this.sourceIP = sourceDestinationIP[Connection.SOURCE];
        this.sourceMAC = sourceDestinationMAC[Connection.SOURCE];
        this.sourcePort = sourceDestinationPort[Connection.SOURCE];

        this.destinationIP = sourceDestinationIP[Connection.DESTINATION];
        this.destinationMAC = sourceDestinationMAC[Connection.DESTINATION];
        this.destinationPort = sourceDestinationPort[Connection.DESTINATION];

        this.timeStamp = new Date(packet.getCaptureHeader().timestampInMillis());

        this.packetHash = java.util.Arrays.hashCode(packetBytes);
    }

    public int getInfractionType() { return infraction_type; }

    public String getPacketType() { return packetType; }

    public String getSourceIP() { return sourceIP; }

    public String getSourceMAC() { return sourceMAC;  }

    public String getDestinationIP() { return destinationIP; }

    public String getDestinationMAC() { return destinationMAC; }

    public int getSourcePort() { return sourcePort; }

    public int getDestinationPort() { return destinationPort; }

    public Date getTimeStamp() { return timeStamp; }

    public int getHash() { return packetHash; }

    public PcapPacket getPacket() {
        return new PcapPacket (this.packet);
    }


    private int [] getSourceDestinationPorts() {
        int [] ports = new int [2];
        PcapPacket packet = getPacket();

        if (packetType.equals("TCP")) {
            Tcp tcpInfo = new Tcp();
            packet.getHeader(tcpInfo);
            ports[Connection.SOURCE] = tcpInfo.source();
            ports[Connection.DESTINATION] = tcpInfo.destination();
        } else {
            Udp udpInfo = new Udp();
            packet.getHeader(udpInfo);
            ports[Connection.SOURCE] = udpInfo.source();
            ports[Connection.DESTINATION] = udpInfo.destination();
        }


        return ports;
    }

    public String getInfractionTypeDescription() {
        switch (this.infraction_type) {
            case BITTORRENT_HANDSHAKE:
                return "BITTORRENT HANDSHAKE";
            case BITTORRENT_PROTOCOL:
                return "BITTORRENT PROTOCOL";
            case UTORRENT_PACKAGE:
                return "UTORRENT PACKAGE";
            case VUZE_PACKAGE:
                return "VUZE PACKAGE";
            case DELUGE_PACKAGE:
                return "DELUGE PACKAGE";
            case UDP_PACKAGE:
                return "UDP PACKAGE";
        }

        return "UNKNOW";
    }

    @Override
    public String toString() {
        return "---| PACKET INFO |---\n" + "Infraction Type: " + getInfractionTypeDescription() + "\n"
                + "Source IP: " + sourceIP + "\nInfractor MAC: " + sourceMAC + "\nTimeStamp: " + timeStamp
                + "\n-----------------------------------\n";
    }

    public String getHTMLTypeLayout() { return ""; }

    public static String getHexRawFromPackage(PcapPacket packet) {
        String packetHexDump = packet.toHexdump(1024, false, false, true);

        return packetHexDump.replaceAll("\\s+","");
    }
}