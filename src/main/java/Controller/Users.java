package Controller;

import Server.Main;

import Server.Main;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

@SuppressWarnings("unchecked")
@Path("users/")

public class Users {

    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public String search(@FormDataParam("username") String InpUsername){

        System.out.println("users/search");
        JSONArray list = new JSONArray();
        try {
            if (InpUsername == null) {
                throw new Exception("The username is missing in the HTTP request's URL.");
            }
            PreparedStatement ps = Main.db.prepareStatement("SELECT Username FROM Users WHERE Username LIKE ?");
            ps.setString(1, (InpUsername+"%"));
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                JSONObject item = new JSONObject();
                item.put("username", results.getString(1));
                list.add(item);
            }
            return list.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to get item, please see server console for more info.\"}";
        }
    }

    @GET
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    public String login(@FormDataParam("username") String username, @FormParam("password") String password){

        System.out.println("users/login");
        JSONObject item = new JSONObject();
        try {
            if (username == null || password == null) {
                throw new Exception("A form parameter is missing in the HTTP request's URL.");
            }
            if (!usernameExists(username)){
                throw new Exception("The username in the HTTP request's URL, does not exist.");
            }
            if (!passwordExists(username, password){
                throw new Exception("The password in the HTTP request's URL, is wrong");
            }
            PreparedStatement ps = Main.db.prepareStatement("SELECT UserID, UserType, Tags, Score " +
                    "FROM Users WHERE (Username = ?) AND (Password = ?) ");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                item.put("username", username);
                item.put("username", password);
                item.put("userID", results.getInt(1));
                item.put("userType", results.getString(2));
                item.put("Tags", results.getString(3));
                item.put("Score", results.getInt(4));
            }
            return item.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to get item, please see server console for more info.\"}";
        }
    }

    @GET
    @Path("getFromName")
    @Produces(MediaType.APPLICATION_JSON)
    public String getFromName(@FormDataParam("username") String username){

        System.out.println("users/getFromName");
        JSONObject item = new JSONObject();
        try {
            if (username == null) {
                throw new Exception("A form parameter is missing in the HTTP request's URL.");
            }
            if (!usernameExists(username)){
                throw new Exception("The username in the HTTP request's URL, does not exist.");
            }
            PreparedStatement ps = Main.db.prepareStatement("SELECT UserID, UserType, Tags, Score FROM Users WHERE Username = ?");
            ps.setString(1, username);
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                item.put("username", username);
                item.put("userID", results.getInt(1));
                item.put("userType", results.getString(2));
                item.put("Tags", results.getString(3));
                item.put("Score", results.getInt(4));
            }
            return item.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to get item, please see server console for more info.\"}";
        }
    }

    public static String getFromID(int userID){
        String username = "";
        String password = "";
        String userType = "";
        String tags = "";
        int score = 0;
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT * FROM Users WHERE UserID = ?");
            ps.setString(1, username);
            ResultSet results = ps.executeQuery();
            username = results.getString(2);
            password = results.getString(3);
            userType = results.getString(4);
            tags = results.getString(5);
            score = results.getInt(6);
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
        return (userID + "," + username + "," + password + "," + userType +"," + tags + "," + score);
    }


    public static void delete(int UserID) {
        try {
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Users where UserID = ?");
            ps.setInt(1, UserID);
            ps.executeUpdate();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    /*
    The following method creates a user; it takes appropriate parameters to query the database.
    It makes use of a prepared statement, which allows the query to be parameterized.
     */
    public static void create(String username, String password, String UserType, String tags, int score){
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Users " +
                    "(Username, Password, UserType, Tags, Score) VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, UserType);
            ps.setString(4, tags);
            ps.setInt(5, score);
            ps.executeUpdate();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    public static void updatePassword(int UserID, String newPassword){
        try {
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Users SET Password = ? where UserID = ?");
            ps.setInt(2, UserID);
            ps.setString(1, newPassword);
            ps.executeUpdate();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
    public static void updateScore(int userID){
        try {
            int newScore = 0;
            PreparedStatement lookupScore = Main.db.prepareStatement("SELECT Score FROM History WHERE UserID = ?");
            lookupScore.setInt(1, userID);
            ResultSet results = lookupScore.executeQuery();
            while (results.next()) {
                newScore += results.getInt(1);
            }
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Users SET Score = ? where UserID = ?");
            ps.setInt(2, userID);
            ps.setInt(1, newScore);
            ps.executeUpdate();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    public static boolean usernameExists(String username){
        boolean found = false;
        try {
            PreparedStatement ps = Main.db.prepareStatement(
                    "Select Exists(SELECT * FROM Users WHERE Username == ?)");
            ps.setString(1, username);
            ResultSet results = ps.executeQuery();
            if (results.getBoolean(1) == true){
                found = true;
            } else{
                found = false;
            }
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
        return found;
    }
    public static boolean passwordExists(String username, String password){
        boolean found = false;
        try {
            PreparedStatement ps = Main.db.prepareStatement(
                    "Select Exists(SELECT * FROM Users WHERE Username == ? AND Password == ?)");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet results = ps.executeQuery();
            if (results.getBoolean(1) == true){
                found = true;
            } else{
                found = false;
            }
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
        return found;
    }

}
