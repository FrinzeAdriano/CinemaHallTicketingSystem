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
        String[] headers = {"ID", "Title", "Genre", "Duration", "Rating", "Release", "Description"};
        ArrayList<String[]> rows = new ArrayList<>();
        Node<Movie> node = movies.getHead();
        while (node != null) {
            Movie m = node.getData();
            String description = m.getDescription();
            rows.add(new String[] {m.getMovieID(), m.getTitle(), m.getGenre(), m.getDuration() + " min",
                    m.getRating(), m.getReleaseDate() == null ? "TBA" : m.getReleaseDate().toString(), description});
            node = node.getNext();
        }
        printTable("ALL MOVIES", headers, rows);
    }

    public void displayNowShowing(LinkedList<Movie> movies) {
        String[] headers = {"ID", "Title", "Genre", "Duration", "Rating", "Release", "Description"};
        ArrayList<String[]> rows = new ArrayList<>();
        Node<Movie> node = movies.getHead();
        while (node != null) {
            Movie m = node.getData();
            String description = m.getDescription();
            rows.add(new String[] {m.getMovieID(), m.getTitle(), m.getGenre(), m.getDuration() + " min",
                    m.getRating(), m.getReleaseDate() == null ? "TBA" : m.getReleaseDate().toString(), description});
            node = node.getNext();
        }
        printTable("NOW SHOWING", headers, rows);
    }

    public void displayComingSoon(LinkedList<Movie> movies) {
        String[] headers = {"ID", "Title", "Genre", "Duration", "Rating", "Release", "Description"};
        ArrayList<String[]> rows = new ArrayList<>();
        Node<Movie> node = movies.getHead();
        while (node != null) {
            Movie m = node.getData();
            String description = m.getDescription();
            rows.add(new String[] {m.getMovieID(), m.getTitle(), m.getGenre(), m.getDuration() + " min",
                    m.getRating(), m.getReleaseDate() == null ? "TBA" : m.getReleaseDate().toString(), description});
            node = node.getNext();
        }
        printTable("COMING SOON", headers, rows);
    }
}