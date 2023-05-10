/**
 * Author: Praveen Ramesh
 * Andrew ID: pramesh2@andrew.cmu.edu
 This class represents a reply message that the blockchain server sends to the client.
 It includes details about the blockchain, including the chain hash, total number of hashes, total difficulty, most recent nonce, and hash rate per second.
 The class has two constructors: one for the blockchain data and one for the response message.
 The class offers getter methods for each field as well as a method to turn an object into a JSON string.
 */
//import the required packages
package blockchaintask1;
import com.google.gson.Gson;
//A public ResponseMessage class with the required methods
public class ResponseMessage {
    //declare a selection string
    String selection;
    //declare size
    int size;
    //declare chainHash
    String chainHash;
    //declare totalHashes
    int totalHashes;
    //declare totalDiff of the chain
    int totalDiff;
    //declare recent nonce of the last block
    int recentNonce;
    //declare diff of the recent block
    int diff;
    //declare hashesPerSecond
    int hps;
    //declare response string
    String response;

    /**
     An Overloaded constructor with the details of the blockchain
     */
    public ResponseMessage(String selection,int size,String chainHash,int totalHashes,int totalDiff,int recentNonce,int diff,int hps){
        this.selection=selection;
        this.size=size;
        this.chainHash=chainHash;
        this.totalHashes=totalHashes;
        this.totalDiff=totalDiff;
        this.recentNonce=recentNonce;
        this.diff=diff;
        this.hps=hps;
    }

    /**
     An Overloaded constructor with the selection string and response
     */
    public ResponseMessage(String selection,String response){
        this.selection=selection;
        this.response=response;
    }

    /**
     Getter method for selection
     */
    public String getSelection() {
        return selection;
    }
    /**
     Getter method for size
     */
    public int getSize() {
        return size;
    }

    /**
     Getter method for chainHash
     */
    public String getChainHash() {
        return chainHash;
    }

    /**
     Getter method for totalHashes
     */
    public int getTotalHashes() {
        return totalHashes;
    }

    /**
     Getter method for totalDiff
     */
    public int getTotalDiff() {
        return totalDiff;
    }

    /**
     Getter method for recentNonce
     */
    public int getRecentNonce() {
        return recentNonce;
    }

    /**
     Getter method for diff
     */
    public int getDiff() {
        return diff;
    }

    /**
     Getter method for hps
     */
    public int getHps() {
        return hps;
    }

    /**
     Getter method for response
     */
    public String getResponse() {
        return response;
    }

    /**
     Getter method for ResponseMessage in JSON format
     */
    public String getResponseMessage()
    {
        Gson gson=new Gson();
        //convert the object to JSON
        return gson.toJson(this);
    }


}
