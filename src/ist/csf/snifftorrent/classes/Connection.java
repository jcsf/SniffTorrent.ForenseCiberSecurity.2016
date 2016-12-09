package ist.csf.snifftorrent.classes;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Connection implements Serializable {
    public static final int SOURCE = 0, DESTINATION = 1;
    public static final int BITTORRENT_TRAFFIC = 0, UDP_TRAFFIC = 1;

    private int hash;
    private int type;
    private String infractorIP, infractorMAC;
    private String outsideIP, outsideMAC;
    ArrayList<PacketInfo> timeline;

    private int UDPPacketCounter;

    public Connection (int type, String infractorIP, String outsideIP, String infractorMAC, String outsideMAC) {
        this.type = type;
        this.infractorIP = infractorIP;
        this.infractorMAC = infractorMAC;
        if (type == UDP_TRAFFIC) {
            this.outsideIP = "Multiple Sources";
            this.outsideMAC = "Multiple Sources";
        } else {
            this.outsideIP = outsideIP;
            this.outsideMAC = outsideMAC;
        }
        this.timeline = new ArrayList<>();
        String mix = infractorIP + "|" + outsideIP + "|" + new Date();
        this.hash = mix.hashCode();

        this.UDPPacketCounter = 0;
    }

    public int getHash() { return this.hash; }
    public int getType() { return this.type; }
    public String getInfractorIP() { return this.infractorIP; }
    public String getInfractorMAC() { return this.infractorMAC; }
    public String getOutsideIP() { return this.outsideIP; }
    public String getOutsideMAC() { return this.outsideMAC; }
    public ArrayList<PacketInfo>getTimeline() { return this.timeline; }

    public void increaseUDPPacketCounter(PacketInfo p) {
        this.timeline.remove(1);
        this.timeline.add(p);
        this.UDPPacketCounter++;
    }

    public void setUDPPacketCounter() { this.UDPPacketCounter = this.timeline.size(); }
    public int getUDPPacketCounter() { return this.UDPPacketCounter; }

    public void addPacketToTimeline(PacketInfo packet) {
        this.timeline.add(packet);
    }

    public static Connection createConnection (int type, PacketInfo packetInfo) {
        Connection newCon;
        PcapPacket packet = packetInfo.getPacket();

        String [] source_destinationIP = getSourceAndDestinationIP(packet); // 0 - Source | 1 - Destination
        String [] source_destinationMAC = getSourceAndDestinationMAC(packet); // 0 - Source | 1 - Destination
        int pingSource = Ping.pingTime(source_destinationIP[SOURCE]);
        int pingDestination = Ping.pingTime(source_destinationIP[DESTINATION]);

        if (pingDestination < pingSource) {
            newCon = new Connection(type, source_destinationIP[DESTINATION], source_destinationIP[SOURCE], source_destinationMAC[DESTINATION], source_destinationMAC[SOURCE]);
        } else {
            newCon = new Connection(type, source_destinationIP[SOURCE], source_destinationIP[DESTINATION], source_destinationMAC[SOURCE], source_destinationMAC[DESTINATION]);
        }

        return newCon;
    }

    public static String [] getSourceAndDestinationIP(PcapPacket packet) {
        String[] ips = new String[2];
        Ip4 ipInfo = new Ip4();
        packet.getHeader(ipInfo);

        ips[0] = FormatUtils.ip(ipInfo.source());
        ips[1] = FormatUtils.ip(ipInfo.destination());

        return ips;
    }

    public static String [] getSourceAndDestinationMAC(PcapPacket packet) {
        String[] macs = new String[2];
        Ethernet ethernetInfo = new Ethernet();
        packet.getHeader(ethernetInfo);

        macs[0] = FormatUtils.mac(ethernetInfo.source());
        macs[1] = FormatUtils.mac(ethernetInfo.destination());

        return macs;
    }

    public String getTypeDescription() {
        switch (this.type) {
            case BITTORRENT_TRAFFIC:
                return "BITTORRENT TRAFFIC";
            case UDP_TRAFFIC:
                return "SUSPICIOUS UDP TRAFFIC";
        }

        return "UNKNOW";
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
