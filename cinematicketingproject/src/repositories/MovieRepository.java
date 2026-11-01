package repositories;

import java.time.LocalDate;
import datastructures.*;
import models.Movie;
import utility.InputValidator;

public class MovieRepository {
    private LinkedList<Movie> movies;

    // Constructor (no ID needed, the repository starts with an empty list)
    public MovieRepository() {
        this.movies = new LinkedList<Movie>();
    }

    // Behavior methods

    // Rules: movie must not be null and its ID must not already be used.
    // Titles can repeat because the ID tells movies apart.
    // Returns true when added; throws IllegalArgumentException otherwise.
    public boolean addMovie(Movie movie) {
        if (movie == null) {
            throw new IllegalArgumentException("Invalid input. No movie was provided.");
        }
        for (int i = 0; i < movies.size(); i++) {
            if (movies.get(i).getMovieID().equals(movie.getMovieID())) {
                throw new IllegalArgumentException("Invalid input. A movie with ID " + movie.getMovieID() + " already exists.");
            }
        }
        movies.insertAtEnd(movie);
        return true;
    }

    // Returns every movie. Throws if there are none.
    public LinkedList<Movie> viewMovies() {
        if (movies.size() == 0) {
            throw new IllegalArgumentException("Invalid input. There are no movies yet.");
        }
        return movies;
    }

    // Rules: there must be movies and the title must not be blank.
    // Exact title match, case-insensitive. Returns the first match; throws if none.
    public Movie findMovie(String movieName) {
        if (movies.size() == 0) {
            throw new IllegalArgumentException("Invalid input. There are no movies yet.");
        }
        if (!InputValidator.isNotBlank(movieName)) {
            throw new IllegalArgumentException("Invalid input. Movie title cannot be blank.");
        }
        String name = movieName.trim();
        Node<Movie> node = movies.getHead();
        while (node != null) {
            if (node.getData().getTitle().equalsIgnoreCase(name)) {
                return node.getData();
            }
            node = node.getNext();
        }
        throw new IllegalArgumentException("Invalid input. Movie " + name + " was not found.");
    }

    // Rules: there must be movies, and one with that ID must exist.
    // Returns true when removed; throws IllegalArgumentException otherwise.
    public boolean removeMovie(String id) {
        if (movies.size() == 0) {
            throw new IllegalArgumentException("Invalid input. There are no movies to remove.");
        }
        if (id != null) {
            for (int i = 0; i < movies.size(); i++) {
                Movie movie = movies.get(i);
                if (movie.getMovieID().equalsIgnoreCase(id.trim())) {
                    movies.remove(movie);
                    return true;
                }
            }
        }
        throw new IllegalArgumentException("Invalid input. Movie " + id + " was not found.");
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

    // Next ID in the form MOV001, MOV002, ... (highest existing number + 1,
    // so removing a movie never causes an ID to be reused).
    public String generateMovieID() {
        int highest = 0;
        for (int i = 0; i < movies.size(); i++) {
            String id = movies.get(i).getMovieID();
            if (id.startsWith("MOV") && id.substring(3).matches("\\d+")) {
                int number = Integer.parseInt(id.substring(3));
                if (number > highest) {
                    highest = number;
                }
            }
        }
        return String.format("MOV%03d", highest + 1);
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