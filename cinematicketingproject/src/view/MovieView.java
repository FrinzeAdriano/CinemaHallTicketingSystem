package view;

import java.util.ArrayList;
import models.Movie;

public class MovieView extends BaseView {

    // Constructor (no ID needed, inherits the scanner from BaseView)
    public MovieView() {
        super();
    }

    // Menus and prompts

    public int showMovieMenu() {
        System.out.println();
        System.out.println("=========== MOVIE MANAGEMENT ===========");
        System.out.println("[1] Add movie");
        System.out.println("[2] Edit movie");
        System.out.println("[3] Remove movie");
        System.out.println("[4] Find movie by title");
        System.out.println("[5] Filter movies");
        System.out.println("[6] View all movies");
        System.out.println("[0] Back");
        System.out.println("----------------------------------------");
        return readInt("Choose an option: ", 0, 6);
    }

    // Reads the raw fields and passes them to the setters. A setter that rejects a value
    // throws IllegalArgumentException; the controller catches it and shows e.getMessage().
    public Movie promptNewMovie(String movieID) {
        Movie movie = new Movie(movieID);
        movie.setTitle(readString("Title: "));
        movie.setGenre(readString("Genre: "));
        movie.setDuration(readString("Duration (minutes): "));
        movie.setRating(readString("Rating (G, PG, R-13, R-16, R-18): "));
        movie.setDescription(readString("Description: "));
        movie.setReleaseDate(readString("Release date (yyyy-MM-dd): "));
        return movie;
    }

    public String promptMovieID() {
        return readString("Enter movie ID (0 to go back): ");
    }

    private static final String[] RATING_CHOICES = {"G", "PG", "R-13", "R-16", "R-18"};
    private static final String[] GENRE_CHOICES = {"Action", "Comedy", "Drama", "Horror", "Romance", "Sci-Fi", "Thriller", "Animation"};

    public int showEditMovieMenu(Movie movie) {
        String title = " EDIT MOVIE " + movie.getMovieID() + " ";
        int fill = Math.max(40 - title.length(), 2);
        System.out.println();
        System.out.println("=".repeat(fill / 2) + title + "=".repeat(fill - fill / 2));
        System.out.println("[1] Title        (" + movie.getTitle() + ")");
        System.out.println("[2] Genre        (" + movie.getGenre() + ")");
        System.out.println("[3] Duration     (" + movie.getDuration() + " min)");
        System.out.println("[4] Rating       (" + movie.getRating() + ")");
        String description = movie.getDescription();
        System.out.println("[5] Description  (" + description + ")");
        System.out.println("[6] Release date (" + (movie.getReleaseDate() == null ? "TBA" : movie.getReleaseDate()) + ")");
        System.out.println("[0] Back");
        System.out.println("----------------------------------------");
        return readInt("Choose a field to edit: ", 0, 6);
    }

    // Display methods

    public void displayMovie(Movie movie) {
        String[] headers = {"Field", "Details"};
        ArrayList<String[]> rows = new ArrayList<>();
        rows.add(new String[] {"ID", movie.getMovieID()});
        rows.add(new String[] {"Title", movie.getTitle()});
        rows.add(new String[] {"Genre", movie.getGenre()});
        rows.add(new String[] {"Duration", movie.getDuration() + " min"});
        rows.add(new String[] {"Rating", movie.getRating()});
        rows.add(new String[] {"Release date", movie.getReleaseDate() == null ? "TBA" : movie.getReleaseDate().toString()});
        rows.add(new String[] {"Description", movie.getDescription()});
        printTable("MOVIE DETAILS", headers, rows);
    }
    
    // Returns the chosen rating, or "0" to go back.
    public String promptRating() {
        System.out.println();
        System.out.println("============= SELECT RATING =============");
        for (int i = 0; i < RATING_CHOICES.length; i++) {
            System.out.println("[" + (i + 1) + "] " + RATING_CHOICES[i]);
        }
        System.out.println("[0] Back");
        System.out.println("----------------------------------------");
        int choice = readInt("Choose a rating: ", 0, RATING_CHOICES.length);
        return choice == 0 ? "0" : RATING_CHOICES[choice - 1];
    }

    // Returns the genre text, or "0" to go back. "Others" asks the user to type the genre.
    public String promptGenre() {
        int othersNumber = GENRE_CHOICES.length + 1;
        System.out.println();
        System.out.println("============== SELECT GENRE =============");
        for (int i = 0; i < GENRE_CHOICES.length; i++) {
            System.out.println("[" + (i + 1) + "] " + GENRE_CHOICES[i]);
        }
        System.out.println("[" + othersNumber + "] Others (type your own)");
        System.out.println("[0] Back");
        System.out.println("----------------------------------------");
        int choice = readInt("Choose a genre: ", 0, othersNumber);
        if (choice == 0) {
            return "0";
        }
        if (choice == othersNumber) {
            return readString("Enter genre (0 to go back): ");
        }
        return GENRE_CHOICES[choice - 1];
    }
}