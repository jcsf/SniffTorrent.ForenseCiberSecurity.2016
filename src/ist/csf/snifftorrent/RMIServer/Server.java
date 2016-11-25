package ist.csf.snifftorrent.RMIServer;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import ist.csf.snifftorrent.classes.*;

public class Server extends UnicastRemoteObject implements ServerInterface{
    public static final int LIVE = 0;
    public static final int SAVED = 1;

    ArrayList<Connection> liveConnections;
    ArrayList<Connection> savedConnections;

    ArrayList<PacketInfo> livePackets;
    ArrayList<PacketInfo> savedPackets;


    protected Server() throws RemoteException {
        this.livePackets = new ArrayList<>();
        this.liveConnections = new ArrayList<>();

        try {
            this.savedPackets = (ArrayList<PacketInfo>) readFileToList("savedPackets");
            this.savedConnections = (ArrayList<Connection>) readFileToList("savedConnections");
        } catch (Exception e) {
            e.printStackTrace();
            this.savedPackets = new ArrayList<>();
            this.savedConnections = new ArrayList<>();
            System.out.println("No files to read. Creating New Files...");
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
    public ArrayList<Connection> getConnectionList(int list) throws RemoteException {
        ArrayList<Connection> conns = getList(list);
        return conns;
    }

    @Override
    public ArrayList<Connection> getPacketsFilteringInfIP(int list, String ip) throws RemoteException {
        ArrayList<Connection> conns = getList(list);
        ArrayList<Connection> filtered = new ArrayList<>();

        for (int i = 0; i < conns.size(); i++) {
            if (conns.get(i).getInfractorIP().toLowerCase().contains(ip.toLowerCase())) {
                filtered.add(conns.get(i));
            }
        }

        return filtered;
    }

    @Override
    public ArrayList<Connection> getPacketsFilteringInfMAC(int list, String mac) throws RemoteException {
        ArrayList<Connection> conns = getList(list);
        ArrayList<Connection> filtered = new ArrayList<>();

        for (int i = 0; i < conns.size(); i++) {
            if (conns.get(i).getInfractorMAC().toLowerCase().contains(mac.toLowerCase())) {
                filtered.add(conns.get(i));
            }
        }

        return filtered;
    }

    @Override
    public Connection getConnection(int list, int hash) throws RemoteException {
        ArrayList<Connection> connections = getList(list);

        for (int i = 0; i < connections.size(); i++) {
            if (connections.get(i).getHash() == hash) {
                return connections.get(i);
            }
        }

        return null;
    }

    // LIVE CONNECTION METHODS

    @Override
    public void insertPacketInfo(PacketInfo info) throws RemoteException, IOException {
        Connection connection = null;
        int list = searchConnectionWithPacket(info, connection);

        connection.addPacketToTimeline(info);

        if(list == -1) {
            this.liveConnections.add(connection);
        } else if (list == SAVED) {
            this.writeListToFile("savedConnections");
        }
    }

    @Override
    public void deleteConnection(int hashConnection) throws RemoteException {
        this.liveConnections.remove(this.getConnection(LIVE, hashConnection));
    }

    private int searchConnectionWithPacket(PacketInfo info, Connection con) {

        for (int k = 0; k < 2; k++) {
            ArrayList<Connection> cons = getList(k);

            for (int i = 0; i < cons.size(); i++) {
                if (info.getSourceIP().equals(cons.get(i).getInfractorIP()) && info.getDestinationIP().equals(cons.get(i).getOutsideIP())) {
                    con = cons.get(i);
                    return k;
                } else if (info.getSourceIP().equals(cons.get(i).getOutsideIP()) && info.getDestinationIP().equals(cons.get(i).getInfractorIP())) {
                    con = cons.get(i);
                    return k;
                }
            }
        }

        con = Connection.createConnection(info);
        return -1;
    }

    // SAVED PACKETS METHODS

    @Override
    public void saveConnection(int hash) throws RemoteException, FileNotFoundException, IOException {
        Connection con = this.getConnection(LIVE, hash);

        this.savedConnections.add(con);
        this.writeListToFile("savedConnections");
        this.liveConnections.remove(con);
    }

    @Override
    public void unSaveConnection(int hash) throws RemoteException, FileNotFoundException, IOException {
        Connection con = this.getConnection(LIVE, hash);

        this.savedPackets.remove(con);
        this.writeListToFile("savedConnections");
    }

    private ArrayList <Connection> getList(int list) {
        if (list == LIVE) {
            return this.liveConnections;
        } else {
            return this.savedConnections;
        }
    }

    private Object readFileToList(String filename) throws FileNotFoundException, IOException, ClassNotFoundException {
        Object lpi;

        // CREATE SAVE FOLDER
        String folder = System.getProperty("user.home") + "\\SniffTorrent";

        // DO ACTION
        FileInputStream fin = new FileInputStream(folder + "\\" + filename + ".snifftorrent");
        ObjectInputStream ois = new ObjectInputStream(fin);
        lpi = ois.readObject();
        ois.close();
        fin.close();

        return lpi;
    }

    private void writeListToFile(String filename) throws FileNotFoundException, IOException {
        // CREATE SAVE FOLDER
        String folder = System.getProperty("user.home") + "\\SniffTorrent";

        // DO ACTION
        FileOutputStream fout = new FileOutputStream(folder + "\\" + filename + ".snifftorrent");
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(this.savedPackets);
        oos.close();
        fout.close();
    }

    // INSIDE CONNECTION METHODS

    @Override
    public PacketInfo getPacketInfo(int list, int hashConnection, int hash) throws RemoteException {
        ArrayList<PacketInfo> infoPackets = getConnection(list, hashConnection).getTimeline();

        for (int i = 0; i < infoPackets.size(); i++) {
            if (infoPackets.get(i).getHash() == hash) {
                return infoPackets.get(i);
            }
        }

        return null;
    }

    @Override
    public ArrayList<PacketInfo> getPacketsFilteringType(int list, int hashConnection, String type) throws RemoteException {
        ArrayList<PacketInfo> infoPackets = getConnection(list, hashConnection).getTimeline();
        ArrayList<PacketInfo> filtered = new ArrayList<>();

        for (int i = 0; i < infoPackets.size(); i++) {
            if (infoPackets.get(i).getInfractionTypeDescription().toLowerCase().contains(type.toLowerCase())) {
                filtered.add(infoPackets.get(i));
            }
        }

        return filtered;
    }

    @Override
    public ArrayList<PacketInfo> getPacketsFilteringTCPUDP(int list, int hashConnection, String type) throws RemoteException {
        ArrayList<PacketInfo> infoPackets = getConnection(list, hashConnection).getTimeline();
        ArrayList<PacketInfo> filtered = new ArrayList<>();

        for (int i = 0; i < infoPackets.size(); i++) {
            if (infoPackets.get(i).getPacketType().toLowerCase().contains(type.toLowerCase())) {
                filtered.add(infoPackets.get(i));
            }
        }

        return filtered;
    }


    @Override
    public void deletePacketInfo(int list, int hashConnection, int hashPacket) throws RemoteException, IOException {
        Connection con = this.getConnection(list, hashConnection);
        ArrayList<PacketInfo> timeline = con.getTimeline();

        timeline.remove(getPacketInfo(list, hashConnection, hashPacket));

        if (list == SAVED) {
            this.writeListToFile("savedConnections");
        }
    }
}
