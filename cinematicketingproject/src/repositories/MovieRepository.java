package repositories;

import java.time.LocalDate;
import datastructures.LinkedList;
import models.Movie;
import utility.InputValidator;

public class MovieRepository {
    private LinkedList<Movie> movies;
    private int highestIssuedNumber;      // highest MOV number ever added, never goes down

    // Constructor (no ID needed, the repository starts with an empty list)
    public MovieRepository() {
        this.movies = new LinkedList<Movie>();
        this.highestIssuedNumber = 0;
    }

    // Behavior methods

    // Rules: movie must not be null and its ID must not already be used.
    // Titles can repeat because the ID tells movies apart.
    // Remembers the ID number so it is never issued again, even after the movie is removed.
    // Returns true when added; throws IllegalArgumentException otherwise.
    public boolean addMovie(Movie movie) {
        if (movie == null) {
            throw new IllegalArgumentException("Invalid input. No movie was provided.");
        }
        if (findByID(movie.getMovieID()) != null) {
            throw new IllegalArgumentException("Invalid input. A movie with ID " + movie.getMovieID() + " already exists.");
        }
        movies.insertAtEnd(movie);
        String id = movie.getMovieID();
        if (id.startsWith("MOV") && id.substring(3).matches("\\d+")) {
            int number = Integer.parseInt(id.substring(3));
            if (number > highestIssuedNumber) {
                highestIssuedNumber = number;
            }
        }
        return true;
    }

    // Returns every movie. Throws if there are none.
    public LinkedList<Movie> viewMovies() {
        if (movies.size() == 0) {
            throw new IllegalArgumentException("Invalid input. There are no movies yet.");
        }
        return movies;
    }

    // Exact title match, case-insensitive. Returns the first match, or null if none.
    public Movie findMovie(String movieName) {
        if (movieName == null) {
            return null;
        }
        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            if (movie.getTitle().equalsIgnoreCase(movieName.trim())) {
                return movie;
            }
        }
        return null;
    }

    // Exact ID match, case-insensitive. Returns null if no movie has that ID.
    public Movie findByID(String id) {
        if (id == null) {
            return null;
        }
        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            if (movie.getMovieID().equalsIgnoreCase(id.trim())) {
                return movie;
            }
        }
        return null;
    }

    // Rules: there must be movies, and one with that ID must exist.
    // Removing a movie does not lower highestIssuedNumber, so its ID is never reused.
    // Returns true when removed; throws IllegalArgumentException otherwise.
    public boolean removeMovie(String id) {
        if (movies.size() == 0) {
            throw new IllegalArgumentException("Invalid input. There are no movies to remove.");
        }
        if (!InputValidator.isValidMovieID(id)) {
            throw new IllegalArgumentException("Invalid input. Movie ID format MOV001.");
        }
        Movie movie = findByID(id);
        if (movie == null) {
            throw new IllegalArgumentException("Invalid input. Movie ID not found.");
        }
        movies.remove(movie);
        return true;
    }

    // Movies whose release date is after today. Movies with no release date are skipped.
    // Throws if there are none.
    public LinkedList<Movie> getComingSoon() {
        LinkedList<Movie> result = new LinkedList<Movie>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            if (movie.getReleaseDate() != null && movie.getReleaseDate().isAfter(today)) {
                result.insertAtEnd(movie);
            }
        }
        if (result.size() == 0) {
            throw new IllegalArgumentException("Invalid input. There are no coming soon movies.");
        }
        return result;
    }

    // Next ID in the form MOV001, MOV002, ... based on the highest number ever added,
    // so a removed movie's ID is never reused. Backing out of "Add movie" doesn't
    // use up a number, because the number is only recorded when the movie is added.
    public String generateMovieID() {
        return String.format("MOV%03d", highestIssuedNumber + 1);
    }

    // Categories: "GENRE", "RATING", "TITLE", "RELEASEDATE".
    // For RELEASEDATE the value is a range written as "yyyy-MM-dd to yyyy-MM-dd".
    // Throws for a blank input, an unknown category, a bad date range, or no matches.
    public LinkedList<Movie> filterMovie(String category, String value) {
        if (!InputValidator.isNotBlank(category) || !InputValidator.isNotBlank(value)) {
            throw new IllegalArgumentException("Invalid input. Filter category and value cannot be blank.");
        }
        String cat = category.trim().toUpperCase();
        if (cat.equals("GENRE")) {
            return filterByGenre(value);
        }
        if (cat.equals("RATING")) {
            return filterByRating(value);
        }
        if (cat.equals("TITLE")) {
            return filterByTitle(value);
        }
        if (cat.equals("RELEASEDATE")) {
            String[] parts = value.trim().split("\\s+to\\s+");
            if (parts.length != 2 || !InputValidator.isValidDate(parts[0]) || !InputValidator.isValidDate(parts[1])) {
                throw new IllegalArgumentException("Invalid input. Release date range must be yyyy-MM-dd to yyyy-MM-dd.");
            }
            return filterByReleaseDate(LocalDate.parse(parts[0].trim()), LocalDate.parse(parts[1].trim()));
        }
        throw new IllegalArgumentException("Invalid input. Movie filter category must be GENRE, RATING, TITLE, or RELEASEDATE.");
    }

    // Exact genre match, case-insensitive. Throws when nothing matches.
    public LinkedList<Movie> filterByGenre(String genre) {
        if (!InputValidator.isNotBlank(genre)) {
            throw new IllegalArgumentException("Invalid input. Genre cannot be blank.");
        }
        LinkedList<Movie> result = new LinkedList<Movie>();
        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            if (movie.getGenre().equalsIgnoreCase(genre.trim())) {
                result.insertAtEnd(movie);
            }
        }
        if (result.size() == 0) {
            throw new IllegalArgumentException("Invalid input. No movies found with genre " + genre.trim() + ".");
        }
        return result;
    }

    // Exact rating match, case-insensitive. Throws when nothing matches.
    public LinkedList<Movie> filterByRating(String rating) {
        if (!InputValidator.isNotBlank(rating)) {
            throw new IllegalArgumentException("Invalid input. Rating cannot be blank.");
        }
        LinkedList<Movie> result = new LinkedList<Movie>();
        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            if (movie.getRating().equalsIgnoreCase(rating.trim())) {
                result.insertAtEnd(movie);
            }
        }
        if (result.size() == 0) {
            throw new IllegalArgumentException("Invalid input. No movies found with rating " + rating.trim() + ".");
        }
        return result;
    }

    // Title contains the keyword, case-insensitive. Throws when nothing matches.
    public LinkedList<Movie> filterByTitle(String keyword) {
        if (!InputValidator.isNotBlank(keyword)) {
            throw new IllegalArgumentException("Invalid input. Search keyword cannot be blank.");
        }
        LinkedList<Movie> result = new LinkedList<Movie>();
        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            if (movie.getTitle().toLowerCase().contains(keyword.trim().toLowerCase())) {
                result.insertAtEnd(movie);
            }
        }
        if (result.size() == 0) {
            throw new IllegalArgumentException("Invalid input. No movies found with title containing " + keyword.trim() + ".");
        }
        return result;
    }

    // Duration from minMinutes to maxMinutes, inclusive.
    // Throws if the range is invalid or nothing matches.
    public LinkedList<Movie> filterByDuration(int minMinutes, int maxMinutes) {
        if (minMinutes < 0 || maxMinutes < minMinutes) {
            throw new IllegalArgumentException("Invalid input. Minimum duration must be 0 or more and not greater than the maximum.");
        }
        LinkedList<Movie> result = new LinkedList<Movie>();
        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            if (InputValidator.inRange(movie.getDuration(), minMinutes, maxMinutes)) {
                result.insertAtEnd(movie);
            }
        }
        if (result.size() == 0) {
            throw new IllegalArgumentException("Invalid input. No movies found between " + minMinutes + " and " + maxMinutes + " minutes.");
        }
        return result;
    }

    // Release date from "from" to "to", inclusive. Movies with no release date are skipped.
    // Throws if either date is missing, "from" is after "to", or nothing matches.
    public LinkedList<Movie> filterByReleaseDate(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Invalid input. Both dates are required.");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Invalid input. The start date cannot be after the end date.");
        }
        LinkedList<Movie> result = new LinkedList<Movie>();
        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            LocalDate date = movie.getReleaseDate();
            if (date != null && !date.isBefore(from) && !date.isAfter(to)) {
                result.insertAtEnd(movie);
            }
        }
        if (result.size() == 0) {
            throw new IllegalArgumentException("Invalid input. No movies found released between " + from + " and " + to + ".");
        }
        return result;
    }
}