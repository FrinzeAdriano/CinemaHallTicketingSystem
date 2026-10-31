package repositories;
import controllers.*;
import models.*;
import datastructures.*;
import java.time.LocalDate;
import view.*;
import utility.*;
import service.*;

public class MovieRepository {

    private LinkedList movies = new LinkedList();


    // Movie Management Methods

    public boolean addMovie(Movie movie) {
         return false;}

    public LinkedList viewMovies() {
         return null;}

    public Movie findMovie(String movieName) {
         return null;}

    public boolean removeMovie(String id) {
         return false;}

    public LinkedList getComingSoon() {
         return null;}

    public String generateMovieID() {
         return " ";}


    // Movie Filtering Methods

    public LinkedList filterMovie(String category, String value) {
         return null;}

    public LinkedList filterByGenre(String genre) {
         return null;}

    public LinkedList filterByRating(String rating) {
         return null;}

    public LinkedList filterByTitle(String keyword) {
         return null;}

    public LinkedList filterByDuration(int minMinutes, int maxMinutes) {
         return null;}

    public LinkedList filterByReleaseDate(LocalDate from, LocalDate to) {
         return null;}

}
