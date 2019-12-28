package Controller;

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
@Path("history/")
public class History {

    @POST
    @Path("update")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String update(
            @FormDataParam("QuizID") Integer quizID, @FormDataParam("Score") Integer score, @FormDataParam("Review") Integer review, @CookieParam("token") String cookie){
        try {
            if (quizID == null || score == null || review == null) {
                throw new Exception("One or more form data parameters are missing in the HTTP request.");
            }
            PreparedStatement ps1 = Main.db.prepareStatement("SELECT EXISTS(SELECT * From History WHERE UserID = ? AND QuizID = ?)");
            ps1.setInt(1, validateSessionCookie(cookie));
            ps1.setInt(2, quizID);
            ResultSet results = ps1.executeQuery();
            if (results.getBoolean(1) == false){
                System.out.println("history/create");
                PreparedStatement ps = Main.db.prepareStatement("INSERT INTO History (UserID, QuizID, Score, Review) VALUES (?, ?, ?, ?)");
                ps.setInt(1, validateSessionCookie(cookie));
                ps.setInt(2, quizID);
                ps.setInt(3, score);
                ps.setInt(4, review);
                ps.executeUpdate();
                return "{\"status\": \"OK\"}";
            } else{
                System.out.println("history/update");
                PreparedStatement ps = Main.db.prepareStatement("UPDATE History SET Score = ?, Review = ? WHERE UserID = ? AND QuizID = ?");
                ps.setInt(3, validateSessionCookie(cookie));
                ps.setInt(4, quizID);
                ps.setInt(1, score);
                ps.setInt(2, review);
                ps.executeUpdate();
                return "{\"status\": \"OK\"}";
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to update item, please see server console for more info.\"}";
        }
    }

    @POST
    @Path("updateRatings")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateRatings(@FormDataParam("ID") Integer id){
        try {
            PreparedStatement ps1 = Main.db.prepareStatement("SELECT QuizID FROM Quizzes");
            ResultSet results = ps1.executeQuery();
            while (results.next()){
                System.out.println("history/update");
                PreparedStatement ps = Main.db.prepareStatement(
                        "UPDATE Quizzes SET Rating = " +
                                "(SELECT AVG(Review) FROM History WHERE QuizID = ?) WHERE QuizID = ?");
                ps.setInt(1, results.getInt(1));
                ps.setInt(2, results.getInt(1));
                ps.executeUpdate();
            }
            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to update item, please see server console for more info.\"}";
        }
    }

    @POST
    @Path("updateCourseRatings")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateCourseRatings(@FormDataParam("ID") Integer id){
        try {
            PreparedStatement ps1 = Main.db.prepareStatement("SELECT CourseID FROM Courses");
            ResultSet results = ps1.executeQuery();
            while (results.next()){
                System.out.println("history/updateCourseRating");
                PreparedStatement ps = Main.db.prepareStatement(
                        "UPDATE Courses SET Rating = " +
                                "(SELECT AVG(Quizzes.Rating) FROM Quizzes WHERE Quizzes.CourseID = ?) WHERE CourseID = ?");
                ps.setInt(1, results.getInt(1));
                ps.setInt(2, results.getInt(1));
                ps.executeUpdate();
            }
            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to update item, please see server console for more info.\"}";
        }
    }

    @GET
    @Path("courseScore/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String totalCourseScore(@PathParam("id") String id){
        System.out.println("history/totalCourseScore/"+id);
        JSONObject item = new JSONObject();
        try {
            Integer courseID = Integer.parseInt(id.split("s")[0]);
            Integer userID = Integer.parseInt(id.split("s")[1]);
            System.out.println(courseID);
            System.out.println(userID);
            if (courseID == null || userID == null) {
                throw new Exception("One or more form data parameters are missing in the HTTP request.");
            }
            PreparedStatement ps = Main.db.prepareStatement("SELECT SUM(Score) FROM History INNER JOIN Quizzes ON Quizzes.QuizID = History.QuizID WHERE Quizzes.CourseID = ? AND History.UserID = ?");
            ps.setInt(1, courseID);
            ps.setInt(2, userID);
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                item.put("Score", results.getInt(1));
                System.out.println(results.getInt(1));
            }
            return item.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to get item, please see server console for more info.\"}";
        }
    }

    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public String list(@CookieParam("token") String cookie){
        System.out.println("history/list");
        JSONArray list = new JSONArray();
        try {
            if (cookie == null) {
                throw new Exception("Token is missing from the HTTP request.");
            }
            if (validateSessionCookie(cookie) == null){
                return "{\"error\": \"user not logged in.\"}";
            } else {
                PreparedStatement ps = Main.db.prepareStatement("SELECT History.UserID, History.QuizID, History.Score, History.Review, Q.QuizName FROM History INNER JOIN Quizzes Q on History.QuizID = Q.QuizID Where History.UserID = ?");
                ps.setInt(1, validateSessionCookie(cookie));
                ResultSet results = ps.executeQuery();
                while (results.next()) {
                    JSONObject item = new JSONObject();
                    item.put("User ID", results.getInt(1));
                    item.put("Quiz ID", results.getInt(2));
                    item.put("score", results.getInt(3));
                    item.put("review", results.getInt(4));
                    item.put("QuizName", results.getInt(5));
                    list.add(item);
                }
                return list.toString();
            }
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return"{\"error\": \"Unable to list items, please see server console for more info.\"}";
        }
    }

    public static Integer validateSessionCookie(String token) {
        try {
            PreparedStatement statement = Main.db.prepareStatement(
                    "SELECT UserID FROM Users WHERE token = ?");
            statement.setString(1, token);
            ResultSet results = statement.executeQuery();
            if (results != null && results.next()) {
                return results.getInt(1);
            }
        } catch (Exception resultsException) {
            String error = "Database error - can't select by id from 'Users' table: " + resultsException.getMessage();

            System.out.println(error);
        }
        return null;
    }
}
