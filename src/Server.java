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
		Server server = new Server();
		server.run();
	}

	private void run() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
		
			ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(this, 0);

			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("server", stub);
			System.out.println("Server ready.");
		
		
		} catch (ConnectException e) {
			
			System.err.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
		
		
	}
	
	public Server() {
		super();
	}

}
