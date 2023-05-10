/**
 * Author: Praveen Ramesh
 * Andrew ID: pramesh2@andrew.cmu.edu
 The MovieMateServlet is a Java servlet that handles HTTP requests and responses related to
 movie searching and suggestions. The servlet communicates with The Movie Database (TMDb)
 API to retrieve information about movies based on request queries or genre selection.
 The servlet also interacts with a MongoDB database to store logs related to user requests
 and generate analytics related to user behavior and logs.
 The servlet provides two main functionalities: movie search and movie suggestion.
 The searchMovie method retrieves information about a movie that matches the request query,
 and the suggestMovie method retrieves a list of popular movies that belong to the genre
 and randomly selects one movie from the list.
 */

//import  all the required packages
package ds;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import com.google.gson.*;
import org.bson.Document;


/*
MovieMateServlet public class which contains all the required methods
and has multiple instance variables such as TMDb API key, HTTP URL connection,
genre mapping object, MongoDB database object, and a dashboard analytics object.
 */

@WebServlet(name = "MovieMateServlet", urlPatterns = {"/MovieMateServlet"})
public class MovieMateServlet extends HttpServlet {
    //Initialize TMDB_URL link for genre and genre ID mapping
    private static String TMBD_URL="https://api.themoviedb.org/3/search/movie?api_key=83bd035c9f4c68dde7d181cd8db4b622&language=en-US&query=";
    //declare a HttpURLConnection
    HttpURLConnection conn;
    //declare genre
    Genre genre;
    //Declare MongoDB object
    MongoDB db;
    //Declare a Document to log the information
    Document movieMateDocument;
    //Declare a DashBoardAnalytics object
    DashBoardAnalytics dash;
    //TMDB private key
    String TMDB_key="83bd035c9f4c68dde7d181cd8db4b622";


    /**
     Overriding init() method to instantiate Genre object,
     MongoDB object and DashBoardAnalytics object. The method also
     calls setUpGenreMap to parse the Genre mapping
     */
    @Override
    public void init(){
        System.out.println("Starting the Server");
        //Initialize Genre object
        genre=new Genre();
        //Initialize MongoDB object
        db=new MongoDB();
        //calls setupMongoDB to set up  the database and collection
        db.setupMongoDB();
        //Initialize DashBoardAnalytics object
        dash=new DashBoardAnalytics();

        try {
            //URL for genre mapping endpoint of TMDB
            URL genreURL=new URL("https://api.themoviedb.org/3/genre/movie/list?api_key="+TMDB_key+"&language=en-US");
            //call setUpGenreMap with the responseText from the TMDB response
            genre.setUpGenreMap(doThirdPartyAPICall(genreURL));
            //catch clause
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Override doGet() to redirect to the required endpoint based on the request path.
     * There are two endpoints for this webservice. One is searchMovie() and the other is
     * suggestMovie()
     * @param request,reponse
     * @return response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //requestPath gets the request path
        String requestPath = request.getPathInfo();
        //get the requestTimestamp
        LocalDateTime requestTimestamp = LocalDateTime.now();
        //get the path to redirect it to the required endpoint
        if (requestPath == null || requestPath.equals("/")) {
            //if request path is null call showDashboard to display the dashboard
            showDashboard(request, response);
        }
        //if requestPath start with search call searchMovie
        else if (requestPath.startsWith("/search")) {
            //Initialize the query
            String query = request.getParameter("query");
            //Create a document to store the logs
            //eventType is search
            movieMateDocument = new Document("eventType", "search")
                    .append("requestElement", query)
                    .append("requestTimestamp", requestTimestamp.toString())
                    .append("mobileModel",request.getHeader("User-Agent"));
            //call searchMovie method
            searchMovie(response, query);
        }
        //if requestPath start with search call suggestMovie
        else if(requestPath.startsWith("/suggest")){
            //Initialize the genreString
            String genreString= request.getParameter("genre");
            //Create a document to store the logs
            //eventType is suggestion
            movieMateDocument = new Document("eventType", "suggest")
                    .append("requestElement", genreString)
                    .append("requestTimestamp", requestTimestamp.toString())
                    .append("mobileModel",request.getHeader("User-Agent"));
            //call suggestMovie method
            suggestMovie(response,genreString);
        }
        else {
            //set the error message for bad request path
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     searchMovie method sends a GET request to TMDb API to retrieve information about a movie
     that matches the user query, then parses the response to retrieve and return the movie's title,
     poster path, vote average, release date, and overview in JSON format. If the retrieved results array
     is empty, returns null. Also logs the retrieved movie information and
     latency in a MongoDB document.
     @param response the HttpServletResponse object
     @param query the user query string to search for a movie
     */
    public void  searchMovie(HttpServletResponse response, String query) {
        try {
            //create an UTF-8 encodedQuery
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            encodedQuery = encodedQuery.replace("+", "%20");
            //Create a TMDB URL for searching a movie
            URL url = new URL("https://api.themoviedb.org/3/search/movie?api_key=" + TMDB_key + "&language=en-US&query=" + encodedQuery);
            //get the start time
            long startTime = System.currentTimeMillis();
            //get the responseText by calling doThirdPartyAPICall with the URL
            String responseText = doThirdPartyAPICall(url);
            //set the endTime
            long endTime = System.currentTimeMillis();
            // set the latency
            long latency = endTime - startTime;
            //declare a GSON object
            Gson gson = new Gson();
            System.out.println(responseText);
            //convert gson to JsonObject
            JsonObject jsonObject = gson.fromJson(responseText.toString(), JsonObject.class);
            //search for results and parse it to JsonArray
            JsonArray resultsArray = jsonObject.getAsJsonArray("results");
            //send the response only when there is atleast one element in resultsArray
            if (resultsArray.size() != 0) {
                //get the topmost search result - TMDB sorts it according to the match percentage with the input query
                JsonObject firstResult = resultsArray.get(0).getAsJsonObject();
                //create a responseJsonObject object
                JsonObject responseJsonObject = new JsonObject();
                //add title to responseJsonObject and sets only the required information to the response JSON
                responseJsonObject.addProperty("title", firstResult.get("title").getAsString());
                //add poster_path to responseJsonObject
                responseJsonObject.addProperty("poster_path", firstResult.get("poster_path").getAsString());
                //add vote_average to responseJsonObject
                responseJsonObject.addProperty("vote_average", firstResult.get("vote_average").getAsFloat());
                //add release_date to responseJsonObject
                responseJsonObject.addProperty("release_date", firstResult.get("release_date").getAsString());
                //add overview to responseJsonObject
                responseJsonObject.addProperty("overview", firstResult.get("overview").getAsString());
                //Initialize finalJSONresponse string
                String finalJSONresponse = gson.toJson(responseJsonObject);
                //set the content type toapplication/json
                response.setContentType("application/json");
                //write the response
                response.getWriter().println(finalJSONresponse);
                //add the elements to the document to insert into the db
                movieMateDocument.append("latency", latency).append("title", responseJsonObject.get("title").getAsString())
                        .append("poster_path", responseJsonObject.get("poster_path").getAsString())
                        .append("vote_average", responseJsonObject.get("vote_average").getAsString())
                        .append("release_date", responseJsonObject.get("release_date").getAsString())
                        .append("overview", responseJsonObject.get("overview").getAsString());
                //call insertMovieMateDocument to insert it to the collection "MovieMateData"
                db.insertMovieMateDocument(movieMateDocument);
            }
        }
        //catch clause
        catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


     /**
      searchMovieSends does a GET request to TMDb API to retrieve a list of popular movies that belong to the genre
      from the request parameter, then randomly selects one movie from the list and returns the selected movie's title,
      poster path, and vote average in JSON format. Similar to seaarchMovie methods it stores the log to the mongoDB
      using the insertMovieMateDocument helper method
     @param response the HttpServletResponse object
     @param genreString the genre string used to suggest a movie
     */
    public void suggestMovie(HttpServletResponse response, String genreString){
            try {
                //Get the corresponding genreCode from the getGenreMap method in genre
                String genreCode = genre.getGenreMap().get(genreString).toString();
                //Initialize the UTF encoded encodedgenreCode
                String encodedgenreCode = URLEncoder.encode(genreCode, "UTF-8");
                encodedgenreCode = encodedgenreCode.replace("+", "%20");
                //Initialise the TMDB URL to suggest a movie based on a genre
                URL url = new URL("https://api.themoviedb.org/3/discover/movie?api_key="+TMDB_key+"&language=en-US&sort_by=popularity.desc&include_adult=false&page=1&with_genres="+encodedgenreCode);
                //Initialze the start time
                long startTime = System.currentTimeMillis();
                //call the doThirdPartyAPICall method to get the JSON response from TMDB
                String responseText = doThirdPartyAPICall(url);
                //Initiaize the endTime
                long endTime = System.currentTimeMillis();
                //Calculate the latency
                long latency = endTime - startTime;
                //Initialize the GSON object
                Gson gson = new Gson();
                System.out.println(responseText);
                //Parse the responseText to JsonObject
                JsonObject jsonObject = gson.fromJson(responseText.toString(), JsonObject.class);
                //search for results and parse it to JsonArray
                JsonArray resultsArray = jsonObject.getAsJsonArray("results");
                //Create a Random object
                Random rand = new Random();
                //get any random movie for the response array
                int randomIndex = rand.nextInt(resultsArray.size());
                //send the response only when there is atleast one element in resultsArray
                if (resultsArray.size() != 0) {
                    //get the random movie information from the resultsArray
                    JsonObject randomResult = resultsArray.get(randomIndex).getAsJsonObject();
                    //Initialize the responseJsonObject
                    JsonObject responseJsonObject = new JsonObject();
                    //add title to the responseJSON
                    responseJsonObject.addProperty("title", randomResult.get("title").getAsString());
                    //add poster_path to the responseJSON
                    responseJsonObject.addProperty("poster_path", randomResult.get("poster_path").getAsString());
                    //add vote_average to the responseJSON
                    responseJsonObject.addProperty("vote_average", randomResult.get("vote_average").getAsFloat());
                    //Initialize the finalJSONresponse
                    String finalJSONresponse = gson.toJson(responseJsonObject);
                    //set the content type to application/json
                    response.setContentType("application/json");
                    response.getWriter().println(finalJSONresponse);
                    //Append the logs to the document
                    movieMateDocument.append("latency", latency).append("title", responseJsonObject.get("title").getAsString())
                            .append("poster_path", responseJsonObject.get("poster_path").getAsString())
                            .append("vote_average", responseJsonObject.get("vote_average").getAsString())
                            .append("release_date", "")
                            .append("overview", "");
                    //call insertMovieMateDocument to insert the document to the collection
                    db.insertMovieMateDocument(movieMateDocument);
                }
            //catch clause
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    /**
     * doThirdPartyAPICall is a helper method to open an HTTP connection
     * and get the GET response from the specified URL. getResponseBody is called to
     * get the responseText in String format
     * @param url
     * @return responseText
     */
    public String doThirdPartyAPICall(URL url){
        try {
            //create a HttpURLConnection
            conn = (HttpURLConnection) url.openConnection();
            //set the request method to GET
            conn.setRequestMethod("GET");
            //set the request property to accept application/json
            conn.setRequestProperty("Accept", "application/json");
            //get the responseText from getResponseBody method
            String responseText = getResponseBody(conn);
            //returns the responseText
            return responseText;
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Citation: Used this code from REST lab in REST_Client_Project
     *  This method gets the reponseText through BufferedReader and closes the
     *  connection after getting the response
     * @param conn
     * @return responseText
     */
    public static String getResponseBody(HttpURLConnection conn) {
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


    /**
     * showDashboard is a method for the dashboard endpoint.It gets the documents from the
     * collection parse it to a list of BSON document and calls the DashBoardAnalytics object methods
     * to set the analytics metrics. Finally, it set the request attributes and creates a dispatcher
     * and sets the dashboard.jsp
     * @param request
     * @param response
     */
    private void showDashboard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Retrieves all the documents from the collection and parses it to ArrayList
        List<Document> logs = db.getMovieMateCollection().find().into(new ArrayList<>());
        //calls the setAverageLatency method from dash object
        dash.setAverageLatency(db.getMovieMateCollection());
        //calls the setTopSearchMovie method from dash object
        dash.setTopSearchMovie(db.getMovieMateCollection());
        //call setTopSuggestionsAsked method from dash object
        dash.setTopSuggestionsAsked(db.getMovieMateCollection());
        //set logs to the attribute to display it in JSP file
        request.setAttribute("logs", logs);
        //set the averageLatency to the request
        request.setAttribute("averageLatency", dash.getAverageLatency());
        //set the topSearchMovie to the request
        request.setAttribute("topSearchMovie", dash.getTopSearchMovie());
        //set the topSuggestionsAsked to the request
        request.setAttribute("topSuggestionsAsked", dash.getTopSuggestionsAsked());
        //dispatcher to set the JSP file
        RequestDispatcher view = request.getRequestDispatcher("dashboard.jsp");
        //forward the request and response to the view
        view.forward(request, response);
    }
}


