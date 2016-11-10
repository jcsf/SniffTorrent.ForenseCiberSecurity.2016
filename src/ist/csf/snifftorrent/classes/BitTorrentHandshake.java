package ist.csf.snifftorrent.classes;

public class BitTorrentHandshake {

    private int pstrLen;
    private String pstr;
    private String reservedBytes;
    private String infoHash;
    private String peerId;

    public BitTorrentHandshake (String hexData) {
        String content = hexData.substring(108);
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

    public static String convertStringHEXToASCII(String hex) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hex.length(); i+=2) {
            String str = hex.substring(i, i+2);
            output.append((char)Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    public String getHTMLLayout() {
        return "<p class=\"lead\"><b>PACKET CONTENT</b></p>\n" +
                "<p class=\"lead\"><b>Protocol Name Length: </b>" + getPstrLen() + "</p>\n" +
                "<p class=\"lead\"><b>Protocol Name: </b>" + getPstr()  + "</p>\n" +
                "<p class=\"lead\"><b>Reserved Bytes: </b>" + getReservedBytes() + "</p>\n" +
                "<p class=\"lead\"><b>Info Hash: </b>" + getInfoHash() + "</p>\n" +
                "<p class=\"lead\"><b>Peer ID: </b>" + getPeerId() + "</p>\n" +
                "<p class=\"lead\"><b>Client Software ID: </b>" + getClientSoftwareID() + "</p>\n" +
                "<hr class=\"my-2\">\n";
    }
}