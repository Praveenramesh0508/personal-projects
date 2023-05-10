/**
 * Author: Praveen Ramesh
 * Andrew ID: pramesh2@andrew.cmu.edu
 The Genre class is a Java class that has a single member variable called genreMap.
 This variable is a TreeMap that stores genre names as keys and their corresponding
 genre codes returned by the TMDB API for genre mapping. These mappings are used to
 suggest movies for a particular genre.
 */
//import the required packages
package ds;
import com.google.gson.*;
import java.util.TreeMap;
//Public class Genre
public class Genre {
    //TreeMap which maps the genre and TMDB's genre code
    TreeMap<String,Integer> genreMap=new TreeMap<String,Integer>();

    /**
     * setUpGenreMap is a method to parse the TMDB's genre lookup API response to
     * treemap to use it for suggesting movies
     * @param genreResponse
     */
    public void setUpGenreMap(String genreResponse){
        //Initialize Gson object
        Gson gson = new Gson();
        //Parse the genreResponse to the JsonObject object
        JsonObject jsonObject = JsonParser.parseString(genreResponse).getAsJsonObject();
        //get the result parameter and parse it to a JsonArray
        JsonArray genresArray = jsonObject.getAsJsonArray("genres");
        //loops over all the elements in the array to add the genre and genre code to treemap
        for (JsonElement genreElement : genresArray) {
            //create a JsonObject from each element
            JsonObject genreObject = genreElement.getAsJsonObject();
            //Store the id and name to the treemap
            int id = genreObject.get("id").getAsInt();
            String name = genreObject.get("name").getAsString();
            genreMap.put(name, id);
        }
    }

    /**
     * Getter method getGenreMap to send the genreMap
     * @return genreMap
     */
    public TreeMap<String, Integer> getGenreMap() {
        return genreMap;
    }
}
