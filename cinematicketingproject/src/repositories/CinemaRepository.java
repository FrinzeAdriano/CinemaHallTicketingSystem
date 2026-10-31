package repositories;
import controllers.*;
import models.*;
import datastructures.*;
import view.*;
import utility.*;
import service.*;

public class CinemaRepository {

    private LinkedList cinemas = new LinkedList();


    // Cinema Management Methods

    public boolean addCinema(Cinema cinema) {
         return false;}

    public LinkedList viewCinemas() {
         return null;}

    public Cinema findCinema(String cinemaName) {
         return null;}

    public boolean removeCinema(String id) {
         return false;}


    // Schedule Methods

    public LinkedList getAllSchedules() {
         return null;}

    public LinkedList filterSchedule(String category, String value) {
         return null;}


    // ID Generation Method

    public String generateCinemaID() {
         return " ";}

}
