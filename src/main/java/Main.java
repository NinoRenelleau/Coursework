import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Main {

    public static Connection db = null;

    public static void main(String[] args) {
        openDatabase("Project.db");
// code to get data from, write to the database etc goes here!
        addNew();
        delete();
        update();
        lookAt();
        closeDatabase();
    }

    private static void openDatabase(String dbFile) {
        try  {
            Class.forName("org.sqlite.JDBC");
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            db = DriverManager.getConnection("jdbc:sqlite:resources/" + dbFile, config.toProperties());
            System.out.println("Database connection successfully established.");
        } catch (Exception exception) {
            System.out.println("Database connection error: " + exception.getMessage());
        }

    }

    private static void closeDatabase(){
        try {
            db.close();
            System.out.println("Disconnected from database.");
        } catch (Exception exception) {
            System.out.println("Database disconnection error: " + exception.getMessage());
        }
    }

    private static void addNew(){
        try {

            PreparedStatement ps = db.prepareStatement("INSERT INTO UserAccount (Username, password, Score) VALUES (?, ?, ?)");

            ps.setString(1, "Angus");
            ps.setString(2, "Password73!");
            ps.setInt(3, 2);

            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    private static void lookAt(){
        try {

            PreparedStatement ps = db.prepareStatement("SELECT UserID, Username, Score FROM UserAccount");

            ResultSet results = ps.executeQuery();
            while (results.next()) {
                int userID = results.getInt(1);
                String username = results.getString(2);
                int score = results.getInt(3);
                System.out.println(userID + " " + username + " " + score);
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    private static void delete() {
        Scanner input = new Scanner(System.in);
        try {
            System.out.println("Enter the username of the user you want to delete");
            String username = input.nextLine();
            PreparedStatement ps = db.prepareStatement("DELETE FROM UserAccount where Username == ?");
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    private static void update(){
        Scanner input = new Scanner(System.in);
        try {
            System.out.println("Enter the userId of the user you need to update");
            String userId = input.nextLine();
            System.out.println("Enter the updated score");
            int score = input.nextInt();
            input.nextLine();
            PreparedStatement ps = db.prepareStatement("UPDATE UserAccount SET Score = ? where UserID = ?");
            ps.setString(2, userId);
            ps.setInt(1, score);
            ps.executeUpdate();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

}
