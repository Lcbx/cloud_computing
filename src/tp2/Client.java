package tp2;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.*;
import java.io.*;

import tp2.ServerInterface;


public class Client {

	public static void main(String[] args) {
		Client client = new Client();
		client.run(args);
	}
	
	private ServerInterface[] serverStubs = new ServerInterface[4];
	
	private int nbData = 100;

	public Data[] data = new Data[nbData];


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
	
	private void loadData(String fileName){

		try{
			String line = null;
			String[] tokens;
			int index = 0;
			String filePath = "operations/" + fileName;

			/* Open text file */
			FileReader fileReader = new FileReader(filePath);
			/* Wrap FileReader in BufferedReader */
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while(((line = bufferedReader.readLine()) != null) && index < nbData){
				/* Split the name of operation and value */
				tokens = line.split(" ");
				if(tokens.length != 2){throw new IllegalArgumentException();}
				/* Store them in the object attributes as String and Int */
				data[index] = new Data();
				data[index].name = tokens[0];
				data[index].value = Integer.parseInt(tokens[1]);

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

	private void distribution(Data[][] subLists, int nbServers){
		int sizeDataList = data.length;
		int remainingSize = sizeDataList;
		int currentSize = 0;

		for(int i = 0; i < subLists.length-1; i++){
			currentSize = (sizeDataList + 1)/nbServers;
			subLists[i] = new Data[currentSize];
			remainingSize -= currentSize;
		}
		subLists[nbServers-1] = new Data[remainingSize];

		for(int i = 0; i < nbServers; i++){
			serverStubs[i].sendWork(subLists[i]);
		}
	}

	public void run(String[] args){
		
		
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		
		/* If the file name is specified */
		if(args.length >= 1){
			loadData(args[0]);
		}
		int nbServers = args.length - 1;

		for(int i = 0; i < nbServers; i++){
			serverStubs[i] = loadServerStub(args[i+1]);
		}
		
		Data[][] subLists = new Data[nbServers][];

		/* Determine work redistribution (need to keep doing that whenever something fails?)*/
		/* Send Data */
		distribution(subLists, nbServers);
		/* Receive Data */
		/* Display final answer */
	}
	
}
