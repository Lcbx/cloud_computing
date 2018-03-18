package tp2;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.*;
import java.io.*;

import tp2.ServerInterface;

/* Object for the operations in the file */
class Operations{
	String name;
	int value;
}

public class Client {

	public static void main(String[] args) {
		Client client = new Client();
		client.run(args);
	}
	
	private ServerInterface serverStub1 = null;
	private ServerInterface serverStub2 = null;
	private ServerInterface serverStub3 = null;
	private ServerInterface serverStub4 = null;
	
	private int nbOperations = 100;

	private Operations[] operations = new Operations[nbOperations];


	public Client() {
		super();
	}

	private ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;
		try {
			
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (ServerInterface) registry.lookup("server");
			
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage() 	+ "' n'est pas défini dans le registre.");
		} catch (AccessException e) {
			System.err.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.err.println("Erreur: " + e.getMessage());
		}
		return stub;
	}
	
	private void loadOperations(String fileName){

		try{
			String line = null;
			String[] tokens;
			int index = 0;
			String filePath = "operations/" + fileName;

			/* Open text file */
			FileReader fileReader = new FileReader(filePath);
			/* Wrap FileReader in BufferedReader */
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while(((line = bufferedReader.readLine()) != null) && index < nbOperations){
				/* Split the name of operation and value */
				tokens = line.split(" ");
				if(tokens.length != 2){throw new IllegalArgumentException();}
				/* Store them in the object attributes as String and Int */
				operations[index] = new Operations();
				operations[index].name = tokens[0];
				operations[index].value = Integer.parseInt(tokens[1]);

				index++;
				
			}

			/* Close text file */
			bufferedReader.close();

		}
		catch(FileNotFoundException e){
			System.err.println("Error opening file: " + e.getMessage());
		} 
		catch(IOException e){
			System.err.println("Error reading operations from file: " + e.getMessage());
		}
	}
	
	public void run(String[] args){
		
		
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		
		/* If the file name is specified */
		if(args.length >= 1){
			loadOperations(args[0]);
		}
		/* If we're only using 1 server for computing */
		if(args.length >= 2){
			serverStub1 = loadServerStub(args[1]);
		}
		/* If we're using 2 servers for computing */
		if(args.length >= 3){			
			serverStub2 = loadServerStub(args[2]);
		}
		/* If we're using 3 servers for computing */
		if(args.length >= 4){
			serverStub3 = loadServerStub(args[3]);
		}
		/* If we're using 4 servers for computing */
		if(args.length >= 5){
			serverStub4 = loadServerStub(args[4]);
		}
		
		/* Determine work redistribution (need to keep doing that whenever something fails?)*/
		/* Send Data */
		/* Receive Data */
		/* Display final answer */
	}
	
}
