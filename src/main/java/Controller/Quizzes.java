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
import java.util.UUID;

@SuppressWarnings("unchecked")
@Path("quizzes/")
public class Quizzes {
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public String list(@FormDataParam("courseID") Integer id){
        System.out.println("quizzes/list");
        JSONArray list = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT CourseName, QuizID, QuizName, Rating FROM Quizzes " +
                    "INNER JOIN Courses ON Courses.CourseID = Quizzes.CourseID WHERE Quizzes.CourseID = ?");
            ps.setInt(1, id);
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                JSONObject item = new JSONObject();
                item.put("coursename", results.getString(1));
                item.put("Quiz ID", results.getInt(2));
                item.put("Quiz name", results.getString(3));
                item.put("rating", results.getString(4));
                list.add(item);
            }
            return list.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return"{\"error\": \"Unable to list items, please see server console for more info.\"}";
        }
    }

    @POST
    @Path("create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String create(
            @FormDataParam("name") String name, @FormDataParam("courseID") Integer id){
        try {
            if (id == null || name == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request.");
            }
            System.out.println("quizzes/create");
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Quizzes (QuizName, CourseID) VALUES (?, ?)");
            ps.setString(1, name);
            ps.setInt(2, id);
            ps.execute();
            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to create new item, please see server console for more info.\"}";
        }
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String delete(@FormDataParam("quizId") Integer id, @CookieParam("token") String cookie) {
        try {
            if (id == null) {
                throw new Exception("Form data parameter is missing in the HTTP request.");
            }
            PreparedStatement ps1 = Main.db.prepareStatement("SELECT UserID From Courses INNER JOIN Quizzes ON Quizzes.CourseID = Course.CourseID Where QuizID = ?");
            ps1.setInt(1, id);
            ResultSet results = ps1.executeQuery();
            int userID = results.getInt(1);
            if (validateSessionCookie(cookie) == 0){
                return "{\"error\": \"user not logged in.\"}";
            } else if (validateSessionCookie(cookie) == userID){
                System.out.println("quizzes/delete id=" + id);
                PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Quizzes where QuizID = ?");
                ps.setInt(1, id);
                ps.execute();
                return "{\"status\": \"OK\"}";
            }else {
                return "{\"error\": \"user does not correspond to the creator of this course.\"}";
            }
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to delete item, please see server console for more info.\"}";
        }
    }

    @POST
    @Path("update")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String update(
            @FormDataParam("QuizId") Integer id, @FormDataParam("name") String quizName, @CookieParam("token") String cookie){
        try {
            if (id == null || quizName == null) {
                throw new Exception("One or more form data parameters are missing in the HTTP request.");
            }
            PreparedStatement ps1 = Main.db.prepareStatement("SELECT UserID From Courses INNER JOIN Quizzes ON Courses.CourseID = Quizzes.CourseID Where QuizID = ?");
            ps1.setInt(1, id);
            ResultSet results = ps1.executeQuery();
            int userID = results.getInt(1);
            if (validateSessionCookie(cookie) == 0){
                return "{\"error\": \"user not logged in.\"}";
            } else if (validateSessionCookie(cookie) == userID){
                System.out.println("quizzes/update id=" + id);
                PreparedStatement ps = Main.db.prepareStatement("UPDATE Quizzes SET QuizName = ? WHERE QuizID = ?");
                ps.setString(1, (quizName));
                ps.setInt(2, (id));
                ps.execute();
                return "{\"status\": \"OK\"}";
            } else{
                return "{\"error\": \"user does not correspond to the creator of this quiz.\"}";
            }
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to update item, please see server console for more info.\"}";
        }
    }

    public static int validateSessionCookie(String token) {
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
        return 0;
    }

}
