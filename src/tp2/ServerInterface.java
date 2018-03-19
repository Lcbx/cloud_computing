package tp2;
import java.rmi.Remote;
import java.rmi.RemoteException;

import java.util.*;

/* Object for the operations in the file */
class Data{
	String name;
	int value;
}

class Result{
	boolean accepted;
	int value;
}

public interface ServerInterface extends Remote {
	public int getWorkCapacity() throws RemoteException;
	public Result sendWork(Data[] data) throws RemoteException;
}