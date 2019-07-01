import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Users {
    public static void listUsers(){
        try {

            PreparedStatement ps = Main.db.prepareStatement("SELECT UserID, Username, UserType, Tags, Score FROM Users");

            ResultSet results = ps.executeQuery();
            while (results.next()) {
                int userID = results.getInt(1);
                String username = results.getString(2);
                String userType = results.getString(3);
                String tags = results.getString(4);
                int score = results.getInt(5);
                System.out.println("ID: " + userID + " Username: " + username + " Type: " + userType + " Tags: " + tags + " Score: " + score);
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    private static void deleteUser(String UserID) {
        try {
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Users where UserID == ?");
            ps.setString(1, UserID);
            ps.executeUpdate();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    private static void addNewUser(String username, String password, String UserType, String tags){
        try {

            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Users (Username, Password, UserType, Tags) VALUES (?, ?, ?, ?)");

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, UserType);
            ps.setString(4, tags);

            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    private static void updateUserPassword(int UserID, String newPassword){
        try {
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Users SET Password = ? where UserID = ?");
            ps.setInt(2, UserID);
            ps.setString(1, newPassword);
            ps.executeUpdate();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    private static void updateUserScore(int UserID, String newScore){
        try {
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Users SET Score = ? where UserID = ?");
            ps.setInt(2, UserID);
            ps.setString(1, newScore);
            ps.executeUpdate();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

}
