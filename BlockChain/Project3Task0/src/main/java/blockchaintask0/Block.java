/**
 * Author: Praveen Ramesh
 * Andrew ID: pramesh2@andrew.cmu.edu
 The Block class represents a block in a blockchain. It contains data
 fields for the index,timestamp, data, previous hash,nonce, and difficulty
 of the block. It also has methods for calculating the hash of the block using
 the SHA-256 algorithm, performing a proof of work to find a hash with a specified
 number of leading zeros, and converting the block to a JSON string using
 the Gson library.
 */
//import required packages
package blockchaintask0;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
//A public class that has all the required methods
public class Block extends java.lang.Object{
    //index representing the idex of the block
    int index;
    //timestamp to store the time of creation
    java.sql.Timestamp timestamp;
    //data to store the transaction
    java.lang.String data;
    //previoushash to store the hashvalue of the previous block
    String previousHash;
    //BigInteger value determined by a proof of work routine
    BigInteger nonce;
    //an int that specifies the minimum number of left most hex digits needed by a proper hash
    int difficulty;
    /**
     Constructs a new Block object with the specified index, timestamp,
     data, and difficulty.
     @param index the index of the block in the blockchain
     @param timestamp the timestamp indicating when the block was created
     @param data the data stored in the block
     @param difficulty the difficulty level for the proof of work algorithm
     */
    public Block(int index, Timestamp timestamp, String data, int difficulty) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;
    }
    //getter method for the index
    public int getIndex() {
        return index;
    }
    //getter method for the timestamp
    public Timestamp getTimestamp() {
        return timestamp;
    }
    //getter method for the data
    public String getData() {
        return data;
    }
    //getter method for the previousHash
    public String getPreviousHash() {
        return previousHash;
    }
    //getter method for the nonce
    public BigInteger getNonce() {
        return nonce;
    }
    //getter method for the difficulty
    public int getDifficulty() {
        return difficulty;
    }
    //setter method for index
    public void setIndex(int index) {
        this.index = index;
    }
    //setter method for the timestamp
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    //setter method for data
    public void setData(String data) {
        this.data = data;
    }
    //setter method for previousHash
    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }
    //setter method for difficulty
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
    //method for calculating hash of the block using SHA=256
    public java.lang.String calculateHash(){
        //declare MessageDigest object for hashing
        MessageDigest md = null;
        //declare hash string
        String hash;
        //Initialize the message to hash
        String message = index + timestamp.toString() + data + previousHash + nonce.toString() + String.valueOf(difficulty);
        try {
            //get the SHA-256 instance
            md = MessageDigest.getInstance("SHA-256");
            //update the digest
            md.update(message.getBytes());
            //completes the hash computation
            hash = bytesToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        //return the hash String
        return hash;
    }

    /**
     * Method that carries out a proof-of-work calculation to locate a
     * hash value that satisfies the criterion for difficulty defined for this block.
     * @return the hash which is computed that meets the difficulty
     */
    public java.lang.String proofOfWork() {
        //Initialze the nonce to 0
        nonce = BigInteger.valueOf(0);
        //Create a check string with required difficulty to compare it with the hash value
        String check = "0".repeat(difficulty);
        //call the calculateHash() to compute the hash
        String hash = calculateHash();
        //while loop to get the hash that matches the required difficulty
        while (!hash.substring(0, difficulty).equals(check)) {
            //adding one to the nonce
            nonce = nonce.add(BigInteger.valueOf(1));
            //computing hash
            hash = calculateHash();
        }
        //return the computed hash that matches the
        return hash;
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
     * toString method overridden to parse the object to JSON string
     * @return the JSON object of the block
     */
    public String toString(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
        // Serialize to JSON
        return gson.toJson(this);

    }
}
