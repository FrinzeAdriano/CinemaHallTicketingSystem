
package models;

import java.time.LocalDate;
import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;
import utility.*;
import service.*;

public class Movie {
    
    private final String movieID;
    private String title;
    private String genre;
    private int duration;
    private String rating;
    private String description;
    private LocalDate releaseDate;
    
    public Movie(String movieID, String title, String genre, int duration, String rating, String description, LocalDate releaseDate){
        this.movieID = movieID;
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.rating =  rating;
        this.description =  description;
        this.releaseDate =  releaseDate; 
    }
    
    //Setters==========================
    public void setTitle(String title){
        this.title = title;}
    
    public void setGenre(String genre){
        this.genre = genre;}
    
    public void setDuration(int duration){
        this.duration = duration;}
    
    public void setRating(String rating){
        this.rating = rating;}
    
    public void setDescription(String description){
        this.description = description;}
    
    public void setReleaseDate(LocalDate releaseDate){
        this.releaseDate = releaseDate;}
    
    //getters======================
    public String getMovieID(){
        return movieID;}
    
    public String getTitle(){
        return title;}
    
    public String getGenre(){
        return genre;}
    
    public int getDuration(){
        return duration;}
    
    public String getRating(){
        return rating;}
    
    public String getDescription(){
        return description;}
    
    public LocalDate getReleaseDate(){
        return releaseDate;}
    
    
}
