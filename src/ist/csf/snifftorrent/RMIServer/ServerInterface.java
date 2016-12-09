package ist.csf.snifftorrent.RMIServer;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileNotFoundException;

import ist.csf.snifftorrent.classes.*;

public interface ServerInterface extends Remote {

    // GENERIC METHODS
    ArrayList<Connection> getConnectionList(int list) throws RemoteException;
    ArrayList<Connection> getPacketsFilteringInfIP(int list, String ip) throws RemoteException;
    ArrayList<Connection> getPacketsFilteringInfMAC(int list, String mac) throws RemoteException;

    // GENERIC CONNECTIONS METHODS
    Connection getConnection(int list, int hash) throws RemoteException;

    // LIVE CONNECTION METHODS
    void insertPacketInfo(PacketInfo info) throws RemoteException, IOException;
    void deleteConnection(int hashConnection) throws RemoteException;

    // SAVED PACKETS METHODS
    void saveConnection(int hash) throws IOException;
    void unSaveConnection(int hash) throws IOException;

    // INSIDE CONNECTION METHODS
    PacketInfo getPacketInfo(int list, int hashConnection, int hash) throws RemoteException;
    ArrayList<PacketInfo> getPacketsFilteringType(int list, int hashConnection, String type) throws RemoteException;
    ArrayList<PacketInfo> getPacketsFilteringTCPUDP(int list, int hashConnection, String type) throws RemoteException;
    void deletePacketInfo(int list, int hashConnection, int hashPacket) throws RemoteException, IOException;

    //UDP BEHAVIOUR TRAFFIC
    void insertUDPPacket(PacketInfo info) throws RemoteException, IOException;
    void checkOldUDPTraffic() throws RemoteException;
}
