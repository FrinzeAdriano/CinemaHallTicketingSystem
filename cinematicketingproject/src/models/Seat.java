package models;
import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;
import utility.*;
import service.*;

public class Seat {
    
    private final String seatID;
    private char seatRow;
    private int seatNumber;
    private String seatType;
    private boolean status;
    private double priceModifier;
    
    public Seat(String seatID, char seatRow, int seatNumber, String seatType, boolean status, double priceModifier){
    this.seatID = seatID;
    this.seatRow = seatRow;
    this.seatNumber = seatNumber;
    this.seatType = seatType;
    this.status = status;
    this.priceModifier = priceModifier;   
    }
    
    
    //Setters=====================
    
    public void setSeatType(String seatType){
        this.seatType = seatType;
    }
    
    public void setSeatType(boolean status){
        this.status = status;
    }
    
    public void setPriceModifier(double priceModifier){
        this.priceModifier = priceModifier;
    }
    //getters=====================
    public String getSeatID(){
        return seatID;}
    
    public char getSeatRow(){
        return seatRow;}
    
    public int getSeatNumber(){
        return seatNumber;}
    
    public String getSeatType(){
        return seatType;}
    
    public boolean getStatus(){
        return status;}
    
    public double getPriceModifier(){
        return priceModifier;}
    
    
    
    
}
