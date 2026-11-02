package controllers;

import models.*;
import datastructures.*;
import controllers.*;
import repositories.*;
import view.*;
import utility.*;

public class MovieManagementController {
    private MovieRepository movieRepo;
    private MovieView movieView;
    private MovieRepositoryView movieListView;

    public MovieManagementController(MovieRepository movieRepo, MovieView movieView,
            MovieRepositoryView movieListView) {
        this.movieRepo = movieRepo;
        this.movieView = movieView;
        this.movieListView = movieListView;
    }

    public void manageMovies() {
        int choice = -1;
        while (choice != 0) {
            choice = movieView.showMovieMenu();
            try {
                switch (choice) {
                    case 1: {
                        // Add movie (0 at any prompt goes back without adding)
                        Movie movie = new Movie(movieRepo.generateMovieID());
                        if (LoopUtility.retryUntilValid(movieView::promptGenre, movie::setGenre, movieView::showError, "0")
                                && LoopUtility.retryUntilValid(movieView::promptRating, movie::setRating, movieView::showError, "0")
                                && LoopUtility.retryUntilValid(() -> movieView.readString("Title (0 to go back): "), movie::setTitle, movieView::showError, "0")
                                && LoopUtility.retryUntilValid(() -> movieView.readString("Duration in minutes (0 to go back): "), movie::setDuration, movieView::showError, "0")
                                && LoopUtility.retryUntilValid(() -> movieView.readString("Description (0 to go back): "), movie::setDescription, movieView::showError, "0")
                                && LoopUtility.retryUntilValid(() -> movieView.readString("Release date, yyyy-MM-dd (0 to go back): "), movie::setReleaseDate, movieView::showError, "0")) {
                            movieRepo.addMovie(movie);
                            movieView.showMessage("Movie " + movie.getMovieID() + " was added.");
                        }
                        break;
                    }
                    case 2: {
                        // Edit movie (found by movie ID, 0 to go back)
                        movieListView.displayMovieList(movieRepo.viewMovies());
                        Movie movie = LoopUtility.retryUntilValidResult(movieView::promptMovieID,
                                id -> {
                                if (!InputValidator.isValidMovieID(id)) {
                                    throw new IllegalArgumentException("Invalid input. Movie ID format MOV001.");
                                }
                                Movie match = movieRepo.findByID(id);
                                if (match == null) {
                                    throw new IllegalArgumentException("Invalid input. Movie " + id.trim().toUpperCase() + " was not found.");
                                }
                                return match;
                            },
                                movieView::showError, "0");
                        int editChoice = (movie == null) ? 0 : -1;
                        while (editChoice != 0) {
                            editChoice = movieView.showEditMovieMenu(movie);
                            switch (editChoice) {
                                case 1:
                                    if (LoopUtility.retryUntilValid(() -> movieView.readString("Title (0 to go back): "), movie::setTitle, movieView::showError, "0")) {
                                        movieView.showMessage("Title updated.");
                                    }
                                    break;
                                case 2:
                                    if (LoopUtility.retryUntilValid(movieView::promptGenre, movie::setGenre, movieView::showError, "0")) {
                                        movieView.showMessage("Genre updated.");
                                    }
                                    break;
                                case 3:
                                    if (LoopUtility.retryUntilValid(() -> movieView.readString("Duration in minutes (0 to go back): "), movie::setDuration, movieView::showError, "0")) {
                                        movieView.showMessage("Duration updated.");
                                    }
                                    break;
                                case 4:
                                    if (LoopUtility.retryUntilValid(movieView::promptRating, movie::setRating, movieView::showError, "0")) {
                                        movieView.showMessage("Rating updated.");
                                    }
                                    break;
                                case 5:
                                    if (LoopUtility.retryUntilValid(() -> movieView.readString("Description (0 to go back): "), movie::setDescription, movieView::showError, "0")) {
                                        movieView.showMessage("Description updated.");
                                    }
                                    break;
                                case 6:
                                    if (LoopUtility.retryUntilValid(() -> movieView.readString("Release date, yyyy-MM-dd (0 to go back): "), movie::setReleaseDate, movieView::showError, "0")) {
                                        movieView.showMessage("Release date updated.");
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                    }
                    case 3: {
                        // Remove movie (by ID, 0 to go back). The confirmation is asked only after
                        // the ID has passed validation and the movie has been found.
                        movieListView.displayMovieList(movieRepo.viewMovies());
                        Movie target = LoopUtility.retryUntilValidResult(movieView::promptMovieID,
                                id -> {
                                    if (!InputValidator.isValidMovieID(id)) {
                                        throw new IllegalArgumentException("Invalid input. Movie ID format MOV001.");
                                    }
                                    Movie match = movieRepo.findByID(id);
                                    if (match == null) {
                                        throw new IllegalArgumentException("Invalid input. Movie " + id.trim().toUpperCase() + " was not found.");
                                    }
                                    return match;
                                },
                                movieView::showError, "0");
                        if (target != null) {
                            if (movieView.confirm("Remove movie " + target.getMovieID() + " (" + target.getTitle() + ")?")) {
                                movieRepo.removeMovie(target.getMovieID());
                                movieView.showMessage("Movie removed.");
                            } else {
                                movieView.showMessage("Removal cancelled.");
                            }
                        }
                        break;
                    }
                    case 4: {
                        // Find movie by title (0 to go back)
                        movieListView.displayMovieList(movieRepo.viewMovies());
                        Movie found = LoopUtility.retryUntilValidResult(() -> movieView.readString("Enter movie title (0 to go back): "),
                                movieRepo::findMovie, movieView::showError, "0");
                        if (found != null) {
                            movieView.displayMovie(found);
                        }
                        break;
                    }
                    case 5: {
                        // Filter movies
                        movieRepo.viewMovies();   // throws if there are no movies
                        String category = null;
                        switch (movieView.readInt("Filter by 1 Genre, 2 Rating, 3 Title, 4 Release date range, 0 Back: ", 0, 4)) {
                            case 1:
                                category = "GENRE";
                                break;
                            case 2:
                                category = "RATING";
                                break;
                            case 3:
                                category = "TITLE";
                                break;
                            case 4:
                                category = "RELEASEDATE";
                                break;
                            default:
                                break;
                        }
                        if (category != null) {
                            final String filterCategory = category;
                            final String hint = filterCategory.equals("RELEASEDATE") ? "yyyy-MM-dd to yyyy-MM-dd" : filterCategory.toLowerCase();
                            LinkedList<Movie> found = LoopUtility.retryUntilValidResult(
                                    () -> movieView.readString("Enter " + hint + " (0 to go back): "),
                                    value -> movieRepo.filterMovie(filterCategory, value),
                                    movieView::showError, "0");
                            if (found != null) {
                                movieListView.displayMovieList(found);
                            }
                        }
                        break;
                    }
                    case 6:
                        // View all movies
                        movieListView.displayMovieList(movieRepo.viewMovies());
                        break;
                    default:
                        break;   // 0 exits the loop
                }
            } catch (IllegalArgumentException e) {
                movieView.showError(e.getMessage());
            }
        }
    }
}