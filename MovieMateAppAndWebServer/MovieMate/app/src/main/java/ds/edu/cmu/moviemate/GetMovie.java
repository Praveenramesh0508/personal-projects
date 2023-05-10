/**
 * Author : Praveen Ramesh
 * Andrew ID: pramesh2@andrew.cmu.edu
 * The GetMovie class is responsible for fetching movie data from a remove web server
 * using the provided search term or suggestion genre. The movie data includes the title,
 * overview, release date, rating, and a poster image. This class provides two methods
 * one for searching movie by search term and another method for suggesting a movie by genre.
 * The results are returned asynchronously to the calling activity. The class uses a BackgroundTask
 * inner class to handle the background processing and fetching the data.
 * Referred Android Lab of Interesting picture for implementation
 */
//import the required packages
package ds.edu.cmu.moviemate;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

//Public class GetMovie to handle the background task and fetch data
public class GetMovie {
    //Web service URL
    String webServerURL="https://pramesh20508-refactored-adventure-667grjxpgg6c5q4q-8080.preview.app.github.dev";
    //Declare MovieMate object
    MovieMate mm = null;
    //Declare search movie
    String searchMovie = null;
    //Declare a bitmap object for poster
    Bitmap poster = null;
    //Declare a title string
    String title="";
    //Declare a overview string
    String overview = "";
    //Declare releaseDate
    String releaseDate="";
    //Declare rating
    String rating="";
    //Declare a variable for choice --> 1 for search and 2 for suggest
    int choice =0 ;
    //Initialize the genre as null
    String genre="null";


    /**
     * The GetMovie's search method is dedicated for searching a movie.
     * This  method initializes the member variables according to search specification and calls the BackgroundTask's
     * execute method with the activity. The choice would be initialized with 1 for search functionality.
     * @param choice 1 for search
     * @param searchMovie Movie name entered by the user
     * @param activity
     * @param mm
     */
    public void search(int choice, String searchMovie, Activity activity, MovieMate mm) {
        //set the choice
        this.choice=choice;
        //set the objects reference
        this.mm = mm;
        //set the movie term to search
        this.searchMovie = searchMovie;
        //call the private class BackgroundTask to execute background task
        //activity is the UI thread activity
        new BackgroundTask(activity).execute();
    }

    /**
     * The GetMovie's search method is dedicated for suggesting a movie. This suggest method
     * is specifically for suggestion functionality.The choice would be initialized with 2 for
     * suggest functionality.
     * @param choice 2 for suggest
     * @param activity
     * @param mm
     * @param genre Genre selected by the user from spinner
     */

    public void suggest( int choice, Activity activity, MovieMate mm,String genre) {
        //set the choice
        this.choice=choice;
        //set the MovieMate's object reference
        this.mm = mm;
        //set the genre for providing suggestions
        this.genre = genre;
        //call the private class BackgroundTask to execute background task
        //activity is the UI thread activity
        new BackgroundTask(activity).execute();
    }

    /**
     * Citation: This class is adapted from Android Interesting Picture lab.
     * This private class BackgroundTask implements a separate thread to execute a long running
     * task that should not be performed on the UI thread. The thread is created using a Thread object,
     * and the doInBackground() method is called to perform the actual work.
     * After the task is complete, the onPostExecute() method is called to update a UI widget.
     * Moreover, the UI widgets are updated from the main UI thread.
     *
     */
    private class BackgroundTask {
        // The UI thread
        private Activity activity;
        //constructor of BackgroundTask
        public BackgroundTask(Activity activity) {
            this.activity = activity;
        }
        //startBackground() create a runnable thread to do the background task
        private void startBackground() {
            new Thread(new Runnable() {
                public void run() {
                    //calls the doInBackground() method ro perform background operations
                    doInBackground();
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            //call the onPostExecute() asynchronously
                            onPostExecute();
                        }
                    });
                }
            }).start();
        }
        //execute() calls the startBackground() top start the background task
        private void execute(){

            startBackground();
        }

        /*doInBackground method checks for the choices and calls the GetMovie's search
          when the choice is 1 else calls the GetMovie's suggest method when the choice is 2
         */
        private void doInBackground() {
            //check for the choice and calls the corresponding methods
            if(choice==1) {
                poster = search(searchMovie);
            }
            else {
                poster =suggest(genre);
            }
        }
        /*
        onPostExecute checks for the choice and calls the MovieMate's corresponding searchReady or
        suggestionReady method to display the results in the UI widgets.
         */
        public void onPostExecute() {
            if(choice==1) {
                mm.searchReady(poster, title, overview, releaseDate);
            }
            else {
                mm.suggestionReady(poster,title,rating);
            }
        }

        /**
         * BackgroundTask's search method is responsible for the actual search functionality
         * in the background thread. This uses several helper functions to set the information
         * about the movie including the title, overview of the movie, release_date and finally returns
         * the Bitmap of the poster image.
         * @param searchMovie
         * @return Bitmap poster image
         */
        private Bitmap search(String searchMovie) {
            try {
                //set the posterURL to null
                String posterURL = null;
                //encode the searchMovie term
                String encodedQuery = null;
                encodedQuery = URLEncoder.encode(searchMovie, "UTF-8");
                //Initialize the URL to do a GET request from the deployed webservice
                URL searchURL = createURL(webServerURL+"/MovieMateServlet/search?query=" + encodedQuery);
                //calls the hitWebServer to get the response from the web server
                String responseText = hitWebServer(searchURL);
                //check if the response text is empty
                if (!responseText.equals(""))
                {   //Create a GSON object
                    Gson gson = new Gson();
                    //Parse the responseText to GSON's JSON object
                    JsonObject jsonObject = gson.fromJson(responseText, JsonObject.class);
                    //set the title from the JSON object's title field
                    title = jsonObject.get("title").getAsString();
                    //set the overview from the JSON object's overview field
                    overview = jsonObject.get("overview").getAsString();
                    //set the releaseDate from the JSON object's release_date field
                    releaseDate = jsonObject.get("release_date").getAsString();
                    //set the posterURL from the JSON object's poster_path field
                    posterURL = jsonObject.get("poster_path").getAsString();
                    //Create a URL to get the image of the poster
                    URL u = createURL("https://image.tmdb.org/t/p/w200/" + posterURL);
                    //call the getRemoteImage with the URL u to get the bitmap and return the bitmap image
                    return getRemoteImage(u);
                }
                //if the responseText is empty return null
                else {
                    return null;
                }
                //catch clause
            } catch (UnsupportedEncodingException e) {
                System.out.println(e.getMessage());
                return null;
            }
        }

        /**
         * BackgroundTask's suggest method is responsible for the actual suggest functionality
         * in the background thread. This uses several helper functions to set the information
         * about the suggested movie including the title, average rating and finally returns
         * the Bitmap of the poster image.
         * @param genre genre requested by the user for suggestion
         * @return Bitmap poster image
         */

        public Bitmap suggest(String genre){
                //Initialize the posterURL as null
                String posterURL=null;
                //Create a URL to do a GET request from the deployed webservice to get the suggestion
                URL suggestURL=createURL(webServerURL+"/MovieMateServlet/suggest?genre="+genre);
                //call the hitWebServer to get the response from the web server
                String responseText = hitWebServer(suggestURL);
                //check if the response text is empty
                if (!responseText.equals(""))
                {
                    //Create a GSON object
                    Gson gson = new Gson();
                    //Parse the responseText to GSON's JSON object
                    JsonObject jsonObject = gson.fromJson(responseText, JsonObject.class);
                    //set the title from the JSON object's title field
                    title = jsonObject.get("title").getAsString();
                    //set the rating from the JSON object's vote_average field
                    rating=jsonObject.get("vote_average").getAsString();
                    //set the posterURL from the JSON object's poster_path field
                    posterURL = jsonObject.get("poster_path").getAsString();
                    //Create a URL to get the image of the poster
                    URL u = createURL("https://image.tmdb.org/t/p/w200/" + posterURL);
                    //call the getRemoteImage with the URL u to get the bitmap and return the bitmap image
                    return getRemoteImage(u);
                }
                //if the responseText is empty return null
                else {
                    return null;
                }
            }

        /**
         * hitWebServer is a helper method to perform the GET request to the web server
         * which has been deployed to perform the search or suggest operation.
         * @param url Endpoint of the web server to perform either search or suggest operation
         * @return responseText which the response JSON from the server
         */
        private String hitWebServer(URL url){
            try {
                //create a HttpURLConnection
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //set the request method to GET
                conn.setRequestMethod("GET");
                //set the request property to accept application/json
                conn.setRequestProperty("Accept", "application/json");
                //get the responseText from getResponseBody method
                String responseText = getResponseBody(conn);
                //returns the responseText
                return  responseText;
              //catch clause
            } catch (ProtocolException e) {
                return null;
            } catch (MalformedURLException e) {
                return null;
            } catch (UnsupportedEncodingException e) {
                return null;
            } catch (IOException e) {
                return null;
            }
        }

        /**
         * This method is adapted from the InterestingPicture Android lab
         * getRemoteImage is a method which returns a bitmap of that image
         * given a URL of the image
         * @param url url of the image
         * @return Bitmap image of the poster
         */
        @RequiresApi(api = Build.VERSION_CODES.P)
        private Bitmap getRemoteImage(final URL url) {
            try {
                //Initialize a URLConnection object
                final URLConnection conn = url.openConnection();
                //Opens the connection
                conn.connect();
                //Get the  from the connection's getInputStream
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                //Create a bitmap object from BufferedInputStream object
                Bitmap bm = BitmapFactory.decodeStream(bis);
                //return the bitmap image
                return bm;
                //catch clause returns null when there is an exception
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * createURL is a helper method to create a URL given a string path
         * @param string path to be converted to URL
         * @return URL
         */
        public URL createURL(String string){
            try {
                //return the URL format of a string path
                return new URL(string);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

        }

        /**
         * Citation: Used this code from REST lab in REST_Client_Project
         * This method gets the reponseText through BufferedReader and closes the
         * connection after getting the response
         * @param conn
         * @return responseText
         * @param conn
         * @return
         */
        public  String getResponseBody(HttpURLConnection conn) {
            //Initialize responseText as ""
            String responseText = "";
            try {
                //Initialize output as ""
                String output = "";
                //get the InputStream through BufferedReader
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));
                while ((output = br.readLine()) != null) {
                    //append the output to responseText
                    responseText += output;
                }
                //disconnect from the connection
                conn.disconnect();
                //catch clause
            } catch (IOException e) {
                System.out.println("Exception caught " + e);
            }
            //return the responseText
            return responseText;
        }
    }
}

