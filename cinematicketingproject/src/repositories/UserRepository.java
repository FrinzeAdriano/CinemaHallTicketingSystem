package repositories;
import controllers.*;
import models.*;
import datastructures.*;
import view.*;
import utility.*;
import service.*;

public class UserRepository {

    private LinkedList users = new LinkedList();


    // User Management Methods

    public boolean addUser(User user) {
         return false;}

    public LinkedList viewUsers() {
         return null;}

    public User findUser(String userID) {
         return null;}

    public LinkedList filterByRole(String role) {
         return null;}

    public boolean deactivateUser(String userID) {
         return false;}

    public boolean reactivateUser(String userID) {
         return false;}

    public boolean deleteUser(String userID) {
         return false;}

    public String generateUserID(String role) {
         return " ";}
}
