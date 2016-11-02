package ist.csf.snifftorrent;

import java.io.Serializable;
import java.util.Date;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;

/**
 * Created by JCSF on 02/11/2016.
 */
public class PacketInfo implements Serializable {

    /* ---| INFRACTIONS TYPE |--- */
    public static final int BITTORRENT_HANDSHAKE = 0;
    public static final int BITTORRENT_PROTOCOL = 1;

    /* ---| CLASS VARIABLES |--- */
    private int infraction_type;
    private String infractor_IP, infractor_MAC;
    private Date timeStamp;
    private PcapPacket packet;

    public PacketInfo (int type, PcapPacket packet) {
        this.infraction_type = type;
        this.packet = packet;

        Ethernet ethernetInfo = new Ethernet();
        Ip4 ipInfo = new Ip4();

        packet.getHeader(ipInfo);
        packet.getHeader(ethernetInfo);

        infractor_IP = FormatUtils.ip(ipInfo.source());
        infractor_MAC = FormatUtils.mac(ethernetInfo.source());

        timeStamp = new Date(packet.getCaptureHeader().timestampInMillis());
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
                + "Packet Details:\n" + packet.toString() + "\n-----------------------------------\n";
    }

    private String getInfractionTypeDescription() {
        switch (this.infraction_type) {
            case 0:
                return "BITTORRENT HANDSHAKE";
            case 1:
                return "BITTORRENT PROTOCOL";
        }

        return "UNKNOW";
    }

}
