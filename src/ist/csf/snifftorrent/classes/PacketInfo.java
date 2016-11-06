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

    /* ---| CLASS VARIABLES |--- */
    private int infraction_type;
    private String infractor_IP, infractor_MAC;
    private Date timeStamp;
    private byte [] packet;

    public PacketInfo (int type, PcapPacket packet) {
        byte [] packetBytes = new byte [packet.getTotalSize()];
        packet.transferStateAndDataTo(packetBytes);

        this.infraction_type = type;
        this.packet = packetBytes;

        Ethernet ethernetInfo = new Ethernet();
        Ip4 ipInfo = new Ip4();

        packet.getHeader(ipInfo);
        packet.getHeader(ethernetInfo);

        infractor_IP = FormatUtils.ip(ipInfo.source());
        infractor_MAC = FormatUtils.mac(ethernetInfo.source());

        timeStamp = new Date(packet.getCaptureHeader().timestampInMillis());
    }

    public String getInfractor_IP() { return this.infractor_IP; }

    public String getInfractor_MAC() { return this.infractor_MAC; }

    public Date getTimeStamp() { return this.timeStamp; }

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
            case 0:
                return "BITTORRENT HANDSHAKE";
            case 1:
                return "BITTORRENT PROTOCOL";
        }

        return "UNKNOW";
    }

}
