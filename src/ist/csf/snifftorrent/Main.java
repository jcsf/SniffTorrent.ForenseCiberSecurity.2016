package ist.csf.snifftorrent;

import java.util.ArrayList;
import java.util.List;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

public class Main {

    public static void main(String[] args) {
        List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs
        StringBuilder errbuf = new StringBuilder(); // For any error msg
        Information user = new Information();

        /***************************************************************************
         * GET LIST OF DEVICES
         **************************************************************************/
        int r = Pcap.findAllDevs(alldevs, errbuf);
        if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
            System.err.printf("Can't read list of devices, error is %s", errbuf.toString());
            return;
        }

        System.out.println("Network devices found:");

        int i = 0;
        for (PcapIf device : alldevs) {
            String description = (device.getDescription() != null) ? device.getDescription() : "No description available";
            System.out.printf("#%d: %s [%s]\n", i++, device.getName(), description);
        }

        PcapIf device = alldevs.get(0); // We know we have atleast 1 device
        System.out.printf("\nChoosing '%s' on your behalf:\n", (device.getDescription() != null) ? device.getDescription() : device.getName());

        /***************************************************************************
         * OPEN DEVICE TO LISTEN
         **************************************************************************/
        int snaplen = 64 * 1024;           // Capture all packets, no trucation
        int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
        int timeout = 10 * 1000;           // 10 seconds in millis
        Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);

        if (pcap == null) {
            System.err.printf("Error while opening device for capture: " + errbuf.toString());
            return;
        }

        /***************************************************************************
         * LISTEN PACKAGES
         **************************************************************************/
        PcapPacketHandler<Information> jpacketHandler = new PcapPacketHandler<Information>() {

            public void nextPacket(PcapPacket packet, Information user) {
                PacketInfo pInfo = null;
                //System.out.println(packet.toString());

                if (BitTorrentFilter.filterHandshake(packet)) { // DETECT A TORRENT HANDSHAKE
                    pInfo = new PacketInfo(PacketInfo.BITTORRENT_HANDSHAKE, packet);
                } else if (BitTorrentFilter.filterBittorentProtocol(packet)) { // DETECT BITTORRENT PROTOCOL
                    pInfo = new PacketInfo(PacketInfo.BITTORRENT_PROTOCOL, packet);
                }

                if (pInfo != null) {
                    System.out.println(pInfo.toString());
                    // TODO: SEND PACKET INFO TO SERVER
                }
            }
        };

        pcap.loop(Pcap.LOOP_INFINITE, jpacketHandler, user);

        pcap.close();

    }
}

class Information {
    private int counter;

    public Information () {
        this.counter = 0;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getCounter() {
        return this.counter;
    }
}
