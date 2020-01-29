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
    @Path("list/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String list(@PathParam("id") String id){
        System.out.println("quizzes/list/"+id);
        JSONArray list = new JSONArray();
        try {
            Integer courseID = Integer.parseInt(id.split("s")[0]);
            Integer userID = Integer.parseInt(id.split("s")[1]);
            System.out.println(Score(1, userID));
            PreparedStatement ps = Main.db.prepareStatement(
                    "SELECT CourseName, QuizID, QuizName, Quizzes.Rating FROM Quizzes " +
                    "INNER JOIN Courses ON Courses.CourseID = Quizzes.CourseID " +
                            "WHERE Quizzes.CourseID = ? ");
            ps.setInt(1, courseID);
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                PreparedStatement ps1 = Main.db.prepareStatement(
                        "SELECT SUM(Points) FROM Quizzes INNER JOIN " +
                                "Questions Q on Quizzes.QuizID = Q.QuizID " +
                                "WHERE Quizzes.QuizID = ?");
                ps1.setInt(1, results.getInt(2));
                ResultSet results1 = ps1.executeQuery();
                JSONObject item = new JSONObject();
                item.put("coursename", results.getString(1));
                item.put("quizID", results.getInt(2));
                Integer quizID = results.getInt(2);
                item.put("quizname", results.getString(3));
                item.put("rating", results.getString(4));
                item.put("Total", results1.getInt(1));
                item.put("score", Score(quizID, userID));
                list.add(item);
                System.out.println(list);
            }
            return list.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return"{\"error\": \"Unable to list items, please see server console for more info.\"}";
        }
    }

    @GET
    @Path("searchByID/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String searchByID(@PathParam("id") Integer id){
        System.out.println("quizzes/searchByID/" + id);
        JSONObject item = new JSONObject();
        try {
            if (id == null) {
                throw new Exception("Course ID is missing in the HTTP request's URL.");
            }
            PreparedStatement ps = Main.db.prepareStatement("SELECT QuizID, QuizName FROM Quizzes WHERE QuizID = ?");
            ps.setInt(1, id);
            ResultSet results = ps.executeQuery();
            if (results.next()) {
                item.put("quizID", results.getInt(1));
                item.put("quizname", results.getString(2));
            }
            return item.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to get item, please see server console for more info.\"}";
        }
    }

    @POST
    @Path("create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String create(
            @FormDataParam("name") String name, @FormDataParam("courseID") Integer id){
        try {
            JSONObject item = new JSONObject();
            if (id == null || name == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request.");
            }
            System.out.println("quizzes/create");
            PreparedStatement ps2 = Main.db.prepareStatement("SELECT EXISTS(SELECT * FROM Quizzes WHERE QuizName = ? AND CourseID = ?)");
            ps2.setString(1, name);
            ps2.setInt(2, id);
            ResultSet results2 = ps2.executeQuery();
            if (!results2.getBoolean(1)){
                PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Quizzes (QuizName, CourseID) VALUES (?, ?)");
                ps.setString(1, name);
                ps.setInt(2, id);
                ps.execute();
                PreparedStatement ps1 = Main.db.prepareStatement("SELECT QuizID FROM Quizzes WHERE QuizName = ? AND CourseID = ?");
                ps1.setString(1, name);
                ps1.setInt(2, id);
                ResultSet results = ps1.executeQuery();
                item.put("quizID", results.getInt(1));
                return item.toString();
            } else{
                return "{\"error\": \"Name given to quiz already exists in this course.\"}";
            }
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
            PreparedStatement ps1 = Main.db.prepareStatement("SELECT UserID From Courses INNER JOIN Quizzes ON Quizzes.CourseID = Courses.CourseID Where QuizID = ?");
            ps1.setInt(1, id);
            ResultSet results = ps1.executeQuery();
            int userID = results.getInt(1);
            if (validateSessionCookie(cookie) == null){
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

    @GET
    @Path("getTotal/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getTotal(@PathParam("id") Integer id){
        System.out.println("quizzes/getTotal" + id);
        JSONObject item = new JSONObject();
        try {
            if (id == null) {
                throw new Exception("Course ID is missing in the HTTP request's URL.");
            }
            PreparedStatement ps = Main.db.prepareStatement(
                    "SELECT SUM(SELECT Points FROM Quizzes INNER JOIN Questions Q on Quizzes.QuizID = Q.QuizID WHERE Quizzes.QuizID = ?)");
            ps.setInt(1, id);
            ResultSet results = ps.executeQuery();
            if (results.next()) {
                item.put("Total Quiz Score:", results.getInt(1));
            }
            return item.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to get item, please see server console for more info.\"}";
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
            if (validateSessionCookie(cookie) == null){
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

    public static Integer Score(Integer quizID, Integer userID){
        try{
            PreparedStatement ps1 = Main.db.prepareStatement(
                    "SELECT EXISTS(SELECT Score FROM History " +
                            "INNER JOIN Quizzes Q on History.QuizID = Q.QuizID " +
                            "WHERE History.QuizID = ? AND UserID = ?) ");
            ps1.setInt(1, quizID);
            ps1.setInt(2, userID);
            ResultSet results1 = ps1.executeQuery();
            System.out.println(results1);
            if(results1.getBoolean(1)){
                PreparedStatement ps = Main.db.prepareStatement(
                        "SELECT Score FROM History INNER JOIN Quizzes Q " +
                                "on History.QuizID = Q.QuizID WHERE History.QuizID = ? AND UserID = ?");
                ps.setInt(1, quizID);
                ps.setInt(2, userID);
                ResultSet results = ps.executeQuery();
                System.out.println(results);
                return results.getInt(1);
            }
        }catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return null;
        }
        return null;
    }

}
