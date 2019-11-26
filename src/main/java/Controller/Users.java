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
import java.util.ArrayList;
import java.util.List;
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

    @POST
    @Path("login")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String login(@FormDataParam("username") String username,
                        @FormDataParam("password") String password){

        System.out.println("users/login");
        JSONObject item = new JSONObject();
        try {
            if (username == null || password == null) {
                throw new Exception("A form parameter is missing in the HTTP request's URL.");
            }
            if (!usernameExists(username)){
                throw new Exception("The username in the HTTP request's URL, does not exist.");
            }
            if (!passwordExists(username, password)){
                throw new Exception("The password in the HTTP request's URL, is wrong");
            }
            PreparedStatement ps = Main.db.prepareStatement("SELECT UserID, UserType, Tags, Score " +
                    "FROM Users WHERE (Username = ?) AND (Password = ?) ");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet results = ps.executeQuery();
            item.put("username", username);
            item.put("username", password);
            item.put("userID", results.getInt(1));
            item.put("userType", results.getString(2));
            item.put("Tags", results.getString(3));
            item.put("Score", results.getInt(4));
            String token = UUID.randomUUID().toString();
            PreparedStatement statement2 = Main.db.prepareStatement(
                    "UPDATE Users SET token = ? WHERE Username = ?");
            statement2.setString(1, token);
            statement2.setString(2, username);
            statement2.executeUpdate();
            item.put("token", token);
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

    @GET
    @Path("getFromID")
    @Produces(MediaType.APPLICATION_JSON)
    public String getFromID(@FormDataParam("id") Integer id){
        System.out.println("users/getFromID");
        JSONObject item = new JSONObject();
        try {
            if (id == null) {
                throw new Exception("A form parameter is missing in the HTTP request's URL.");
            }
            PreparedStatement ps = Main.db.prepareStatement("SELECT UserID, Username, Password, UserType, Tags, Score FROM Users WHERE UserID = ?");
            ps.setInt(1, id);
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                item.put("userID", results.getInt(1));
                item.put("username", results.getString(2));
                item.put("username", results.getString(3));
                item.put("userType", results.getString(4));
                item.put("Tags", results.getString(5));
                item.put("Score", results.getInt(6));
            }
            return item.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to get item, please see server console for more info.\"}";
        }
    }


    @POST
    @Path("delete")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String delete(@FormDataParam("id") Integer id) {
        try {
            if (id == null) {
                throw new Exception("One or more form data parameters are missing in the HTTP request.");
            }
            System.out.println("users/delete id=" + id);
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Users where UserID = ?");
            ps.setInt(1, id);
            ps.execute();
            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to delete item, please see server console for more info.\"}";
        }
    }

    /*
    The following method creates a user; it takes appropriate parameters to query the database.
    It makes use of a prepared statement, which allows the query to be parameterized.
     */

    @POST
    @Path("create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String create(
            @FormDataParam("username") String username, @FormDataParam("password") String password, @FormDataParam("UserType") String userType, @FormDataParam("tags") String tags){
        try {
            if (username == null || password == null || tags == null || userType == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request.");
            }
            System.out.println("users/create");
            if(!((usernameValid(username) == null) && (passwordValid(password) == null) && (userTypeValid(userType) == null))){
                //throw new Exception(usernameValid(username) + "\r\n" + passwordValid(password) + "\r\n" + userTypeValid(userType));
                String message = usernameValid(username) + "\r\n" + passwordValid(password) + "\r\n" + userTypeValid(userType);
                return "{\"error\":\""+ message +"\"}";
            } else{
                PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Users " +
                        "(Username, Password, UserType, Tags) VALUES (?, ?, ?, ?)");
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, userType);
                ps.setString(4, tags);
                ps.execute();
            }

            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to create new item, please see server console for more info.\"}";
        }
    }

    @POST
    @Path("logout")
    public void logout(@CookieParam("token") String token) {

        System.out.println("/Users/logout - Logging out user");

        try {
            PreparedStatement statement = Main.db.prepareStatement("Update Users SET token = NULL WHERE token = ?");
            statement.setString(1, token);
            statement.executeUpdate();
        } catch (Exception resultsException) {
            String error = "Database error - can't update 'Users' table: " + resultsException.getMessage();
            System.out.println(error);
        }

    }

    @POST
    @Path("updatePassword")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String updatePassword(
            @FormDataParam("UsersId") Integer id, @FormDataParam("NewPassword") String password, @CookieParam("token") String cookie){
        try {
            if (id == null || password == null) {
                throw new Exception("One or more form data parameters are missing in the HTTP request.");
            }
            if (validateSessionCookie(cookie) == Integer.parseInt(null)){
                return "{\"error\": \"user not logged in.\"}";
            } else if (validateSessionCookie(cookie) == id){
                System.out.println("users/updatePassword ");
                PreparedStatement ps = Main.db.prepareStatement("UPDATE Users SET Password = ? where UserID = ?");
                ps.setString(1, password);
                ps.setInt(2, id);
                ps.executeUpdate();
                return "{\"status\": \"OK\"}";
            } else{
                return "{\"error\": \"session token does not correspond to that of the user being updated.\"}";
            }
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to update item, please see server console for more info.\"}";
        }
    }
    @POST
    @Path("updateScore")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateScore(
            @FormDataParam("UsersId") Integer id, @CookieParam("token") String cookie){
        try {
            if (id == null) {
                throw new Exception("form data parameter is missing in the HTTP request.");
            }
            if (validateSessionCookie(cookie) == Integer.parseInt(null)){
                return "{\"error\": \"user not logged in.\"}";
            } else if (validateSessionCookie(cookie) == id){
                System.out.println("users/updateScore ");
                PreparedStatement ps = Main.db.prepareStatement("UPDATE Users SET Score = (SELECT SUM(Score) FROM History WHERE UserID = ?) where UserID = ?");
                ps.setInt(1, id);
                ps.setInt(2, id);
                ps.executeUpdate();
                return "{\"status\": \"OK\"}";
            } else{
                return "{\"error\": \"session token does not correspond to that of the user being updated.\"}";
            }
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to update item, please see server console for more info.\"}";
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

    private static List usernameValid(String username){
        boolean valid = true;
        List<String> errors = new ArrayList<String>();
        int nameLen = username.length();
        if(nameLen > 15){
            valid = false;
            errors.add("must be less than 15 characters long");
        }
        if (Users.usernameExists(username)){
            valid = false;
            errors.add("Username already exists, try another one");
        }
        if (valid == false){
            System.out.println("Inputted username is invalid; " + errors);
        }
        return errors;
    }

    private static List passwordValid(String password){
        List<String> errors = new ArrayList<String>();
        boolean valid = true;
        int numCount = 0;
        int upperCount = 0;
        int passLen = password.length();
        if (passLen < 8){
            valid = false;
            errors.add("must be more than 8 characters long");
        }
        for(int x = 0; x < passLen; x++){
            if (Character.isUpperCase(password.charAt(x))){
                upperCount += 1;
            }else if (Character.isDigit(password.charAt(x))){
                numCount += 1;
            }
        }
        if (upperCount < 2){
            valid = false;
            errors.add("must have at least 2 uppercase characters");
        }
        if (numCount < 3){
            valid = false;
            errors.add("must have at least 3 digits");
        }
        if (valid == false){
            System.out.println("Inputted password is invalid: " + errors);
        }
        return errors;
    }

    private static List userTypeValid(String userType){
        List<String> errors = new ArrayList<String>();
        boolean valid = true;
        if ((!userType.equalsIgnoreCase("teacher")) && (!userType.equalsIgnoreCase("student"))){
            valid = false;
            System.out.println("Inputted userType is invalid: must be either 'teacher' or 'student'");
            errors.add("Inputted userType is invalid: must be either 'teacher' or 'student'");
        }
        return errors;
    }

    public static int validateSessionCookie(String token) {
        try {
            PreparedStatement statement = Main.db.prepareStatement(
                    "SELECT UserID FROM Users WHERE SessionToken = ?");
            statement.setString(1, token);
            ResultSet results = statement.executeQuery();
            if (results != null && results.next()) {
                return results.getInt(1);
            }
        } catch (Exception resultsException) {
            String error = "Database error - can't select by id from 'Admins' table: " + resultsException.getMessage();

            System.out.println(error);
        }
        return Integer.parseInt(null);
    }


}
