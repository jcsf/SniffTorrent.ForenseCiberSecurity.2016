package ist.csf.snifftorrent.RMIServer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import ist.csf.snifftorrent.classes.PacketInfo;

public class Server extends UnicastRemoteObject implements ServerInterface{
    ArrayList<PacketInfo> infoPackets;

    protected Server() throws RemoteException {
        this.infoPackets = new ArrayList<>();
    }

    public static void main(String[] args) throws RemoteException {
        System.getProperties().put("java.security.policy", "policy.all");
        System.setProperty("java.rmi.server.hostname","localhost");
        ServerInterface server = new Server();
        LocateRegistry.createRegistry(1099).rebind("server", server);
        System.out.println("Server Ready...");
    }

    @Override
    public ArrayList<PacketInfo> getPacketInfoList() throws RemoteException {
        return this.infoPackets;
    }

    @Override
    public ArrayList<PacketInfo> getPacketsFilteringType(String type) throws RemoteException {
        ArrayList<PacketInfo> filtered = new ArrayList<>();

        for (int i = 0; i < this.infoPackets.size(); i++) {
            if (this.infoPackets.get(i).getInfractionTypeDescription().toLowerCase().contains(type.toLowerCase())) {
                filtered.add(this.infoPackets.get(i));
            }
        }

        return filtered;
    }

    @Override
    public ArrayList<PacketInfo> getPacketsFilteringInfIP(String ip) throws RemoteException {
        ArrayList<PacketInfo> filtered = new ArrayList<>();

        for (int i = 0; i < this.infoPackets.size(); i++) {
            if (this.infoPackets.get(i).getInfractor_IP().toLowerCase().contains(ip.toLowerCase())) {
                filtered.add(this.infoPackets.get(i));
            }
        }

        return filtered;
    }

    @Override
    public ArrayList<PacketInfo> getPacketsFilteringInfMAC(String mac) throws RemoteException {
        ArrayList<PacketInfo> filtered = new ArrayList<>();

        for (int i = 0; i < this.infoPackets.size(); i++) {
            if (this.infoPackets.get(i).getInfractor_MAC().toLowerCase().contains(mac.toLowerCase())) {
                filtered.add(this.infoPackets.get(i));
            }
        }

        return filtered;
    }

    @Override
    public PacketInfo getPacketInfo(int hash) throws RemoteException {
        for (int i = 0; i < this.infoPackets.size(); i++) {
            if (this.infoPackets.get(i).getHash() == hash) {
                return this.infoPackets.get(i);
            }
        }

        return null;
    }

    @Override
    public void insertPacketInfo(PacketInfo info) throws RemoteException {
        this.infoPackets.add(info);
    }

    @Override
    public int getTest() throws RemoteException {
        return 1;
    }

}
