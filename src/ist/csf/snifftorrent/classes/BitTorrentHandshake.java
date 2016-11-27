package ist.csf.snifftorrent.classes;

import org.jnetpcap.packet.PcapPacket;

public class BitTorrentHandshake extends PacketInfo {

    private int pstrLen;
    private String pstr;
    private String reservedBytes;
    private String infoHash;
    private String peerId;

    public BitTorrentHandshake (PcapPacket packet) {
        super(PacketInfo.BITTORRENT_HANDSHAKE, packet);

        String content;
        if (this.getPacketType().equals("TCP")) {
            content = getHexRawFromPackage(getPacket()).substring(108);
        } else {
            content = getHexRawFromPackage(getPacket()).substring(124);
        }

        String pstrHexLen = content.substring(0, 2);
        this.pstrLen = Integer.parseInt(pstrHexLen, 16);
        int endPstr = (this.pstrLen + 1) * 2;

        this.pstr = content.substring(2, endPstr);
        this.reservedBytes = content.substring(endPstr, endPstr + 8 * 2);
        this.infoHash = content.substring(endPstr + 8 * 2, endPstr + 8 * 2 + 20 * 2);
        this.peerId = content.substring(endPstr + 8 * 2 + 20 * 2);
    }

    public int getPstrLen() { return pstrLen; }

    public String getPstr() {
        return BitTorrentHandshake.convertStringHEXToASCII(this.pstr);
    }

    public String getReservedBytes() {
        return this.reservedBytes;
    }

    public String getInfoHash() { return infoHash; }

    public String getPeerId() { return this.peerId; }

    public String getClientSoftwareID() {
        String clientID = BitTorrentHandshake.convertStringHEXToASCII(this.peerId);

        return clientID.substring(0, 8);
    }

    @Override
    public String getHTMLTypeLayout() {
        return "<p class=\"timeline-collapse\"><b>PACKET CONTENT</b></p>\n" +
                "<p class=\"timeline-collapse\"><b>Protocol Name Length: </b>" + getPstrLen() + "</p>\n" +
                "<p class=\"timeline-collapse\"><b>Protocol Name: </b>" + getPstr()  + "</p>\n" +
                "<p class=\"timeline-collapse\"><b>Reserved Bytes: </b>" + getReservedBytes() + "</p>\n" +
                "<p class=\"timeline-collapse\"><b>Info Hash: </b>" + getInfoHash() + "</p>\n" +
                "<p class=\"timeline-collapse\"><b>Peer ID: </b>" + getPeerId() + "</p>\n" +
                "<p class=\"timeline-collapse\"><b>Client Software ID: </b>" + getClientSoftwareID() + "</p>\n";
    }

    public static String convertStringHEXToASCII(String hex) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hex.length(); i+=2) {
            String str = hex.substring(i, i+2);
            output.append((char)Integer.parseInt(str, 16));
        }
        return output.toString();
    }
}
