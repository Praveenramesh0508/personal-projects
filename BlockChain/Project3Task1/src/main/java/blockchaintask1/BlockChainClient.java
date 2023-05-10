/**
 * Author: Praveen Ramesh
 * Andrew ID: pramesh2@andrew.cmu.edu
 The BlockChainClient class is a client program that communicates with a BlockChainServer
 over a socket connection.Through this program, users can see the fundamental blockchain
 state, add a transaction, verify the blockchain, view the blockchain, corrupt the chain,
 and then cover up the corruption by repairing the chain.The program uses user input to decide
 what action to take before sending a request message via a socket connection to the server.
 The request is handled by the server, which then replies with a message that the client receives
 and sees on the console. The Gson library is utilized by the Java application for JSON
 serialization and deserialization.
 */
//import the required packages
package blockchaintask1;
import com.google.gson.Gson;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
//Referred my project2Task4 code for TCP
public class BlockChainClient {
    //declare a scanner object
    static Scanner scanner ;
    //declare a socket
    static Socket clientSocket;

    /**
     * The BlockChainClient class main method is in charge of initializing the client socket
     * and corresponding with the server.The action to be taken is determined by user input,
     * and the socketCommunication() method is used to contact the server.
     * When the user enters 6 to end the program, the while loop in this method continues to execute.
     * @param args
     */

    public static void main(String args[])
    {
        try {
            //declare a sort
            int serverPort;
            //create a scanner object
            scanner = new Scanner(System.in);
            System.out.println("The BlockChain client is running");
            System.out.print("Enter the BlockChainServer port number:");
            //get the server port
            serverPort = Integer.parseInt(scanner.nextLine());
            //Create a socket for connection
            clientSocket = new Socket("localhost", serverPort);
            System.out.println("The BlockChain client is running");
            //declare the choice
            int choice;
            //infinite loop till the user exists
            while (true) {
                //display the menu options
                System.out.println("0. View basic blockchain status.\n" +
                        "\n" +
                        "1. Add a transaction to the blockchain.\n" +
                        "\n" +
                        "2. Verify the blockchain.\n" +
                        "\n" +
                        "3. View the blockchain.\n" +
                        "\n" +
                        "4. Corrupt the chain.\n" +
                        "\n" +
                        "5. Hide the corruption by repairing the chain.\n" +
                        "\n" +
                        "6. Exit.");
                //get the choice from the user
                choice = Integer.parseInt(scanner.nextLine());
                //call the socketCommunication if the choice is not 6 to perform the required operation
                if (choice!=6) {
                    socketCommunication(choice);
                }
                //exit if the choice is 6
                else
                {   //exit the program
                    System.exit(0);
                }
            }
        }
        //catch clause
        catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * socketCommunication method is responsible for communicating with the server
     * through the client socket.It takes an integer choice as an input, which
     * determines the action to be performed by the server.The method creates a
     * RequestMessage object based on the user input and sends it to the server.
     * It then receives a JSON response from the server and calls the displayResults()
     * method to display the response to the user.
     * @param choice An integer which determines the action to be performed by the server.
     */
    public static void socketCommunication(int choice)
    {
        try {
            //create a requestMessage object with the choice
            RequestMessage requestMessage= createRequestMessage(choice);
            //create a BufferedReader object for reading the message
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            //create a PrintWriter object for writing the  message to server
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
            //write the requestMessage
            out.println(requestMessage.getRequestMessage());
            out.flush();
            //get the response JSON from the server
            String responseJSONFromServer = in.readLine();
            //call the displayResults method with the response JSON
            displayResults(responseJSONFromServer);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
     }

    /*
     *createRequestMessage method creates a RequestMessage object based on the user's choice.
     *The user's choice determines the type of RequestMessage to create.
     * @param choice
     * @return RequestMessage object
     */
    public static RequestMessage  createRequestMessage(int choice)
    {
        //declare a requestMessage object
        RequestMessage requestMessage=null;
        if (choice==0){
            //create an object with the choice if the choice is 0
            requestMessage=new RequestMessage(String.valueOf(choice));
        }
        if (choice==1){
            //declare difficulty
            int enteredDifficulty;
            //declare transaction
            String transaction;
            //get the difficulty and transaction from the user
            System.out.println("Enter difficulty > 0");
            enteredDifficulty=Integer.parseInt(scanner.nextLine());
            System.out.println("Enter transaction");
            transaction=scanner.nextLine();
            //create a RequestMessage object using choice,entered difficulty and transaction
            requestMessage=new RequestMessage(String.valueOf(choice),enteredDifficulty,transaction);
        }
        //if the choice is 2 then create a RequestMessage object with the choice
        if (choice==2){
            requestMessage=new RequestMessage(String.valueOf(choice));
        }
        //if the choice is 3 then create a RequestMessage object with the choice
        if (choice==3){
            requestMessage=new RequestMessage(String.valueOf(choice));
        }
        if (choice==4) {
            System.out.println("corrupt the Blockchain");
            System.out.println("Enter block ID of block to corrupt");
            //get the corruptID from the user
            int CorruptID=Integer.parseInt(scanner.nextLine());
            //get the new transaction for the block
            System.out.println("Enter new data for block "+CorruptID);
            String newTransaction=scanner.nextLine();
            //create a RequestMessage object using choice, newTransaction and CorruptID
            requestMessage=new RequestMessage(String.valueOf(choice),newTransaction,CorruptID);
        }
        //if the choice is 3 then create a RequestMessage object with the choice
        if(choice==5){
            requestMessage=new RequestMessage(String.valueOf(choice));
        }
        //return the requestMessage object
        return requestMessage;
    }

    /**
     * displayResults method displays the results received from the server.
     * @param responseJSONFromServer a JSON string containing the response message from the server.
     */
     public static void displayResults(String responseJSONFromServer)
     {  //declare a selection variable
         int selection;
         //create a new Gson object
         Gson gson=new Gson();
         //convert the responseJSONFromServer string to ResponseMessage object
         ResponseMessage messageFromServer = gson.fromJson(responseJSONFromServer,ResponseMessage.class);
         //get the selection from getter method
         selection= Integer.parseInt(messageFromServer.getSelection());
         if (selection==0){
             //if the se;ection is 0 then use the corresponding getters to display the results
             System.out.println("Current size of chain: "+messageFromServer.getSize());
             System.out.println("Difficulty of most recent block: "+messageFromServer.getDiff());
             System.out.println("Total difficulty for all blocks: "+ messageFromServer.getTotalDiff());
             System.out.println("Approximate hashes per second on this machine: "+ messageFromServer.getHps());
             System.out.println("Expected total hashes required for the whole chain: "+ messageFromServer.getTotalHashes());
             System.out.println("Nonce for most recent block: "+ messageFromServer.getRecentNonce());
             System.out.println("Chain hash: "+messageFromServer.getChainHash());
         }

         if (selection==1){
             //directly print the response message from the server
            System.out.println(messageFromServer.getResponse());
         }
         if(selection==2){
             //directly print the response message from the server
             System.out.println(messageFromServer.getResponse());
         }
         if(selection==3){
             //directly print the response message from the server
             System.out.println("View the Blockchain");
             System.out.println(messageFromServer.getResponse());
         }
         if(selection==4){
             //directly print the response message from the server
             System.out.println(messageFromServer.getResponse());
         }
         if(selection==5){
             //directly print the response message from the server
             System.out.println(messageFromServer.getResponse());
         }
     }
}
