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
    @Path("updatePoints")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String updatePoints(
            @FormDataParam("questionID") Integer id, @FormDataParam("points") Integer points){
        try {
            if (id == null || points == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request.");
            }
            System.out.println("objects/update");
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Questions SET Points = ? WHERE QuestionID = ?");
            ps.setInt(2, id);
            ps.setInt(1, points);
            ps.executeUpdate();
            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to create new item, please see server console for more info.\"}";
        }
    }

    @POST
    @Path("create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String create(
            @FormDataParam("QuizID") Integer quizID, @FormDataParam("Points") Integer points){
        try {
            if (quizID == null || points == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request.");
            }
            JSONObject item = new JSONObject();
            System.out.println("questions/create");
            PreparedStatement ps1 = Main.db.prepareStatement("SELECT OrderNum FROM Questions");
            ResultSet results = ps1.executeQuery();
            Integer order = 0;
            Integer last = 0;
            while(results.next()){
                last = results.getInt(1);
                if (last > order){
                    order = last;
                }
            }
            order ++;
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Questions (OrderNum, Points, QuizID) VALUES (?, ?, ?)");
            ps.setInt(1, order);
            ps.setInt(2, points);
            ps.setInt(3, quizID);
            ps.executeUpdate();
            PreparedStatement ps2 = Main.db.prepareStatement("SELECT QuestionID FROM Questions WHERE OrderNum = ? AND QuizID = ?");
            ps2.setInt(1, order);
            ps2.setInt(2, quizID);
            ResultSet results2 = ps2.executeQuery();
            while(results2.next()){
                item.put("questionID", results2.getInt(1));
            }
            return item.toString();
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
            PreparedStatement ps1 = Main.db.prepareStatement("SELECT UserID From Courses INNER JOIN Quizzes ON Quizzes.CourseID = Courses.CourseID INNER JOIN Questions ON Questions.QuizID = Quizzes.QuizID Where QuestionID = ?");
            ps1.setInt(1, id);
            ResultSet results = ps1.executeQuery();
            int userID = results.getInt(1);
            if (validateSessionCookie(cookie) == 0){
                return "{\"error\": \"user not logged in.\"}";
            } else if (validateSessionCookie(cookie) == userID){
                PreparedStatement ps2 = Main.db.prepareStatement("SELECT OrderNum, QuizID FROM Questions WHERE QuestionID = ?");
                ps2.setInt(1, id);
                ResultSet results2 = ps2.executeQuery();
                Integer order = results2.getInt(1);
                Integer quizID = results2.getInt(2);
                PreparedStatement ps3 = Main.db.prepareStatement("SELECT QuestionID, OrderNum FROM Questions WHERE QuizID = ?");
                ps3.setInt(1, quizID);
                ResultSet results3 = ps3.executeQuery();
                while(results3.next()){
                    if(results3.getInt(2) > order){
                        PreparedStatement ps4 = Main.db.prepareStatement("UPDATE Questions SET OrderNum = ? WHERE QuestionID = ?");
                        ps4.setInt(1, results3.getInt(2) - 1);
                        ps4.setInt(2, results3.getInt(1));
                        ps4.executeUpdate();
                    }
                }
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
    @Path("list/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String list(@PathParam("id") Integer id){
        System.out.println("questions/list/"+id);
        JSONArray list = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT QuestionID, Points, OrderNum FROM Questions WHERE QuizID = ? ORDER BY OrderNum");
            ps.setInt(1, id);
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                JSONObject item = new JSONObject();
                item.put("questionID", results.getInt(1));
                item.put("Points", results.getInt(2));
                item.put("OrderNum", results.getInt(3));
                list.add(item);
            }
            return list.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return"{\"error\": \"Unable to list items, please see server console for more info.\"}";
        }
    }

    @GET
    @Path("getQuestion/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getQuestion(@PathParam("id") Integer id){
        System.out.println("questions/getQuestion/"+id);
        JSONObject item = new JSONObject();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT QuestionID, Points, OrderNum FROM Questions WHERE QuizID = ?");
            ps.setInt(1, id);
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                item.put("questionID", results.getInt(1));
                item.put("Points", results.getInt(2));
                item.put("OrderNum", results.getInt(3));

            }
            return item.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return"{\"error\": \"Unable to list items, please see server console for more info.\"}";
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
