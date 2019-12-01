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
@Path("questions/")
public class Questions {

    @POST
    @Path("create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String create(
            @FormDataParam("QuizID") Integer quizID, @FormDataParam("questionData") String data, @FormDataParam("QuestionTemplateID") Integer templateID){
        try {
            if (quizID == null || data == null || templateID == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request.");
            }
            System.out.println("questions/create");
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Questions (QuestionData, QuestionTemplateID, QuizID) VALUES (?, ?, ?)");
            ps.setString(1, data);
            ps.setInt(2, templateID);
            ps.setInt(3, quizID);
            ps.executeUpdate();
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
    public String delete(@FormDataParam("QuestionId") Integer id, @CookieParam("token") String cookie) {
        try {
            if (id == null) {
                throw new Exception("Form data parameter is missing in the HTTP request.");
            }
            PreparedStatement ps1 = Main.db.prepareStatement("SELECT UserID From Courses INNER JOIN Quizzes ON Quizzes.CourseID = Course.CourseID INNER JOIN Questions ON Questions.QuizID = Quizzes.QuizID Where QuestionID = ?");
            ps1.setInt(1, id);
            ResultSet results = ps1.executeQuery();
            int userID = results.getInt(1);
            if (validateSessionCookie(cookie) == 0){
                return "{\"error\": \"user not logged in.\"}";
            } else if (validateSessionCookie(cookie) == userID){
                System.out.println("quizzes/delete id=" + id);
                PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Questions WHERE QuestionID = ?");
                ps.setInt(1, id);
                ps.executeUpdate();
                return "{\"status\": \"OK\"}";
            }else {
                return "{\"error\": \"user does not correspond to the creator of this course.\"}";
            }
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to delete item, please see server console for more info.\"}";
        }
    }

    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public String list(@FormDataParam("QuizID") Integer id){
        System.out.println("questions/list");
        JSONArray list = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT * FROM Questions WHERE QuizID = ?");
            ps.setInt(1, id);
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                JSONObject item = new JSONObject();
                item.put("Question ID", results.getInt(1));
                item.put("QuestionTemplateID", results.getInt(2));
                item.put("QuestionData", results.getString(3));
                list.add(item);
            }
            return list.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return"{\"error\": \"Unable to list items, please see server console for more info.\"}";
        }
    }

    @GET
    @Path("getData/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getData(@PathParam("id") Integer id){
        System.out.println("questions/getData" + id);
        JSONObject item = new JSONObject();
        try {
            if (id == null) {
                throw new Exception("Question ID is missing in the HTTP request's URL.");
            }
            PreparedStatement ps = Main.db.prepareStatement("SELECT QuestionData FROM Questions WHERE QuestionID = ?");
            ps.setInt(1, id);
            ResultSet results = ps.executeQuery();
            if (results.next()) {
                item.put("questionData", results.getString(1));
            }
            return item.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to get item, please see server console for more info.\"}";
        }
    }

    @GET
    @Path("getTemplate/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getTemplate(@PathParam("id") Integer id){
        System.out.println("questions/getTemplate" + id);
        JSONObject item = new JSONObject();
        try {
            if (id == null) {
                throw new Exception("Question ID is missing in the HTTP request's URL.");
            }
            PreparedStatement ps = Main.db.prepareStatement("SELECT QuestionTemplateID FROM Questions WHERE QuestionID = ?");
            ps.setInt(1, id);
            ResultSet results = ps.executeQuery();
            if (results.next()) {
                item.put("TemplateID", results.getString(1));
            }
            return item.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to get item, please see server console for more info.\"}";
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
