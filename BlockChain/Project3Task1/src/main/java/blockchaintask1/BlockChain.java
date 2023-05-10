/**
 * Author: Praveen Ramesh
 * Andrew ID: pramesh2@andrew.cmu.edu
 The BlockChain class represents a blockchain of all the transactions. It contains data
 fields for the blocks, chainHash and hashesPerSecond of the chain. It also has methods for
 adding a block,verifying the blockchain, viewing the blockchain, corrupting and repairing the
 blockchain.
 */
//import required packages
package blockchaintask1;
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
    public void addBlock(blockchaintask1.Block newBlock){
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
    public blockchaintask1.Block getLatestBlock(){
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
    public blockchaintask1.Block getBlock(int i){
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
}


