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

	/*
	*	Function: main()
	*
	*	In params:
	*			- String[] args: Array containing all arguments passed in parameters by user.
	*
	*	Description: Main function of class Client
	*/
	public static void main(String[] args) {
		Client client = new Client();
		client.run(args);
	}
	
	/* Declaration of 4 serverStubs, 4 being the maximum number of servers used in this experiment */
	private ServerInterface[] serverStubs = new ServerInterface[4];
	
	/* Declaration of number of data */
	private int length;

	/* Array of type Data containing all the data from the text file */
	public Data[] data;


	/* Declaration of threads for multithreading */
	Thread[] myThreads;

	/* Array containing the results from the computing servers */
	Result[] results;


	/*
	*	Function: super()
	*
	*	Description: Calls the parent constructor with no arguments.
	*/
	public Client() {
		super();
	}

	/*
	*	Function: loadServerStub()
	*
	*	In params:
	*			- String hostname: IP adress of the server
	*
	*	Returns: Server stub of class ServerInterface
	*
	*	Description: Connects to a server and loads a server stub.
	*/
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
	
	/*
	*	Function: loadData()
	*
	*	In params:
	*			- String fileName: Name of the file containing the operations.
	*
	*	Description: Opens the text file and loads its data into the array data of type Data.
	*/
	private void loadData(String fileName){

		try{
			String line = null;
			String[] tokens;
			int index = 0;			
			data = new Data[length];

			/* Relative path of file */
			String filePath = "operations/" + fileName;

			/* Open text file */
			FileReader fileReader = new FileReader(filePath);
			/* Wrap FileReader in BufferedReader */
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while((line = bufferedReader.readLine()) != null){
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

	/*
	*	Function: distribution()
	*
	*	In params:
	*			- Data[][] subLists: 2D Array containing the lists of data that will be sent to each server.
	*			- int nbServers: Number of servers.
	*
	*	Description: Computes the sub lists to distribution according to number of computing servers active.
	*/
	private void distribution(Data[][] subLists, int nbServers){

		int totalData = data.length;
		/* Number of data */
		int remainingSize = totalData;
		/* Number of data to send to current server */
		int currentSize = 0;
		/* Maximum capacities of each server */
		int[] capacities = new int[nbServers];
		/* Total capacities of all servers */
		int totalCapacities = 0;

		try{
			/* Retrieve capacity data from servers */
			for(int i = 0; i < nbServers; i++){
				capacities[i] = serverStubs[i].getWorkCapacity();
				totalCapacities += capacities[i];
			}
		} catch(RemoteException e){
			System.err.println("Error getting work capacities or servers: " + e.getMessage());
		}

		/* Distribution function: Total data * (capacity(i) / (sum(capacities))) */
		for(int i = 0; i < subLists.length-1; i++){
			currentSize = (int)(capacities[i] / totalCapacities);
			subLists[i] = new Data[currentSize];
			subLists[i] = Arrays.copyOfRange(data, totalData - remainingSize, currentSize);
			remainingSize -= currentSize;
		}
		subLists[nbServers-1] = new Data[remainingSize];
		subLists[nbServers-1] = Arrays.copyOfRange(data, totalData - remainingSize, currentSize);
		
		/* Function call to send the lists to each server */
		sendToServers(subLists, nbServers);
	}

	/*
	*	Function: sendToServers()
	*
	*	In params:
	*			- Data[][] subLists: 2D Array containing the lists of data that will be sent to each server.
	*			- int nbServers: Number of servers.
	*
	*	Description: Sends each sub list to its appropriate to server.
	*/
	private void sendToServers(Data[][] subLists, int nbServers){

		results = new Result[nbServers];

		/* For each server, send data in a different thread */
		for(int i = 0; i < nbServers; i++){
			myThreads[i] = new Thread(){
				public void run(){
					try{
						/* Sends data and receives the computing result in results[i] */
						for(int i = 0; i < nbServers; i++){
							results[i] = serverStubs[i].sendWork(subLists[i]);
						}
						/* Wait for thread to finish */
						for(int i = 0; i < nbServers; i++){
							myThreads[i].join();
						}
					} catch(RemoteException e){

						System.err.println("Error sending data to servers: " + e.getMessage());
						distribution(subLists, nbServers);

					} catch(InterruptedException e){

						System.err.println("Error receiving server results: " + e.getMessage());
					}
				}
			};
			/* Activates thread */
			myThreads[i].start();
		}

		/* Verify sub results validity */
		verifyResults(subLists, nbServers);	
	}

	/*
	*	Function: verifyResults()
	*
	*	In params:
	*			- Data[][] subLists: 2D Array containing the lists of data that will be sent to each server.
	*			- int nbServers: Number of servers.
	*
	*	Description: Verifies if the sub results are all valid/accepted. If not, returns to distribution function.
	*/
	private void verifyResults(Data[][] subLists, int nbServers){
		
		for(int i = 0; i < nbServers; i++){
			if(results[i].accepted == false){
				distribution(subLists, nbServers);
			}
		}
	}

	/*
	*	Function: finalAnswer()
	*
	*	In params:
	*			- int nbServers: Number of servers.
	*
	*	Description: Computes the final results, applies the final modulo 4000 to avoid overflow and displays it to the user.
	*/
	private void finalAnswer(int nbServers){

		long finalResult = 0;

		for(int i = 0; i < nbServers; i++){
			finalResult += results[i].value;
		}

		finalResult = finalResult%4000;

		System.out.println("Resultat du calcul: " + finalResult);
	}

	/*
	*	Function: run()
	*
	*	In params:
	*			- Strings[] args: Array of strings, being the parameters passed by the user on terminal
	*
	*	Description: Runs the dispatcher and its different methods for this experiment.
	*/
	public void run(String[] args){

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		
		/* If the file name is specified, load its data */
		if(args.length >= 1){	
			loadData(args[0]);
		}

		/* Define the mode */
		boolean modeSecured;
		if(args.length >= 2){
			if(args[1] == "s"){modeSecured = true;}
			else if(args[1] == "n"){modeSecured = false;}
		}

		/* Number of computing servers */
		int nbServers = args.length - 2;
		myThreads = new Thread[nbServers];

		/* Server stubs loading */
		for(int i = 0; i < nbServers; i++){
			serverStubs[i] = loadServerStub(args[i+2]);
		}
		
		/* Sublists to split task between the servers */
		Data[][] subLists = new Data[nbServers][];

		/* Determine work redistribution (need to keep doing that whenever something fails?)*/
		distribution(subLists, nbServers);

		/* Display final answer */
		finalAnswer(nbServers);
	}
	
}