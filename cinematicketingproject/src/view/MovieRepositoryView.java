package view;

import datastructures.LinkedList;
import datastructures.Node;
import models.Movie;

public class MovieRepositoryView extends BaseView {

    // Constructor (no ID needed, inherits the scanner from BaseView)
    public MovieRepositoryView() {
        super();
    }

    // Display methods (the controller decides which movies go in each list)

    public void displayMovieList(LinkedList<Movie> movies) {
        System.out.println();
        System.out.println("===== ALL MOVIES =====");
        System.out.printf("%-8s %-30s %-15s %-9s %-7s %-12s%n", "ID", "Title", "Genre", "Duration", "Rating", "Release");
        Node<Movie> node = movies.getHead();
        while (node != null) {
            Movie m = node.getData();
            System.out.printf("%-8s %-30s %-15s %-9s %-7s %-12s%n", m.getMovieID(), m.getTitle(), m.getGenre(),
                    m.getDuration() + " min", m.getRating(), m.getReleaseDate() == null ? "TBA" : m.getReleaseDate());
            node = node.getNext();
        }
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