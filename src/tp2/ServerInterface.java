package tp2;
import java.rmi.Remote;
import java.rmi.RemoteException;

import java.util.*;

public interface ServerInterface extends Remote {
	public int getWorkCapacity() throws RemoteException;
	public AbstractMap.SimpleEntry<Boolean, Integer> sendWork(AbstractMap.SimpleEntry<String, Integer>[] data) throws RemoteException; // accepted work, result
}