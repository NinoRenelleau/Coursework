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
@Path("courses/")

public class Courses {
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public String list(){
        System.out.println("courses/list");
        JSONArray list = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT CourseID, Username, CourseName, Courses.Tags " +
                    "FROM Courses INNER JOIN Users ON Courses.UserID = Users.UserID");
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                JSONObject item = new JSONObject();
                item.put("course ID", results.getInt(1));
                item.put("username", results.getString(2));
                item.put("coursename", results.getString(3));
                item.put("tags", results.getString(4));
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
            @FormDataParam("userId") Integer id, @FormDataParam("coursename") String coursename, @FormDataParam("tags") String tags){
        try {
            if (id == null || coursename == null || tags == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request.");
            }
            System.out.println("courses/create");
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Courses (CourseName, Tags, UserID) VALUES (?, ?, ?)");
            ps.setString(1, coursename);
            ps.setString(2, tags);
            ps.setInt(3, id);
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
    public String delete(@FormDataParam("courseId") Integer id, @CookieParam("token") String cookie) {
        try {
            if (id == null) {
                throw new Exception("One or more form data parameters are missing in the HTTP request.");
            }
            if (validateSessionCookie(cookie) == null){

            }
            System.out.println("courses/delete id=" + id);
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Courses where CourseID == ?");
            ps.setInt(1, id);
            ps.execute();
            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to delete item, please see server console for more info.\"}";
        }
    }

    @GET
    @Path("searchByName")
    @Produces(MediaType.APPLICATION_JSON)
    public String searchByName(@FormDataParam("coursename") String InpCourse){

        System.out.println("courses/searchByName");
        JSONArray list = new JSONArray();
        try {
            if (InpCourse == null) {
                throw new Exception("The course name is missing in the HTTP request's URL.");
            }
            PreparedStatement ps = Main.db.prepareStatement("SELECT CourseID, Username, CourseName, Tags " +
                    "FROM Courses INNER JOIN Users ON Courses.UserID = Users.UserID WHERE CourseName LIKE ?");
            ps.setString(1, (InpCourse+"%"));
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                JSONObject item = new JSONObject();
                item.put("course ID", results.getInt(1));
                item.put("username", results.getString(2));
                item.put("coursename", results.getString(3));
                item.put("tags", results.getString(4));
                list.add(item);
            }
            return list.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to get item, please see server console for more info.\"}";
        }
    }

    @GET
    @Path("searchByCreator")
    @Produces(MediaType.APPLICATION_JSON)
    public String searchByCreator(@FormDataParam("username") String InpName){

        System.out.println("courses/searchByCreator");
        JSONArray list = new JSONArray();
        try {
            if (InpName == null) {
                throw new Exception("The creator's username is missing in the HTTP request's URL.");
            }
            PreparedStatement ps = Main.db.prepareStatement("SELECT CourseID, Username, CourseName, Tags " +
                    "FROM Courses INNER JOIN Users ON Courses.UserID = Users.UserID WHERE Users.Username = ?");
            ps.setString(1, InpName);
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                JSONObject item = new JSONObject();
                item.put("course ID", results.getInt(1));
                item.put("username", results.getString(2));
                item.put("coursename", results.getString(3));
                item.put("tags", results.getString(4));
                list.add(item);
            }
            return list.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to get item, please see server console for more info.\"}";
        }
    }




    @GET
    @Path("searchByID/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String searchByID(@PathParam("id") Integer id){
        System.out.println("courses/searchByID" + id);
        JSONObject item = new JSONObject();
        try {
            if (id == null) {
                throw new Exception("Course ID is missing in the HTTP request's URL.");
            }
            PreparedStatement ps = Main.db.prepareStatement("SELECT CourseID, Username, CourseName, Tags " +
                    "FROM Courses INNER JOIN Users ON Courses.UserID = Users.UserID WHERE CourseID = ?");
            ps.setInt(1, id);
            ResultSet results = ps.executeQuery();
            if (results.next()) {
                item.put("course ID", results.getInt(1));
                item.put("username", results.getString(2));
                item.put("coursename", results.getString(3));
                item.put("tags", results.getString(4));
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
    public String updateName(
            @FormDataParam("courseId") Integer id, @FormDataParam("name") String coursename){
        try {
            if (id == null || coursename == null) {
                throw new Exception("One or more form data parameters are missing in the HTTP request.");
            }
            System.out.println("courses/update id=" + id);
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Courses SET CourseName = ? WHERE CourseID = ?");
            ps.setString(1, (coursename));
            ps.setInt(2, (id));
            ps.execute();
            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to update item, please see server console for more info.\"}";
        }
    }

    @GET
    @Path("searchByTags")
    @Produces(MediaType.APPLICATION_JSON)
    public String searchByTags(@FormDataParam("tags") String InpTags){

        System.out.println("courses/searchByTags");
        JSONArray list = new JSONArray();
        try {
            if (InpTags == null) {
                throw new Exception("The course tag is missing in the HTTP request's URL.");
            }
            PreparedStatement ps = Main.db.prepareStatement("SELECT CourseID, Username, CourseName, Tags FROM Courses " +
                    "INNER JOIN Users ON Courses.UserID = Users.UserID WHERE Courses.Tags LIKE ?");
            ps.setString(1, ("%"+InpTags+"%"));
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                JSONObject item = new JSONObject();
                item.put("course ID", results.getInt(1));
                item.put("username", results.getString(2));
                item.put("coursename", results.getString(3));
                item.put("tags", results.getString(4));
                list.add(item);
            }
            return list.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to get item, please see server console for more info.\"}";
        }
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
