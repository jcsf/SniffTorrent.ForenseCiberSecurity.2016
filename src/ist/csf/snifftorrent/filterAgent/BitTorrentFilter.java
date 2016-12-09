package ist.csf.snifftorrent.filterAgent;

import ist.csf.snifftorrent.classes.PacketInfo;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.tcpip.Tcp;

public class BitTorrentFilter {

    /* BITTORRENT HANDSHAKE VARIABLES */
    private static final String bittorrentHandshakeSignature = "13426974546f7272656e742070726f746f636f6c";

    /* BITTORRENT PROTOCOL VARIABLES */
    private static final String bittorrentProtocolSignature = "546f7272656e74";

    /* UTORRENT PACKETS VARIABLES */
    private static final String utorrentPacketsSignature = "75546f7272656e74";

    /* VUZETORRENT PACKET VARIABLES */
    private static final String vuzePacketsSignature = "6c6962746f7272656e745f6c6561702f";

    /*DELUGE PACKET VARIABLES*/
    private static final String delugePacketsSignature = "44656c756765";

    public static boolean filterHandshake (PcapPacket packet) {
        String rawHexData = PacketInfo.getHexRawFromPackage(packet);
        if (rawHexData.contains(bittorrentHandshakeSignature)) {
            return true;
        }

        return false;
    }

    public static boolean filterBittorentProtocol (PcapPacket packet) {
        String rawHexData = PacketInfo.getHexRawFromPackage(packet);
        if (rawHexData.contains(bittorrentProtocolSignature)) {
            return true;
        }

        return false;
    }

    public static boolean filterUTorrentPackage (PcapPacket packet) {
        String rawHexData = PacketInfo.getHexRawFromPackage(packet);
        if (rawHexData.contains(utorrentPacketsSignature)) {
            return true;
        }

        return false;
    }

    public static boolean filterVuzeTorrentPackage (PcapPacket packet) {
        String rawHexData = PacketInfo.getHexRawFromPackage(packet);
        if (rawHexData.contains(vuzePacketsSignature)) {
            return true;
        }

        return false;
    }

    public static boolean filterDelugeTorrentPackage (PcapPacket packet) {
        String rawHexData = PacketInfo.getHexRawFromPackage(packet);
        if (rawHexData.contains(delugePacketsSignature)) {
            return true;
        }

        return false;
    }

}