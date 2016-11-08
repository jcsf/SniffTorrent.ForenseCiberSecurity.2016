package ist.csf.snifftorrent.RMIServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import ist.csf.snifftorrent.classes.PacketInfo;

public interface ServerInterface extends Remote {
    ArrayList<PacketInfo> getPacketInfoList() throws RemoteException;
    ArrayList<PacketInfo> getPacketsFilteringType(String type) throws RemoteException;
    ArrayList<PacketInfo> getPacketsFilteringInfIP(String ip) throws RemoteException;
    ArrayList<PacketInfo> getPacketsFilteringInfMAC(String mac) throws RemoteException;
    PacketInfo getPacketInfo(int hash) throws RemoteException;
    void insertPacketInfo(PacketInfo info) throws RemoteException;
    int getTest() throws RemoteException;
}
