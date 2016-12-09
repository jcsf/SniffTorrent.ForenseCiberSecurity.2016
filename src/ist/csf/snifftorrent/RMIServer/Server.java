package ist.csf.snifftorrent.RMIServer;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import ist.csf.snifftorrent.classes.*;

public class Server extends UnicastRemoteObject implements ServerInterface{
    public static final int LIVE = 0;
    public static final int SAVED = 1;

    private ArrayList<Connection> liveConnections;
    private ArrayList<Connection> savedConnections;
    private ArrayList<Connection> udpTraffic;

    private ServerProperties properties;

    protected Server() throws RemoteException {
        this.liveConnections = new ArrayList<>();
        this.udpTraffic = new ArrayList<>();

        try {
            this.savedConnections = (ArrayList<Connection>) readObjectFromFile("savedConnections");
        } catch (Exception e) {
            //e.printStackTrace();
            this.savedConnections = new ArrayList<>();
            System.out.println("No files to read. Creating New Files...");
            try {
                writeObjectToFile("savedConnections", this.savedConnections);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        try {
            this.properties = (ServerProperties) readObjectFromFile("config");
        } catch (Exception e) {
            //e.printStackTrace();
            this.properties = new ServerProperties(5, 1000); //5 Minutes, 1000 Packets
            System.out.println("No Properties to read. Using Default Properties...");
            try {
                writeObjectToFile("config", this.properties);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws RemoteException {
        System.getProperties().put("java.security.policy", "policy.all");
        System.setProperty("java.rmi.server.hostname","localhost");
        ServerInterface server = new Server();
        LocateRegistry.createRegistry(1099).rebind("server", server);
        new UDPTrafficCleaner(TimeUnit.MINUTES.toMillis(1), server);
        System.out.println("Server Ready...");
    }

    @Override
    public ArrayList<Connection> getConnectionList(int list) throws RemoteException {
        ArrayList<Connection> conns = getList(list);
        return conns;
    }

    @Override
    public ArrayList<Connection> getConnectionsFilteringInfIP(int list, String ip) throws RemoteException {
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
    public ArrayList<Connection> getConnectionsFilteringInfMAC(int list, String mac) throws RemoteException {
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
    public ArrayList<Connection> getConnectionsFilteringType(int list, String type) throws RemoteException {
        ArrayList<Connection> conns = getList(list);
        ArrayList<Connection> filtered = new ArrayList<>();

        for (int i = 0; i < conns.size(); i++) {
            if (conns.get(i).getTypeDescription().toLowerCase().contains(type.toLowerCase())) {
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
        Connection connection = searchConnectionWithPacket(info);

        connection.addPacketToTimeline(info);

        if(!this.liveConnections.contains(connection) && !this.savedConnections.contains(connection)) {
            this.liveConnections.add(connection);
        } else if (this.savedConnections.contains(connection)) {
            this.writeObjectToFile("savedConnections", this.savedConnections);
        }
    }

    @Override
    public void deleteConnection(int hashConnection) throws RemoteException {
        this.liveConnections.remove(this.getConnection(LIVE, hashConnection));
    }

    private Connection searchConnectionWithPacket(PacketInfo info) {

        for (int k = 0; k < 2; k++) {
            ArrayList<Connection> cons = getList(k);

            for (int i = 0; i < cons.size(); i++) {
                if (info.getSourceIP().equals(cons.get(i).getInfractorIP()) && info.getDestinationIP().equals(cons.get(i).getOutsideIP())) {
                    return cons.get(i);
                } else if (info.getSourceIP().equals(cons.get(i).getOutsideIP()) && info.getDestinationIP().equals(cons.get(i).getInfractorIP())) {
                    return cons.get(i);
                }
            }
        }

        return Connection.createConnection(Connection.BITTORRENT_TRAFFIC, info);
    }

    // SAVED PACKETS METHODS

    @Override
    public void saveConnection(int hash) throws RemoteException, FileNotFoundException, IOException {
        Connection con = this.getConnection(LIVE, hash);

        this.savedConnections.add(con);
        this.writeObjectToFile("savedConnections", this.savedConnections);
        this.liveConnections.remove(con);
    }

    @Override
    public void unSaveConnection(int hash) throws RemoteException, FileNotFoundException, IOException {
        Connection con = this.getConnection(SAVED, hash);

        this.savedConnections.remove(con);
        this.writeObjectToFile("savedConnections", this.savedConnections);
    }

    private ArrayList <Connection> getList(int list) {
        if (list == LIVE) {
            return this.liveConnections;
        } else {
            return this.savedConnections;
        }
    }

    private Object readObjectFromFile(String filename) throws FileNotFoundException, IOException, ClassNotFoundException {
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

    private void writeObjectToFile(String filename, Object obj) throws FileNotFoundException, IOException {
        // CREATE SAVE FOLDER
        String folder = System.getProperty("user.home") + "\\SniffTorrent";

        // DO ACTION
        FileOutputStream fout = new FileOutputStream(folder + "\\" + filename + ".snifftorrent");
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(obj);
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
            this.writeObjectToFile("savedConnections", this.savedConnections);
        }
    }

    //UDP BEHAVIOUR TRAFFIC
    @Override
    public void insertUDPPacket(PacketInfo info) throws RemoteException, IOException {
        Connection connection = searchConnectionWithUDPPacket(info);

        if (connection.getUDPPacketCounter() <= properties.numberOfPackets) {
            connection.addPacketToTimeline(info);
        } else {
            connection.increaseUDPPacketCounter(info);
        }

        if(!this.udpTraffic.contains(connection)) {
            this.udpTraffic.add(connection);
        }

        if(connection.getTimeline().size() > properties.numberOfPackets && !this.liveConnections.contains(connection) && !this.savedConnections.contains(connection)) {
            PacketInfo first = connection.getTimeline().get(0);
            PacketInfo last = connection.getTimeline().get(connection.getTimeline().size()-1);
            connection.setUDPPacketCounter();
            connection.getTimeline().clear();
            connection.getTimeline().add(first);
            connection.getTimeline().add(last);

            this.liveConnections.add(connection);
        }
    }

    private Connection searchConnectionWithUDPPacket(PacketInfo info) {

        for (int i = 0; i < this.udpTraffic.size(); i++) {
            if (info.getSourceIP().equals(this.udpTraffic.get(i).getInfractorIP())) {
                return this.udpTraffic.get(i);
            } else if (info.getDestinationIP().equals(this.udpTraffic.get(i).getInfractorIP())) {
                return this.udpTraffic.get(i);
            }
        }

        return Connection.createConnection(Connection.UDP_TRAFFIC, info);
    }

    @Override
    public void checkOldUDPTraffic() throws RemoteException{
        ArrayList <Connection> toRemove = new ArrayList<>();
        Date now = new Date();

        for (int i = 0; i < this.udpTraffic.size(); i++) {
            Date timestampAfter10Min = new Date (this.udpTraffic.get(i).getTimeline().get(0).getTimeStamp().getTime() + this.properties.timeWindow);
            if(now.after(timestampAfter10Min)) {
                toRemove.add(this.udpTraffic.get(i));
            }
        }

        for (int i = 0; i < toRemove.size(); i++) {
            this.udpTraffic.remove(toRemove.get(i));
        }
    }

    @Override
    public ServerProperties getServerProperties() throws RemoteException {
        return this.properties;
    }

    @Override
    public void changeServerProperties(long timeWindowInMinutes, int numberOfPackages) throws RemoteException {
        this.properties.timeWindow =  TimeUnit.MINUTES.toMillis(timeWindowInMinutes);
        this.properties.numberOfPackets = numberOfPackages;
        try {
            writeObjectToFile("config", this.properties);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}

//Thread to clean old udp traffic detected
class UDPTrafficCleaner extends Thread {

    long timeSleepMS;
    ServerInterface db;

    public UDPTrafficCleaner (long timeSleepMS, ServerInterface db) {
        this.timeSleepMS = timeSleepMS;
        this.db = db;
        this.start();
    }

    //=============================
    public void run(){
        while(true) {
            try {
                Thread.sleep(timeSleepMS);
                db.checkOldUDPTraffic();
            } catch (Exception e) {}
        }
    }
}
