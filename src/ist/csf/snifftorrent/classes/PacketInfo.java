package ist.csf.snifftorrent.classes;

import java.io.Serializable;
import java.util.Date;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;

public class PacketInfo implements Serializable {

    /* ---| INFRACTIONS TYPE |--- */
    public static final int BITTORRENT_HANDSHAKE = 0;
    public static final int BITTORRENT_PROTOCOL = 1;
    public static final int UTORRENT_PACKAGE = 2;

    /* ---| CLASS VARIABLES |--- */
    private int infraction_type;
    private String infractor_IP, infractor_MAC;
    private Date timeStamp;
    private byte [] packet;
    private int packetHash;

    public PacketInfo (int type, PcapPacket packet) {
        byte [] packetBytes = new byte [packet.getTotalSize()];
        packet.transferStateAndDataTo(packetBytes);

        this.infraction_type = type;
        this.packet = packetBytes;

        Ethernet ethernetInfo = new Ethernet();
        Ip4 ipInfo = new Ip4();

        packet.getHeader(ipInfo);
        packet.getHeader(ethernetInfo);

        this.infractor_IP = FormatUtils.ip(ipInfo.source());
        this.infractor_MAC = FormatUtils.mac(ethernetInfo.source());

        this.timeStamp = new Date(packet.getCaptureHeader().timestampInMillis());

        this.packetHash = java.util.Arrays.hashCode(packetBytes);
    }

    public int getInfractionType() { return this.infraction_type; };

    public String getInfractor_IP() { return this.infractor_IP; }

    public String getInfractor_MAC() { return this.infractor_MAC; }

    public Date getTimeStamp() { return this.timeStamp; }

    public int getHash() {return this.packetHash; }

    public PcapPacket getPacket() {
        return new PcapPacket (this.packet);
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

}
