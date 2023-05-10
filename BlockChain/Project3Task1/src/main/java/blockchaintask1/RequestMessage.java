/**
 * Author: Praveen Ramesh
 * Andrew ID: pramesh2@andrew.cmu.edu
 The request message that the client sends to the blockchain server is represented by this class.
 It includes details on the kind of request being performed.
 The class comprises three constructors, each for a distinct request type, and getter methods are provided for each field.
 A method to turn the object into a JSON string is also included in the class.
 */
//import the required packages
package blockchaintask1;
import com.google.gson.Gson;
//A public RequestMessage class with the required methods
public class RequestMessage {
    //declare the selection
    String selection;
    //declare enteredDifficulty to store the entered difficulty
    int enteredDifficulty;
    //declare the transaction
    String transaction;
    //declare the corruptID for corruption
    int corruptID;
    //declre the new transaction to corrupt the block
    String newTransaction;

    /**
     * An Overloaded constructor with selection parameter
     */
    public RequestMessage(String selection){
        this.selection=selection;
    }

    /**
     * An Overloaded constructor with selection, enteredDifficulty, and transaction parameter to add a block
     */
    public RequestMessage(String selection,int enteredDifficulty,String transaction){
        this.selection=selection;
        this.enteredDifficulty=enteredDifficulty;
        this.transaction=transaction;
    }

    /**
     * An Overloaded constructor with selection, newTransaction, and corruptID parameters to corrupt a block
     */
    public RequestMessage(String selection,String newTransaction,int corruptID){
        this.selection=selection;
        this.corruptID=corruptID;
        this.newTransaction=newTransaction;
    }

    /**
     Getter method for enteredDifficulty
     */
    public int getEnteredDifficulty() {
        return enteredDifficulty;
    }

    /**
     Getter method for transaction
     */
    public String getTransaction() {
        return transaction;
    }

    /**
     Getter method for corruptID
     */
    public int getCorruptID() {
        return corruptID;
    }

    /**
     Getter method for selection
     */
    public String getSelection() {
        return selection;
    }

    /**
     Getter method for newTransaction
     */
    public String getNewTransaction() {
        return newTransaction;
    }

    /**
     Getter method for RequestMessage in JSON format
     */
    public String getRequestMessage(){
        Gson  gson=new Gson();
        //convert the object to JSON
        return gson.toJson(this);
    }

}
