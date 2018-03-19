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
import tp2.Operations;

public class Server implements ServerInterface {

	public static void main(String[] args) {
		if(arg.length < 4){
			System.out.println("expected arguments : serverName, port, workCapacity, maliciousness");
			return;
		}
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
	java.util.Random random;
	
	public Server(String serverName, int serverPort, int workCapacity, float maliciousness) {
		super();
		name = serverName;
		port = serverPort;
		Q = workCapacity;
		m = maliciousness;
		random = new Random();
	}
	
	@Override
	public int getWorkCapacity(){
		return Q;
	}
	
	@Override
	public Result sendWork(Data[] data){
		
		Result result = new Result();
		
		int u = data.length;
		float chanceToDefect= ((float) (u-Q)) /Q;
		
		if (random.nextFloat() < chanceToDefect){
			result.accepted = false; 
		}
		else{
			
			result.accepted = true;
			
			if (random.nextFloat() < m){
				result.value = (random.nextInt()%4000);
			}
			else {
				result.value = execute(data);
			}
		}
		
		return result;
	}
	
	private int execute(Data[] operations){
		int result = 0;
		for(Data operation : operations){
			switch(operation.name){
				case "prime":{
					result += (Operations.prime(operation.value)%4000);
					break;
			}
				case "pell":{
					result += (Operations.pell(operation.value)%4000);
					break;
				}
			}
		}
		return result;
	}
	
	
	
	
}



















