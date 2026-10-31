package models;


import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;
import utility.*;
import service.*;

public class Cinema {
 
    private final String cinemaID;
    private String name;
    private String location;
    private LinkedList cinemaRooms = new LinkedList();
    
     public Cinema(String cinemaID, String name , String location , LinkedList cinemaRooms){
        this.cinemaID = cinemaID;
        this.name = name;
        this.location = location;
        this.cinemaRooms = cinemaRooms;
    }
    
    public boolean addRoom(CinemaRoom room){
        return false;
    }
    
    public boolean removeRoom(String roomID){
        return false;
    }
    
    public CinemaRoom findRoom(String roomID){
        return  null;
    }
        
//Setters=====================================
    public void setName(String name){
        this.name = name;}
    
    public void setLocation(String location){
        this.location = location;}
    
//Getters==========================================
    public String getCinemaID(){
        return cinemaID;}
    
    public String getName(){
        return name;}
    
    public String getLocation(){
        return location;}
    
    public LinkedList setCinemaRooms(){
        return cinemaRooms;}
    
}