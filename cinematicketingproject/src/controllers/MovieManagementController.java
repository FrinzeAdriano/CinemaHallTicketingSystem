package controllers;

import repositories.MovieRepository;
import view.MovieView;

public class MovieManagementController {
    private MovieRepository movieRepo;
    private MovieView movieView;

    public MovieManagementController(MovieRepository movieRepo, MovieView movieView) {
        this.movieRepo = movieRepo;
        this.movieView = movieView;
    }

    public void manageMovies() {
    }
}
