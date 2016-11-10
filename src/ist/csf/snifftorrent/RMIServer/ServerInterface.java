package ist.csf.snifftorrent.RMIServer;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileNotFoundException;

import ist.csf.snifftorrent.classes.PacketInfo;

public interface ServerInterface extends Remote {

    // GENERIC METHODS
    ArrayList<PacketInfo> getPacketInfoList(int list) throws RemoteException;
    ArrayList<PacketInfo> getPacketsFilteringType(int list, String type) throws RemoteException;
    ArrayList<PacketInfo> getPacketsFilteringInfIP(int list, String ip) throws RemoteException;
    ArrayList<PacketInfo> getPacketsFilteringInfMAC(int list, String mac) throws RemoteException;
    ArrayList<PacketInfo> getPacketsFilteringTCPUDP(int list, String type) throws RemoteException;
    PacketInfo getPacketInfo(int list, int hash) throws RemoteException;

    // LIVE PACKETS METHODS
    void insertPacketInfo(PacketInfo info) throws RemoteException;
    void deletePacketInfo(int hash) throws RemoteException;

    // SAVED PACKETS METHODS
    void savePacketInfo(int hash) throws RemoteException, FileNotFoundException, IOException;
    void unSavePacketInfo(int hash) throws RemoteException, FileNotFoundException, IOException;
}
