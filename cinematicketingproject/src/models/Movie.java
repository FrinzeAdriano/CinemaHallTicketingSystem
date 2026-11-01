package models;

import java.time.LocalDate;
import utility.InputValidator;

public class Movie {
    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_GENRE_LENGTH = 30;
    private static final int MAX_DESCRIPTION_LENGTH = 500;
    private static final int MIN_DURATION = 1;
    private static final int MAX_DURATION = 600;
    private static final String[] VALID_RATINGS = {"G", "PG", "R-13", "R-16", "R-18"};
    private static final String RATING_OPTIONS = "G, PG, R-13, R-16, R-18";

    private final String movieID;
    private String title;
    private String genre;
    private int duration;
    private String rating;
    private String description;
    private LocalDate releaseDate;

    // Constructor (ID is required because the table has no setter for it)
    public Movie(String movieID) {
        this.movieID = movieID;
        this.title = "TBA";
        this.genre = "TBA";
        this.duration = 0;
        this.rating = "TBA";
        this.description = "TBA";
        this.releaseDate = null;
    }

    // Setters (business rules live here).
    // An invalid value throws IllegalArgumentException with the error message;
    // the controller catches it and passes e.getMessage() to the view.

    // Title: not blank, at most 100 characters.
    public void setTitle(String title) {
        if (!InputValidator.isNotBlank(title)) {
            throw new IllegalArgumentException("Invalid input. Title cannot be blank.");
        }
        String cleaned = title.trim();
        if (!InputValidator.inRange(cleaned.length(), 1, MAX_TITLE_LENGTH)) {
            throw new IllegalArgumentException("Invalid input. Title must be at most " + MAX_TITLE_LENGTH + " characters.");
        }
        this.title = cleaned;
    }

    // Genre: not blank, at most 30 characters, letters/spaces/hyphens only.
    public void setGenre(String genre) {
        if (!InputValidator.isNotBlank(genre)) {
            throw new IllegalArgumentException("Invalid input. Genre cannot be blank.");
        }
        String cleaned = genre.trim();
        if (!InputValidator.inRange(cleaned.length(), 1, MAX_GENRE_LENGTH)) {
            throw new IllegalArgumentException("Invalid input. Genre must be at most " + MAX_GENRE_LENGTH + " characters.");
        }
        for (int i = 0; i < cleaned.length(); i++) {
            char c = cleaned.charAt(i);
            if (!Character.isLetter(c) && c != ' ' && c != '-') {
                throw new IllegalArgumentException("Invalid input. Genre can only contain letters, spaces, and hyphens.");
            }
        }
        this.genre = cleaned;
    }

    // Duration (minutes): must be a whole number, between 1 and 600.
    public void setDuration(String duration) {
        if (!InputValidator.isPositiveInt(duration)) {
            throw new IllegalArgumentException("Invalid input. Duration must be a whole number.");
        }
        int minutes = Integer.parseInt(duration.trim());
        if (!InputValidator.inRange(minutes, MIN_DURATION, MAX_DURATION)) {
            throw new IllegalArgumentException(
                "Invalid input. Duration must be between " + MIN_DURATION + " and " + MAX_DURATION + " minutes.");
        }
        this.duration = minutes;
    }

    // Rating: must be one of G, PG, R-13, R-16, R-18 (case-insensitive, stored uppercase).
    public void setRating(String rating) {
        if (!InputValidator.isNotBlank(rating)) {
            throw new IllegalArgumentException("Invalid input. Rating cannot be blank.");
        }
        String cleaned = rating.trim().toUpperCase();
        for (int i = 0; i < VALID_RATINGS.length; i++) {
            if (VALID_RATINGS[i].equals(cleaned)) {
                this.rating = cleaned;
                return;
            }
        }
        throw new IllegalArgumentException("Invalid input. Rating must be one of: " + RATING_OPTIONS + ".");
    }

    // Description: not blank, at most 500 characters.
    public void setDescription(String description) {
        if (!InputValidator.isNotBlank(description)) {
            throw new IllegalArgumentException("Invalid input. Description cannot be blank.");
        }
        String cleaned = description.trim();
        if (!InputValidator.inRange(cleaned.length(), 1, MAX_DESCRIPTION_LENGTH)) {
            throw new IllegalArgumentException(
                "Invalid input. Description must be at most " + MAX_DESCRIPTION_LENGTH + " characters.");
        }
        this.description = cleaned;
    }

    // Release date: must be a valid yyyy-MM-dd date, today or later.
    public void setReleaseDate(String releaseDate) {
        if (!InputValidator.isValidDate(releaseDate)) {
            throw new IllegalArgumentException("Invalid input. Release date must be a valid date in yyyy-MM-dd format.");
        }
        LocalDate parsed = LocalDate.parse(releaseDate.trim());
        if (parsed.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid input. Release date cannot be in the past.");
        }
        this.releaseDate = parsed;
    }

    // Getters
    public String getMovieID() {
        return movieID;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public int getDuration() {
        return duration;
    }

    public String getRating() {
        return rating;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }
}