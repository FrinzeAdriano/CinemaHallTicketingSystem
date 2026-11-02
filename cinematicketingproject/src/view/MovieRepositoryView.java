package view;

import datastructures.LinkedList;
import datastructures.Node;
import java.util.ArrayList;
import models.Movie;

public class MovieRepositoryView extends BaseView {

    // Constructor (no ID needed, inherits the scanner from BaseView)
    public MovieRepositoryView() {
        super();
    }

    // Display methods (the controller decides which movies go in each list)

    public void displayMovieList(LinkedList<Movie> movies) {

    if (movies == null || movies.size() == 0) {
        showMessage("No movies available.");
        return;
    }

    String[] headers = {
        "ID", "Title", "Genre", "Duration", "Rating", "Release"
    };

    ArrayList<String[]> rows = new ArrayList<String[]>();

    for (int i = 0; i < movies.size(); i++) {

        Movie movie = movies.get(i);

        rows.add(new String[] {
            movie.getMovieID(),
            movie.getTitle(),
            movie.getGenre(),
            movie.getDuration() + " min",
            movie.getRating(),
            String.valueOf(movie.getReleaseDate())
        });
    }

    BaseView.printTable("ALL MOVIES", headers, rows);
}

    public void displayNowShowing(LinkedList<Movie> movies) {
        System.out.println();
        System.out.println("===== NOW SHOWING =====");
        System.out.printf("%-8s %-30s %-15s %-9s %-7s %-12s%n", "ID", "Title", "Genre", "Duration", "Rating", "Release");
        Node<Movie> node = movies.getHead();
        while (node != null) {
            Movie m = node.getData();
            System.out.printf("%-8s %-30s %-15s %-9s %-7s %-12s%n", m.getMovieID(), m.getTitle(), m.getGenre(),
                    m.getDuration() + " min", m.getRating(), m.getReleaseDate() == null ? "TBA" : m.getReleaseDate());
            node = node.getNext();
        }
    }

    public void displayComingSoon(LinkedList<Movie> movies) {
        System.out.println();
        System.out.println("===== COMING SOON =====");
        System.out.printf("%-8s %-30s %-15s %-9s %-7s %-12s%n", "ID", "Title", "Genre", "Duration", "Rating", "Release");
        Node<Movie> node = movies.getHead();
        while (node != null) {
            Movie m = node.getData();
            System.out.printf("%-8s %-30s %-15s %-9s %-7s %-12s%n", m.getMovieID(), m.getTitle(), m.getGenre(),
                    m.getDuration() + " min", m.getRating(), m.getReleaseDate() == null ? "TBA" : m.getReleaseDate());
            node = node.getNext();
        }
    }
}