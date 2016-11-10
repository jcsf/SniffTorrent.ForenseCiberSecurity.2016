package ist.csf.snifftorrent.filterAgent;

import java.util.ArrayList;
import java.util.List;

import java.rmi.Naming;
import java.rmi.RemoteException;

import com.sun.org.apache.xpath.internal.SourceTree;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import ist.csf.snifftorrent.RMIServer.ServerInterface;
import ist.csf.snifftorrent.classes.*;

public class Main {
    public static ServerInterface server = null;

    public static void main(String[] args) {
        List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs
        StringBuilder errbuf = new StringBuilder(); // For any error msg

        try {
            server = (ServerInterface) Naming.lookup("rmi://localhost:1099/server");
        } catch (Exception e) {
            System.out.println("ERRO [Server]: " + e);
            e.printStackTrace();
            return;
        }

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
        PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() {

            public void nextPacket(PcapPacket packet, String user) {
                PacketInfo pInfo = null;

                if (BitTorrentFilter.filterHandshake(packet)) { // DETECT A TORRENT HANDSHAKE
                    pInfo = new PacketInfo(PacketInfo.BITTORRENT_HANDSHAKE, packet);
                } else if (BitTorrentFilter.filterUTorrentPackage(packet)) { // DETECT UTORRENT PACKAGES
                    pInfo = new PacketInfo(PacketInfo.UTORRENT_PACKAGE, packet);
                } else if (BitTorrentFilter.filterBittorentProtocol(packet)) { // DETECT BITTORRENT PROTOCOL
                    pInfo = new PacketInfo(PacketInfo.BITTORRENT_PROTOCOL, packet);
                }

                if (pInfo != null) {
                    System.out.println(pInfo.toString());
                    // TODO: SEND PACKET INFO TO SERVER
                    try {
                        Main.server.insertPacketInfo(pInfo);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        pcap.loop(Pcap.LOOP_INFINITE, jpacketHandler, "Listen");

        pcap.close();

    }
}