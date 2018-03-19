package tp2;

import java.rmi.ConnectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


import java.util.*;
import java.io.*;

import tp2.ServerInterface;

public class Server implements ServerInterface {

	public static void main(String[] args) {
		Server server = new Server(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Float.parseFloat(args[3]) );
		server.run();
	}

	private void run() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
		
			ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(this, 0);

			Registry registry = LocateRegistry.getRegistry(port);
			registry.rebind(name, stub);
			System.out.println("Server ready.");
		
		
		} catch (ConnectException e) {
			
			System.err.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
		
		
	}
	
	String name;
	int port;
	int Q;
	float m;
	
	public Server(String serverName, int serverPort, int workCapacity, float maliciousness) {
		super();
		name = serverName;
		port = serverPort;
		Q = workCapacity;
		m = maliciousness;
	}
	
	@Override
	public int getWorkCapacity(){
		return Q;
	}
	
	@Override
	public Result sendWork(Data[] data){
		return new Result();
	}
}
