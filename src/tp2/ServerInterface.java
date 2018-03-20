package tp2;
import java.rmi.Remote;
import java.rmi.RemoteException;

import java.util.*;

/* Object for the operations in the file */
class Data{
	String name;
	int value;
}



public interface ServerInterface extends Remote {
	public int getWorkCapacity() throws RemoteException;
	public AbstractMap.SimpleEntry<Boolean, Integer> sendWork(Data[] data) throws RemoteException; // accepted work, result
}