/**
 * Author: Praveen Ramesh
 * Andrew ID: pramesh2@andrew.cmu.edu
 The MongoDB class is a Java class responsible for creating or retrieving a database that stores logs
 of user interactions with the MovieMate webservice. It provides two helper methods for inserting a new
 document into the database and retrieving a list of existing documents in the collection.
 */
package ds;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
//Public MongoDB class
public class MongoDB {
    //Initialize database connection uri
    String uri = "mongodb://pramesh2:pramesh2@ac-gx9xm3y-shard-00-00.qlzkwa8.mongodb.net:27017,ac-gx9xm3y-shard-00-01.qlzkwa8.mongodb.net:27017,ac-gx9xm3y-shard-00-02.qlzkwa8.mongodb.net:27017/test?w=majority&retryWrites=true&tls=true&authMechanism=SCRAM-SHA-1";
    //Declare a MongoClient
    private MongoClient mongoClient;
    //Declare a MongoDatabase
    private MongoDatabase database;
    //Declare MongoCollection
    private MongoCollection<Document> movieMateCollection;

    /**
     * setupMongoDB is a method to create a database object or get the existing database named "MovieMate" and
     * get the collection named  "MovieMateData".
     *
     */
    public void setupMongoDB() {
            mongoClient = MongoClients.create(uri);
             database = mongoClient.getDatabase("MovieMate");
            if ((movieMateCollection = database.getCollection("MovieMateData")) == null) {
                database.createCollection("MovieMateData");
            } else {
                movieMateCollection = database.getCollection("MovieMateData");
            }
        }

    /**
     * insertMovieMateDocument inserts a movieMateDocument to the collection
      * @param movieMateDocument
     */
    public void insertMovieMateDocument(Document movieMateDocument){
        movieMateCollection.insertOne(movieMateDocument);
    }

    /**
     * Getter method to send the MongoCollection documents
     * @return movieMateCollection
     */
    public MongoCollection<Document> getMovieMateCollection(){
        return movieMateCollection;
    }
}
