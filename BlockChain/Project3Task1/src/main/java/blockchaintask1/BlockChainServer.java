/**
 * Author: Praveen Ramesh
 * Andrew ID: pramesh2@andrew.cmu.edu
 This is a server program for a blockchain. It listens on a port for incoming
 connections and processes requests from the client. The requests are in the form of JSON strings that are
 parsed and processed to execute different operations of blockchain. The program contains methods to
 process different types of requests such as viewing blockchain status, adding a new block, verifying the entire
 chain, viewing the entire chain, corrupting the chain, and repairing the chain. The program uses the Gson library
 to parse the JSON strings and convert Java objects to JSON strings. The program uses a BlockChain object to
 represent the blockchain and its functionalities.
 */
//import the required packages
package blockchaintask1;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import static blockchaintask1.BlockChain.getTime;
//Referred my project2Task4 code for TCP
public class BlockChainServer {
    //declare a clientSocket
    static Socket clientSocket = null;
    //declare a ServerSocket
    static ServerSocket listenSocket=null;
    //declare a port
    static int port;
    //declare a scanner object
    static Scanner scanner;
    //declare a blockChain object to create a chain
    static BlockChain blockChain;
    //declare and initialize index to 0
    static int index=0;
    //declare a genesis block
    static Block genesis;
    //declare a GSON object
    static Gson gson;
    //declare a GSON's JSONObject
    static JsonObject json;
    //declare a ResponseMessage object
    static ResponseMessage responseMessage;
    //declare a RequestMessage to get the message from the  client
    static RequestMessage messageFromClient;

    /**
     * The main method sets up a server socket to start listening on a user-specified port.
     * The blockChain object is then initialized, the hash rate is calculated, and the genesis block is created.
     * After that, a while loop starts up and continuously checks for new client connections.
     * The processRequest method is called by the server after reading the client's request message during a connection.
     * The response message that has been generated is sent back to the client. Up to a manual shutdown of the program,
     * this process keeps running.
     */
    public static void main(String[] args) {
        try {
            //create a scanner object
            scanner=new Scanner(System.in);
            //get the port number from the user to listen
            System.out.print("Enter the port number for the server to listen:");
            port = Integer.parseInt(scanner.nextLine());
            //create a ServerSocket object with the port
            listenSocket= new ServerSocket(port);
            System.out.println("Blockchain server running");
            //create new BlockChain object
            blockChain=new BlockChain();
            //compute the hashesPerSecond by the machine
            blockChain.computeHashesPerSecond();
            //create a new genesis block
            genesis=new Block(0,getTime(),"Genesis",2);
            //add 1 to the index
            index=index+1;
            //set the previousHash to empty string
            genesis.setPreviousHash("");
            //add the genesis block to the chain
            blockChain.addBlock(genesis);
            //infinite loop
            while (true) {
                //accept connections from client
                clientSocket = listenSocket.accept();
                System.out.println("We have a visitor");
                Scanner in;
                //create a new scanner object to get the message from the client
                in = new Scanner(clientSocket.getInputStream());
                //declare a printWriter onject to send the message to the client
                PrintWriter out;
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
                while (in.hasNextLine())
                {   //get the JSON request message from the client
                    String requestJSON = in.nextLine();
                    //call processRequest() to process the message and do the operations on blockchain
                    String responseJSON = processRequest(requestJSON);
                    //send the JSON response to the client
                    out.println(responseJSON);
                    out.flush();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *processRequest method processes the JSON request message from the client by first parsing it
     * into a RequestMessage object using the Gson library. The selection value in the request is
     * used to determine the operation to be performed on the blockchain.
     * The appropriate operation method is then called with the selection value as an argument.The JSON
     * returned by the called methods is sent back to the main method.
     * @param requestJSON
     * @return JSON response
     */
    public static String processRequest(String requestJSON)
    {   //create a GSON object
        gson=new Gson();
        //parse the JSON to RequestMessage object named as messageFromClient
        messageFromClient = gson.fromJson(requestJSON,RequestMessage.class);
        //get the selection from the messageFromClient object
        int selection = Integer.parseInt(messageFromClient.getSelection());
        //if the selection is 0 call processViewBlockChainStatus method
        if(selection==0){
            return processViewBlockChainStatus(selection);
        }
        //if the selection is 1 then call processAddBlock method
        if(selection==1){
            return processAddBlock(selection);
        }
        //if the selection is 2 then call processVerifyBlockChain method
        if(selection==2){
            return processVerifyBlockChain(selection);
        }
        //if the selection is 3 then call processViewBlockChain method
        if(selection==3){
            return processViewBlockChain(selection);
        }
        //if the selection is 4 then call processCorruptBlockChain method
        if(selection==4){
            return processCorruptBlockChain(selection);
        }
        //if the selection is 5 then call processRepairBlockChain method
        if(selection==5){
            return processRepairBlockChain(selection);
        }
        //else return null
        return null;
    }

    /**
     * processViewBlockChainStatus method processes the request to view the current status of the
     * blockchain network, including the number of blocks in the chain, the current difficulty level,
     * and the latest block attributes etc.
     * @param selection
     * @return JSON response string
     */
    public static String  processViewBlockChainStatus(int selection)
    {
        //create a ResponseMessage object with all the attributes of the blockchain
        responseMessage=new ResponseMessage(String.valueOf(selection),blockChain.getChainSize(),blockChain.getChainHash(),(int)blockChain.getTotalExpectedHashes(),blockChain.getTotalDifficulty(),blockChain.nonceMostRecent().intValue(), blockChain.difficultyMostRecent(),blockChain.getHashesPerSecond());
        //parse the JSON string returned by the getResponseMessage to JSONObject
        json = gson.fromJson(responseMessage.getResponseMessage(), JsonObject.class);
        //create a new JsonObject to put the required attributes
        JsonObject reponseToCLient = new JsonObject();
        //add selection
        reponseToCLient.addProperty("selection", json.get("selection").getAsString());
        //add size
        reponseToCLient.addProperty("size", json.get("size").getAsInt());
        //add chainHash
        reponseToCLient.addProperty("chainHash", json.get("chainHash").getAsString());
        //add totalHashes
        reponseToCLient.addProperty("totalHashes", json.get("totalHashes").getAsInt());
        //add totalDiff
        reponseToCLient.addProperty("totalDiff", json.get("totalDiff").getAsInt());
        //add recentNonce
        reponseToCLient.addProperty("recentNonce", json.get("recentNonce").getAsInt());
        //add diff
        reponseToCLient.addProperty("diff", json.get("diff").getAsInt());
        //add hps
        reponseToCLient.addProperty("hps", json.get("hps").getAsInt());
        //print the response format
        System.out.println("Response :" +reponseToCLient);
        //return the JSON string of reponseToCLient
        return reponseToCLient.toString();
    }

    /**
     * processAddBlock parse the JSON request to get the required data to create a block and add the
     * block to the blockchain through addblock() method . It creates a ResponseMessage object and parse it to JSON message to send
     * it back to the client
     * @param selection
     * @return JSON response
     */
    public static String processAddBlock(int selection)
    {
        //Adding a block
        System.out.println("Adding a block");
        //declare difficulty
        int difficulty;
        //declare transaction
        String transaction;
        //get the difficulty from the messageFromClient object
        difficulty=messageFromClient.getEnteredDifficulty();
        //get the transaction from the messageFromClient object
        transaction=messageFromClient.getTransaction();
        //get the start time
        long start = System.currentTimeMillis();
        //create a new block with the data from the client
        Block block= new Block(index,getTime(),transaction,difficulty);
        //increment the index
        index=index+1;
        //add the block to the chain
        blockChain.addBlock(block);
        //get the stop time
        long stop = System.currentTimeMillis();
        //create a response string
        String addResult="Total execution time to add this block was "+(stop-start)+" milliseconds";
        System.out.println("Setting response to "+addResult);
        //create a ResponseMessage with the selection and addResult response string
        responseMessage=new ResponseMessage(String.valueOf(selection),addResult);
        //convert the ResponseMessage object to JSONObject
        json = gson.fromJson(responseMessage.getResponseMessage(), JsonObject.class);
        //create a new JSONObject to put the required values to send it back to the client
        JsonObject reponseToCLient = new JsonObject();
        //add selection
        reponseToCLient.addProperty("selection", json.get("selection").getAsString());
        //add response
        reponseToCLient.addProperty("response", json.get("response").getAsString());
        //print the response
        System.out.println(reponseToCLient);
        //return the response JSON
        return reponseToCLient.toString();
    }

    /**
     *processVerifyBlockChain method calls blockChain.isChainValid() method to verify the chain
     * and create a ResponseMessage object with the response string and parse it to JSON to send it
     * back to the client
     * @param selection
     * @return JSON string
     */
    public static String processVerifyBlockChain(int selection)
    {
        System.out.println("Verifying entire chain");
        //get the start time
        long start = System.currentTimeMillis();
        //call the isChainValid() method to verify the chain
        String varificationResult = "Chain Verification : "+ blockChain.isChainValid();
        //get the stop time
        long stop = System.currentTimeMillis();
        //create a varificationResult response string
        varificationResult=varificationResult+"\nTotal execution time to verify the chain was "+(stop-start)+" milliseconds";
        System.out.println(varificationResult);
        System.out.println("Setting response to "+"Total execution time to verify the chain was "+(stop-start)+" milliseconds");
        //create a ResponseMessage object with the varificationResult string
        responseMessage=new ResponseMessage(String.valueOf(selection),varificationResult);
        //parse the responseMessage object to a JsonObject
        json = gson.fromJson(responseMessage.getResponseMessage(), JsonObject.class);
        //create a new JsonObject to put the required values to the JSON
        JsonObject reponseToCLient = new JsonObject();
        //add selection
        reponseToCLient.addProperty("selection", json.get("selection").getAsString());
        //add response string
        reponseToCLient.addProperty("response", json.get("response").getAsString());
        //retur the JSON in the string format
        return reponseToCLient.toString();
    }

    /** processViewBlockChain method call the blockChain.toString() to get the JSON format of the chain.
     * It then create a ResponseMessage object porse it to a JSONObject and returns the JSON response
     * @param selection
     * @return JSON string
     */
    public static String processViewBlockChain(int selection)
    {
        System.out.println("View the Blockchain");
        //calls to toString() method
        String viewResult= blockChain.toString();
        System.out.println("Setting response to "+viewResult);
        //create a ResponseMessage object
        responseMessage=new ResponseMessage(String.valueOf(selection),viewResult);
        //parse it to JSONObject
        json = gson.fromJson(responseMessage.getResponseMessage(), JsonObject.class);
        //create a new JsonObject object to put the required fields
        JsonObject reponseToCLient = new JsonObject();
        //add selection
        reponseToCLient.addProperty("selection", json.get("selection").getAsString());
        //add response
        reponseToCLient.addProperty("response", json.get("response").getAsString());
        //return JSON string
        return reponseToCLient.toString();
    }

    /**
     * processCorruptBlockChain method parses the JSON request to get the required data to corrput a block.
     * Calls the setData() mehtod to set the new transaction and returns a JSON response
     * @param selection
     * @return JSON string
     */

    public static String processCorruptBlockChain(int selection)
    {
        System.out.println("corrupt the Blockchain");
        //get the block ID to corrupt
        int ID= messageFromClient.getCorruptID();
        // get the new transaction
        String newData=messageFromClient.getNewTransaction();
        //set the new transaction
        blockChain.getBlock(ID).setData(newData);
        //create a response string called corruptResult
        String corruptResult ="Block "+ID+" now holds "+newData;
        System.out.println(corruptResult);
        //create a new ResponseMessage object with the selection and corruptResult string
        responseMessage=new ResponseMessage(String.valueOf(selection),corruptResult);
        //parse it to JSONObject
        json = gson.fromJson(responseMessage.getResponseMessage(), JsonObject.class);
        //create a new JSONObject to add the required fields
        JsonObject reponseToCLient = new JsonObject();
        System.out.println("Setting response to "+corruptResult);
        //add selection
        reponseToCLient.addProperty("selection", json.get("selection").getAsString());
        //add response
        reponseToCLient.addProperty("response", json.get("response").getAsString());
        //return the JSON string format
        return reponseToCLient.toString();
    }

    /**
     * processRepairBlockChain method call the blockchain's repairChain() method to perform the
     * repair operation. It then creates a ResponseMessage and parse it to JSON and return the JSON
     * @param selection
     * @return JSON string
     */
    public static String processRepairBlockChain(int selection)
    {
        System.out.println("Repairing the entire chain");
        //get the start time
        long start = System.currentTimeMillis();
        //call the repairChain() method
        blockChain.repairChain();
        //get the stop time
        long stop = System.currentTimeMillis();
        //create a repairMessage string to set the reponse
        String repairMessage= "Total execution time to repair the chain was "+(stop-start)+" milliseconds";
        //create a ResponseMessage onject with the selection and repairMessage string
        responseMessage=new ResponseMessage(String.valueOf(selection),repairMessage);
        //parse it tp JSONObject
        json = gson.fromJson(responseMessage.getResponseMessage(), JsonObject.class);
        //create a new JSONObject to add the required fields
        JsonObject reponseToCLient = new JsonObject();
        System.out.println("Setting response to "+repairMessage);
        //add selection
        reponseToCLient.addProperty("selection", json.get("selection").getAsString());
        //add response
        reponseToCLient.addProperty("response", json.get("response").getAsString());
        //return the JSON string
        return reponseToCLient.toString();
    }
}
