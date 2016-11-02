package ist.csf.snifftorrent;

import org.jnetpcap.packet.PcapPacket;
/**
 * Created by JCSF on 02/11/2016.
 */
public class BitTorrentFilter {

    /* BITTORRENT HANDSHAKE VARIABLES */
    private static final String bittorrentHandshakeSignature = "13426974546f7272656e742070726f746f636f6c";
    private static final int bittorrentHandshakeLength = 122;

    /* BITTORRENT PROTOCOL VARIABLES */
    private static final String bittorrentProtocolSignature = "546f7272656e74";

    public static boolean filterHandshake (PcapPacket packet) {
        String rawHexData = getHexRawFromPackage(packet);
        if (packet.getCaptureHeader().caplen() == bittorrentHandshakeLength && rawHexData.contains(bittorrentHandshakeSignature)) {
            return true;
        }

        return false;
    }

    public static boolean filterBittorentProtocol (PcapPacket packet) {
        String rawHexData = getHexRawFromPackage(packet);
        if (rawHexData.contains(bittorrentProtocolSignature)) {
            return true;
        }

        return false;
    }



    private static String getHexRawFromPackage(PcapPacket packet) {
        String packetHexDump = packet.toHexdump(1024, false, false, true);

        return packetHexDump.replaceAll("\\s+","");
    }

}
