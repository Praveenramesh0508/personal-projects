/**
 * Author: Praveen Ramesh
 * Andrew ID: pramesh2@andrew.cmu.edu
 The BlockChain class represents a blockchain of all the transactions. It contains data
 fields for the blocks, chainHash and hashesPerSecond of the chain. It also has methods for
adding a block,verifying the blockchain, viewing the blockchain, corrupting and repairing the
 blockchain.
 */
//import required packages
package blockchaintask0;
import com.google.gson.Gson;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
//A public class that has all the required methods
public class BlockChain extends java.lang.Object{
    //An arraylist which contains all the blocks
    private ArrayList<Block> blocks;
    //chainHash which contains the hash value of the last block in the chain
    private String chainHash;
    //hashes computed per second by the machine
    private int hashesPerSecond;
    //Constructor to initialize the members of the class
    public BlockChain(){
        blocks=new ArrayList<Block>();
        chainHash ="";
        hashesPerSecond=0;
    }

    /**
     * Method that adds a block to the blockchain after performing the
     * proof of work. ChainHash is assigned from the last block's hash value
     * @param newBlock
     */
    public void addBlock(blockchaintask0.Block newBlock){
        //if the size of the block is non-zero then set the previousHash of the
        //new block to the chain's current chainHash, else the previousHash will
        //be "" for chain containing just one block
        if(blocks.size()!=0){
            newBlock.setPreviousHash(chainHash);
        }
        //perform the proof of work for the newly added block
        newBlock.proofOfWork();
        //add the block to the arraylist
        blocks.add(newBlock);
        //set the chainHash as the new block's hash value
        chainHash=newBlock.calculateHash();
    }

    /**
     * Method to check whether a chain is valid by comparing the block's hash
     * with the number of leftmost 0's (proof of work) as specified in the difficulty field
     * and comparing the hash with the previousHash of the next block. The corresponding
     * chainHash of the chain is also checked.
     * @return TRUE if the chain is valid, else returns FALSE with an appropriate error message
     */
    public java.lang.String isChainValid(){
        //checks the size of the block
        if(blocks.size()==1){
            //get the first genesis block
            Block b=blocks.get(0);
            //compute the hash of the block
            String hash= b.calculateHash();
            //Initialize a string to check the proof of work
            String check = "0".repeat(b.getDifficulty());
            //compares the hash with the leftmost 0's
            if(!hash.substring(0, b.getDifficulty()).equals(check)){
                //if comparison fails then return false and the error message
                return "FALSE \nImproper hash on node 0 Does not begin with 00";
            } else if (!hash.equals(chainHash)) {
                //if the chainHash doesn't match with the computed hash then
                //return false with the error message
                return "FALSE \nHash value and chainHash are not matching";
            }
            //else return true
            else {
                return "TRUE";
            }
        }
        //logic for chain containing more than one block
        else {
            //loop for iterating over the chain
            for (int i=0;i<blocks.size();i++){
                //get the ith block
                Block b = blocks.get(i);
                //compute the hash of the block
                String hash = b.calculateHash();
                //check if the block is the last block in the chain
                if(i!=blocks.size()-1) {
                    //Initialize the check string to compare hash with the difficulty
                    String check = "0".repeat(b.getDifficulty());
                    //check the substring of the hash
                    if (!hash.substring(0, b.getDifficulty()).equals(check)) {
                        //if the comparison fails then return false with the error message
                        return "FALSE \nImproper hash on node "+ i +" Does not begin with 00";
                    }
                    //checks the block's hash value with the next block's previousHash value
                    if (!hash.equals(blocks.get(i + 1).previousHash)){
                        //if the comparison fails then return FALSE with an error message
                        return "FALSE \nHash value of node "+ i +" is not matching with node "+(i+1)+" previosHash";
                    }
                }
                //check condition for the last block in the chain
                else {
                    //check whether the hash value matches the chainHash of the blockchain
                    if (!hash.equals(chainHash)){
                        //if the condition fails then return FALSE with the corresponding error
                        return "FALSE \nHash value and chainHash are not matching";
                    }
                }
            }
            //return TRUE if all the check cases are passed
            return "TRUE";
        }
    }

    /**
     * Method that repairs the chain. It checks the hashes of each block and ensures that any illegal
     * hashes are recomputed. After this routine is run, the chain will be valid.It computes new proof
     * of work based on the difficulty specified in the Block
     */
    public void repairChain()
    {   //loop for iterating over the chain
        for (int i=0;i<blocks.size();i++)
        {   //compute the hash of the ith block
            String hash = blocks.get(i).calculateHash();
            //check if the block is the last block in the chain
            if(i!=blocks.size()-1)
            {   //checks the block's hash value with the next block's previousHash value
                if (!hash.equals(blocks.get(i + 1).previousHash))
                {   //if the comparison fails then compute the proof of work again
                    blocks.get(i).proofOfWork();
                    //set the previousHash of the next block to the new hash
                    blocks.get(i+1).setPreviousHash(blocks.get(i).calculateHash());
                }
            }
            //check condition for the last block in the chain
            else
            {   //check whether the hash value matches the chainHash of the blockchain
                if (!hash.equals(chainHash)){
                    //if the comparison fails then compute the proof of work again
                    blocks.get(i).proofOfWork();
                    //set the previousHash of the next block to the new hash
                    chainHash=blocks.get(i).calculateHash();
            }
            }
        }
    }
    //getter method to get the chainHash
    public java.lang.String getChainHash(){
        return chainHash;
    }

    //getter method to get the latestBlock in the blockChain
    public blockchaintask0.Block getLatestBlock(){
        return blocks.get(blocks.size()-1);
    }
    //getter method to get the chain size
    public int getChainSize(){
        return blocks.size();
    }

    /**
     * This method computes exactly 2 million hashes and times how long that process takes
     * and computes the hashes per second from that value
     */
    public void computeHashesPerSecond(){
        //Initialize the message to hash
        String messageToHash="00000000";
        String hash;
        MessageDigest md = null;
        //get the start time
        long start = System.currentTimeMillis();
        //loop for hashing 2 million times
        for (int i=0;i<2000000;i++){
            try {
                //get the SHA-256 instance
                md = MessageDigest.getInstance("SHA-256");
                //compute the hash
                md.update(messageToHash.getBytes());
                //convert it to hex string
                hash = bytesToHex(md.digest());
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        //get the stop time
        long stop = System.currentTimeMillis();
        //compute hashesPerSecond
        hashesPerSecond= (int) (2000000/(stop-start))*1000;
    }

    //getter method to get the hashesPerSecond
    public int getHashesPerSecond(){
        return hashesPerSecond;
    }

    //getter  method to get the block
    public blockchaintask0.Block getBlock(int i){
        return blocks.get(i);
    }

    //getter method to get the total difficulty of the chain
    public int getTotalDifficulty(){
        //Initialze the totalDifficulty to 0
        int totalDifficulty=0;
        //loop over each block
        for (Block b:blocks) {
            //sum the difficulty
            totalDifficulty=totalDifficulty+b.getDifficulty();
        }
        //return the totalDifficulty
        return totalDifficulty;
    }

    //method to get the totalExpectedHashes
    public double getTotalExpectedHashes(){
        //Initialze the totalExpectedHashes to 0
        double totalExpectedHashed=0;
        //loop over each block
        for (Block b: blocks) {
            //compute the total expected hashed
            totalExpectedHashed= (totalExpectedHashed+Math.pow(16,b.getDifficulty()));
        }
        //return the totalExpectedHashes
        return totalExpectedHashed;
    }

    //method to get the difficulty of most recent block
    public int difficultyMostRecent(){
        return blocks.get(blocks.size()-1).getDifficulty();
    }

    //method to get the nonce of most recent block
    public BigInteger nonceMostRecent(){
        return blocks.get(blocks.size()-1).getNonce();
    }

    /**
     * Method that overrides the toString() to get a JSON format of the
     * blockchain and the chain's chainHash
     * @return the JSON format of the blockchain
     */
    public java.lang.String toString(){
        //Declare and initialize the GSON's JSON object
        JsonObject jsonObject = new JsonObject();
        //Declare and initialize the GSON's JSON array
        JsonArray jsonArray = new JsonArray();
        //Create a GsonBuilder
        Gson gson = new GsonBuilder().create();
        //loop over all blocks
        for (Block b :blocks)
        {   //get the toString() of each block and convert it to JSON object
            JsonObject json = gson.fromJson(b.toString(), JsonObject.class);
            //add the JSON object to the JSON array
            jsonArray.add(json);
        }
        //append the JSON elements to a final JSONObject
        jsonObject.add("ds_chain",jsonArray);
        jsonObject.addProperty("chainHash",this.chainHash);

        //return the String format of the JSONObject
        return jsonObject.toString();
    }

    public static java.sql.Timestamp getTime(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime currentDateTime = LocalDateTime.now();
        Timestamp formattedDateTime = Timestamp.valueOf(new String(currentDateTime.format(formatter)));
        return formattedDateTime;
    }


    // Code from stack overflow
    // https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    /**
     * Method to convert bytes array to hex String
     * @param bytes
     * @return the hex String format of the input bytes array
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Main method that acts as a test driver for your Blockchain. It will begin by
     * creating a BlockChain object and then adding the Genesis block to the chain.
     * The Genesis block will be created with an empty string as the pervious hash and a difficulty of 2.
     * All other methods are called based on the user's choice
     * @param args
     */
    public static void main(java.lang.String[] args){
        //Initialize the index to 0
        int index=0;
        //Create scanner object
        Scanner scanner = new Scanner(System.in);
        //Initialize the blockChain
        BlockChain blockChain=new BlockChain();
        //Call the computeHashesPerSecond method
        blockChain.computeHashesPerSecond();
        //Create the genesis block
        Block genesis=new Block(0,getTime(),"Genesis",2);
        //add 1 to the index
        index=index+1;
        //set the previousHash of the genesis block to "" string
        genesis.setPreviousHash("");
        //add the genesis block to the chain
        blockChain.addBlock(genesis);
        //declare the choice variable
        int choice;
        //create an infinite loop
        while(true){
             /*
            Time Data Analysis:
            For addBlock():
            When the difficulty is 2 - 5milliseconds
            When the difficulty is 4 - 50 milliseconds
            When the difficulty is 6 - 42357 milliseconds

            For isChainValid():
            The time for execution doesn't change

            For repairChain():
            When the difficulty is 6 - 44406 milliseconds

            Therefore, when the difficulty increases the time to find the proof of work increases drastically while adding
            the new block.Thus, when difficulty increases time taken to add the block also increases drastically.
            The isChainValid() method doesn't take that much time because it doesn't do proof of work.
            However, repairChain() takes a lot of time when difficulty increases as it has to do the proof of block again,
            which takes a lot of time when difficulty is higher
             */
            //display the menu
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
            //get the choice
            choice=Integer.parseInt(scanner.nextLine());
            //check the choice
            if(choice==6){
                System.exit(0);
            }
            //if the choice is 0 display all the details of the blockchain
            if(choice==0){
                //display the details by calling the required methods
                System.out.println("Current size of chain: "+blockChain.getChainSize());
                System.out.println("Difficulty of most recent block: "+ blockChain.difficultyMostRecent());
                System.out.println("Total difficulty for all blocks: "+blockChain.getTotalDifficulty());
                System.out.println("Approximate hashes per second on this machine: "+blockChain.getHashesPerSecond());
                System.out.println("Expected total hashes required for the whole chain: "+blockChain.getTotalExpectedHashes());
                System.out.println("Nonce for most recent block: "+blockChain.nonceMostRecent());
                System.out.println("Chain hash: "+blockChain.chainHash);
            }
            //Adding the block
            if(choice==1){
                //declare difficulty
                int difficulty;
                //declare transaction
                String transaction;
                //get the difficulty and transaction
                System.out.println("Enter difficulty > 0");
                difficulty=Integer.parseInt(scanner.nextLine());
                System.out.println("Enter transaction");
                transaction=scanner.nextLine();
                //get the start time
                long start = System.currentTimeMillis();
                //create a new block
                Block block= new Block(index,getTime(),transaction,difficulty);
                //add 1 to the index
                index=index+1;
                //call the addBlock method to add the new block
                blockChain.addBlock(block);
                //get the stop time
                long stop = System.currentTimeMillis();
                //display the execution time
                System.out.println("Total execution time to add this block was "+(stop-start)+" milliseconds");

            }
            //Chain verification
            if(choice==2){
                //get the start time
                long start = System.currentTimeMillis();
                //call the isChainValid method to verify the chain
                System.out.println("Chain Verification : "+ blockChain.isChainValid());
                //get the stop time
                long stop = System.currentTimeMillis();
                //display the execution time
                System.out.println("Total execution time to verify the chain was "+(stop-start)+" milliseconds");
            }
            //display the blockchain
            if(choice==3){
                //call the toString method to display the JSON
                System.out.println(blockChain.toString());
            }
            //Corrupt the blockchain
            if(choice==4){
                System.out.println("corrupt the Blockchain");
                //get the ID to corrupt
                System.out.println("Enter block ID of block to corrupt");
                int ID=Integer.parseInt(scanner.nextLine());
                //get the new transaction from the user
                System.out.println("Enter new data for block "+ID);
                String newData=scanner.nextLine();
                //set the new transaction to the block through the setter method
                blockChain.blocks.get(ID).setData(newData);
                //display the result
                System.out.println("Block "+ID+" now holds "+newData);
            }
            //Repair the chain
            if(choice==5){
                //get the start time
                long start = System.currentTimeMillis();
                //call the  method to repair the blackchain
                blockChain.repairChain();
                //get the stop time
                long stop = System.currentTimeMillis();
                //display the execution time
                System.out.println("Total execution time to repair the chain was "+(stop-start)+" milliseconds");
            }
        }
    }
}


