package ist.csf.snifftorrent.RMIServer;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import ist.csf.snifftorrent.classes.PacketInfo;

public class Server extends UnicastRemoteObject implements ServerInterface{
    public static final int LIVE_PACKETS = 0;
    public static final int SAVED_PACKETS = 1;

    ArrayList<PacketInfo> livePackets;
    ArrayList<PacketInfo> savedPackets;

    protected Server() throws RemoteException {
        this.livePackets = new ArrayList<>();

        try {
            this.savedPackets = readFileToList();
        } catch (Exception e) {
            e.printStackTrace();
            this.savedPackets = new ArrayList<>();
        }
    }

    public static void main(String[] args) throws RemoteException {
        System.getProperties().put("java.security.policy", "policy.all");
        System.setProperty("java.rmi.server.hostname","localhost");
        ServerInterface server = new Server();
        LocateRegistry.createRegistry(1099).rebind("server", server);
        System.out.println("Server Ready...");
    }

    @Override
    public ArrayList<PacketInfo> getPacketInfoList(int list) throws RemoteException {
        ArrayList<PacketInfo> infoPackets = getList(list);
        return infoPackets;
    }

    @Override
    public ArrayList<PacketInfo> getPacketsFilteringType(int list, String type) throws RemoteException {
        ArrayList<PacketInfo> infoPackets = getList(list);
        ArrayList<PacketInfo> filtered = new ArrayList<>();

        for (int i = 0; i < infoPackets.size(); i++) {
            if (infoPackets.get(i).getInfractionTypeDescription().toLowerCase().contains(type.toLowerCase())) {
                filtered.add(infoPackets.get(i));
            }
        }

        return filtered;
    }

    @Override
    public ArrayList<PacketInfo> getPacketsFilteringInfIP(int list, String ip) throws RemoteException {
        ArrayList<PacketInfo> infoPackets = getList(list);
        ArrayList<PacketInfo> filtered = new ArrayList<>();

        for (int i = 0; i < infoPackets.size(); i++) {
            if (infoPackets.get(i).getInfractor_IP().toLowerCase().contains(ip.toLowerCase())) {
                filtered.add(infoPackets.get(i));
            }
        }

        return filtered;
    }

    @Override
    public ArrayList<PacketInfo> getPacketsFilteringInfMAC(int list, String mac) throws RemoteException {
        ArrayList<PacketInfo> infoPackets = getList(list);
        ArrayList<PacketInfo> filtered = new ArrayList<>();

        for (int i = 0; i < infoPackets.size(); i++) {
            if (infoPackets.get(i).getInfractor_MAC().toLowerCase().contains(mac.toLowerCase())) {
                filtered.add(infoPackets.get(i));
            }
        }

        return filtered;
    }

    @Override
    public ArrayList<PacketInfo> getPacketsFilteringTCPUDP(int list, String type) throws RemoteException {
        ArrayList<PacketInfo> infoPackets = getList(list);
        ArrayList<PacketInfo> filtered = new ArrayList<>();

        for (int i = 0; i < infoPackets.size(); i++) {
            if (infoPackets.get(i).getPacketType().toLowerCase().contains(type.toLowerCase())) {
                filtered.add(infoPackets.get(i));
            }
        }

        return filtered;
    }

    @Override
    public PacketInfo getPacketInfo(int list, int hash) throws RemoteException {
        ArrayList<PacketInfo> infoPackets = getList(list);

        for (int i = 0; i < infoPackets.size(); i++) {
            if (infoPackets.get(i).getHash() == hash) {
                return infoPackets.get(i);
            }
        }

        return null;
    }

    @Override
    public void insertPacketInfo(PacketInfo info) throws RemoteException {
        this.livePackets.add(info);
    }

    @Override
    public void deletePacketInfo(int hash) throws RemoteException {
        this.livePackets.remove(this.getPacketInfo(LIVE_PACKETS, hash));
    }

    // SAVED PACKETS METHODS

    @Override
    public void savePacketInfo(int hash) throws RemoteException, FileNotFoundException, IOException {
        PacketInfo pInfo = this.getPacketInfo(LIVE_PACKETS, hash);

        this.savedPackets.add(pInfo);
        this.writeListToFile();
        this.livePackets.remove(pInfo);
    }

    @Override
    public void unSavePacketInfo(int hash) throws RemoteException, FileNotFoundException, IOException {
        PacketInfo pInfo = this.getPacketInfo(SAVED_PACKETS, hash);

        this.savedPackets.remove(pInfo);
        this.writeListToFile();
    }

    private ArrayList <PacketInfo> getList(int list) {
        if (list == LIVE_PACKETS) {
            return this.livePackets;
        } else {
            return this.savedPackets;
        }
    }

    private ArrayList <PacketInfo> readFileToList() throws FileNotFoundException, IOException, ClassNotFoundException {
        ArrayList <PacketInfo> lpi;

        // CREATE SAVE FOLDER
        String folder = System.getProperty("user.home") + "\\SniffTorrent";

        // DO ACTION
        FileInputStream fin = new FileInputStream(folder + "\\saved.packets");
        ObjectInputStream ois = new ObjectInputStream(fin);
        lpi = (ArrayList<PacketInfo>) ois.readObject();
        ois.close();
        fin.close();

        return lpi;
    }

    private void writeListToFile() throws FileNotFoundException, IOException {
        // CREATE SAVE FOLDER
        String folder = System.getProperty("user.home") + "\\SniffTorrent";

        // DO ACTION
        FileOutputStream fout = new FileOutputStream(folder + "\\saved.packets");
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(this.savedPackets);
        oos.close();
        fout.close();
    }
}
