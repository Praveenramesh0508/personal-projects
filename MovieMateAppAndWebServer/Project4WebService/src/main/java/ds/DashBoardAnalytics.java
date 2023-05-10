/**
 * Author: Praveen Ramesh
 * Andrew ID: pramesh2@andrew.cmu.edu
 The DashBoardAnalytics is a Java class that contains three instance variables and three methods used
 to compute operational analytics for the MovieMate application. These metrics include the average latency,
 top searched movies, and top suggested genres. Additionally, the class has three getter methods that can be
 used to retrieve these metrics and send them to the Servlet.
 */
package ds;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import java.util.Arrays;


//Public DashBoardAnalytics class
public class DashBoardAnalytics {
    //declare averageLatency
    private double  averageLatency;

    //declare topSearchMovie
    private String topSearchMovie;

    //declare topSuggestionsAsked
    private String topSuggestionsAsked;

    /**
     * Referred https://www.mongodb.com/docs/drivers/java/sync/current/fundamentals/aggregation/ aggregation and
     * builders documentation for aggregating and averaging the latency from mongoDB collection
     * setAverageLatency matches and filter the documents in the collection and groups it to get the
     * average of latency field for operational analytics
     * @param movieMateCollection
     */
    public void setAverageLatency(MongoCollection<Document> movieMateCollection){
        //Create a document to store the average latency
        //Documents in the collection with latency field is collected and aggregated for computing the average through Accumulators
        Document latencyDoc = movieMateCollection.aggregate(Arrays.asList(Aggregates.match(Filters.exists("latency")),
                Aggregates.group(null, Accumulators.avg("avgLatency", "$latency"))
        )).first();
        //check if the result has any document
        if (latencyDoc != null) {
            //get the average latency
            averageLatency = latencyDoc.getDouble("avgLatency");
            //else set the averageLatency to 0
        } else {
            averageLatency=0;
        }
    }


    /**
     * setTopSearchMovie matches and filters the documents in the collection and groups it to get the
     * top searched movie by sorting the documents
     * @param movieMateCollection
     */
    public void setTopSearchMovie(MongoCollection<Document> movieMateCollection){
        //Create a document to store the top searched movie
        //Documents in the collection with eventType field as search is collected and aggregated based on
        //title and counted. The title aggregation is sorted and limited to 1 to get the topmost searched
        //movie.
        Document mostFrequentTitle = movieMateCollection.aggregate(Arrays.asList(
                Aggregates.match(new Document("eventType", "search")),
                Aggregates.group("$title", Accumulators.sum("count", 1)),
                Aggregates.sort(Sorts.descending("count")),
                Aggregates.limit(1)
        )).first();
        //check if the result has any document
        if (mostFrequentTitle != null) {
            //get top searched movie
            topSearchMovie = mostFrequentTitle.getString("_id");
        }
        //else set the topSearchMovie with null
        else {
            topSearchMovie="null";
        }
    }

    /**
     * setTopSuggestionsAsked is a method to that matches and filters the documents in the collection and groups it to get the
     * top suggestions asked based on the genre by the user
     * @param movieMateCollection
     */
    public void setTopSuggestionsAsked(MongoCollection<Document> movieMateCollection){
        //Create a document to store the top sugggestions asked
        //Documents in the collection with eventType field as suggest is collected and aggregated based on
        //$requestElement(genre) and counted. The genre aggregation is sorted and limited to 1 to get the topmost suggestions
        //asked
        Document mostFrequentSugestion = movieMateCollection.aggregate(Arrays.asList(
                Aggregates.match(new Document("eventType", "suggest")),
                Aggregates.group("$requestElement", Accumulators.sum("count", 1)),
                Aggregates.sort(Sorts.descending("count")),
                Aggregates.limit(1)
        )).first();
        //check if the result has any document
        if (mostFrequentSugestion != null) {
            //get top suggestions asked
            topSuggestionsAsked = mostFrequentSugestion.getString("_id");
        }
        //else set the topSuggestionsAsked to null
        else {
            topSuggestionsAsked="null";
        }
    }

    /**
     * Getter method for the computed averageLatency
     * @return averageLatency
     */
    public float getAverageLatency() {
        return (float) averageLatency;
    }

    /**
     * Getter method for  topSearchMovie
     * @return topSearchMovie
     */
    public String getTopSearchMovie() {
        return topSearchMovie;
    }

    /**
     * Getter method for topSuggestionsAsked
     * @return topSuggestionsAsked
     */
    public String getTopSuggestionsAsked() {
        return topSuggestionsAsked;
    }
}
