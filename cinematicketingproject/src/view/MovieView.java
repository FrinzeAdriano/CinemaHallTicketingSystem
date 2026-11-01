package view;

import models.Movie;

public class MovieView extends BaseView {

    // Constructor (no ID needed, inherits the scanner from BaseView)
    public MovieView() {
        super();
    }

    // Menus and prompts

    public int showMovieMenu() {
        System.out.println();
        System.out.println("===== MOVIE MANAGEMENT =====");
        System.out.println("1. Add movie");
        System.out.println("2. Edit movie");
        System.out.println("3. Remove movie");
        System.out.println("4. Find movie by title");
        System.out.println("5. Filter movies");
        System.out.println("6. View all movies");
        System.out.println("0. Back");
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
        return readString("Enter movie ID: ");
    }

    public int showEditMovieMenu(Movie movie) {
        System.out.println();
        System.out.println("===== EDIT MOVIE " + movie.getMovieID() + " =====");
        System.out.println("1. Title        (" + movie.getTitle() + ")");
        System.out.println("2. Genre        (" + movie.getGenre() + ")");
        System.out.println("3. Duration     (" + movie.getDuration() + " min)");
        System.out.println("4. Rating       (" + movie.getRating() + ")");
        System.out.println("5. Description");
        System.out.println("6. Release date (" + (movie.getReleaseDate() == null ? "TBA" : movie.getReleaseDate()) + ")");
        System.out.println("0. Back");
        return readInt("Choose a field to edit: ", 0, 6);
    }

    public void displayMovie(Movie movie) {
        System.out.println();
        System.out.println("----- MOVIE DETAILS -----");
        System.out.println("ID           : " + movie.getMovieID());
        System.out.println("Title        : " + movie.getTitle());
        System.out.println("Genre        : " + movie.getGenre());
        System.out.println("Duration     : " + movie.getDuration() + " min");
        System.out.println("Rating       : " + movie.getRating());
        System.out.println("Release date : " + (movie.getReleaseDate() == null ? "TBA" : movie.getReleaseDate()));
        System.out.println("Description  : " + movie.getDescription());
    }
}