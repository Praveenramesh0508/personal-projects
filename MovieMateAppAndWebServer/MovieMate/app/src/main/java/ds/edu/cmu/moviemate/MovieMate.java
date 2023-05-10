/**
 * Author: Praveen Ramesh
 * Andrew ID: pramesh2@andrew.cmu.edu
 * The MovieMate class is an Android activity that offers two tabs to search movies
 * and obtain movie suggestions based on the requested genre. It extends the AppCompatActivity class
 * and overrides the onCreate method to create the activity and manage user input. It interacts with
 * the GetMovie class to retrieve movie information from deployed web service. The search tab enables
 * users to input a movie name and access movie information that matches the term. The suggestion tab
 * provides users with an option to select a genre and obtain suggestion of a movie within that genre.
 * The class also has two methods, searchReady and suggestionReady, that receive calls from the GetMovie
 * class to update widgets the UI with retrieved movie information.
 * Referred Android Lab of Interesting picture for implementation
 */
//import the required packages
package ds.edu.cmu.moviemate;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
//A public MovieMate that extends AppCompatActivity
public class MovieMate extends AppCompatActivity {
    //Create a reference to this MovieMate object
    MovieMate me = this;
    //Declare a variable selectedGenre
    private String selectedGenre;

    //Override onCreate to create the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //call the super onCreate with savedInstanceState
        super.onCreate(savedInstanceState);
        //Set the layout of the activity to activity_main layout
        setContentView(R.layout.activity_main);
        //Create a reference of this MovieMate object
        final MovieMate ma = this;
        //Create a reference to the submit button for searching
        Button submitButtonSearch = (Button)findViewById(R.id.submit);

        //Set an OnClickListener for the submit button on the search tab
        submitButtonSearch.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {
                String searchMovie = ((EditText)findViewById(R.id.searchTerm)).getText().toString();
                //Get the movie entered by the user in the android application
                //Create a new GetMovie object
                GetMovie gp = new GetMovie();
                //Call the GetMovie's search method with the searchMovie and other reference variables
                gp.search(1,searchMovie, me, ma);
            }
        });

        //Create a reference to the genre spinner on the suggestion tab
        Spinner genreSpinner = findViewById(R.id.genre_spinner);
        //Create an ArrayAdapter using the movie_genres array resource and set it as the adapter for the genre spinner
        //Referred https://developer.android.com/reference/android/widget/ArrayAdapter documentation for the syntax
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.movie_genres, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(adapter);

        //Set an OnItemSelectedListener for the genre spinner
        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Store the selected genre in the selectedGenre variable
                selectedGenre = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Create a reference to the submit button on the suggestion tab
        Button submitSuggestButton = findViewById(R.id.submitSuggestButton);
        //Set an OnClickListener for the submit button on the suggestion tab
        submitSuggestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check whether a genre has been selected from the spinner
                if (selectedGenre != null) {
                    //Create a GetMovie object
                    GetMovie gp = new GetMovie();
                    //Call the GetMovie's suggest method
                    gp.suggest(2,me, ma,selectedGenre);
                    // Display an error message if a genre is not selected
                } else {
                    Toast.makeText(MovieMate.this, "Please select a genre", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * searchReady method is called by the GetMovie class to update the UI with movie information when a search is performed.
     * It takes a Bitmap image of the poster,the movie title,movie's overview, and release date as parameters and
     * updates the UI widgets with the information.
     * @param poster picture of the poster
     * @param titleString title of the movie
     * @param overviewString Overview of the movie
     * @param releaseDateString Release date of the movie
     */
    public void searchReady(Bitmap poster,String titleString,String overviewString,String releaseDateString) {
        //Create a reference to the searchPicture
        ImageView pictureView = (ImageView)findViewById(R.id.searchPicture);
        //Create a reference to the searchTerm
        TextView searchView = (EditText)findViewById(R.id.searchTerm);
        //Create a reference to the searchTitleView
        TextView showTitle= (TextView) findViewById(R.id.searchTitleView);
        //Create a reference to the overView
        TextView overView= (TextView) findViewById(R.id.overView);
        //Create a reference to the releaseDateView
        TextView  releaseDate= (TextView) findViewById(R.id.releaseDateView);
        //Check if the poster bitmap is not null
        if (poster != null) {
            //set the picture view with the poster image
            pictureView.setImageBitmap(poster);
            //set the information about the movie with title
            showTitle.setText("Here is the information about the movie "+ titleString);
            //set the overview text about the movie
            overView.setText(overviewString);
            //set the release date of the movie
            releaseDate.setText("Release date : "+releaseDateString);
            //change the visibility of the UI elements
            showTitle.setVisibility(View.VISIBLE);
            overView.setVisibility(View.VISIBLE);
            releaseDate.setVisibility(View.VISIBLE);
            pictureView.setVisibility(View.VISIBLE);
            //If there is no image set the error message
        } else {
            //set the image as android robot
            pictureView.setImageResource(R.mipmap.ic_launcher);
            //set the error message in the title view
            showTitle.setText("We're sorry, but we couldn't find any movies matching your search criteria - "+searchView.getText());
            //Change the visibility of the UI elements
            pictureView.setVisibility(View.VISIBLE);
            showTitle.setVisibility(View.VISIBLE);
            overView.setVisibility(View.INVISIBLE);
            releaseDate.setVisibility(View.INVISIBLE);
        }
        //set the searchView to empty for further searches
        searchView.setText("");
        //invalidate the pictureView
        pictureView.invalidate();
    }

    /**
     * suggestionReady method is called by the GetMovie class to update the UI with movie information when a suggestion is made.
     * It takes a Bitmap image of the poster, the suggested movie title, and rating as parameters and updates the
     * appropriate UI elements with these information.
     * @param poster poster of the suggested movie
     * @param titleString title of the suggested movie
     * @param rating rating of the suggested movie
     */
    public void suggestionReady(Bitmap poster,String titleString,String rating){
            //Create a reference to the suggestionPicture
            ImageView pictureView= (ImageView) findViewById(R.id.suggestionPicture);
            //Create a reference to the suggestionTitleView
            TextView suggestionTitleView=(TextView) findViewById(R.id.suggestionTitleView);
            //Create a reference to the suggestionRating
            TextView suggestionRating = (TextView) findViewById(R.id.suggestionRating);
        //Check if the poster bitmap is not null
        if (poster != null) {
            //set the picture view with the poster image
            pictureView.setImageBitmap(poster);
            //Set the title of the suggested movie
            suggestionTitleView.setText("Title : "+titleString);
            //set the rating of the suggested movie
            suggestionRating.setText("Average Rating : "+rating);
            //change the visibility of the UI elements
            pictureView.setVisibility(View.VISIBLE);
            suggestionTitleView.setVisibility(View.VISIBLE);
            suggestionRating.setVisibility(View.VISIBLE);
        //If there is no image set the error message
        } else
        {   //set the image as android robot
            pictureView.setImageResource(R.mipmap.ic_launcher);
            //set the error message in the title view
            suggestionTitleView.setText("We're sorry, but we couldn't find any movies matching your suggestion criteria - "+selectedGenre);
            //change the visibility of the UI elements
            suggestionTitleView.setVisibility(View.VISIBLE);
            pictureView.setVisibility(View.VISIBLE);
            suggestionRating.setVisibility(View.INVISIBLE);
        }
        //invalidate the pictureView
        pictureView.invalidate();
    }
}
