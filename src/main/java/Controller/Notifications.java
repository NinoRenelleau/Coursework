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
@Path("notifications/")
public class Notifications {
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public String list(@FormDataParam("userID") Integer id){
        System.out.println("notifications/list");
        JSONArray list = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT Username, CourseName, Message FROM Notifications INNER JOIN Users ON Notifications.SenderID = Users.UserID INNER JOIN Courses ON Courses.CourseID = Notifications.CourseID WHERE ReceiverID = ?");
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                JSONObject item = new JSONObject();
                item.put("username", results.getString(1));
                item.put("coursename", results.getString(2));
                item.put("message", results.getString(3));
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
            @FormDataParam("courseId") Integer courseID, @FormDataParam("senderID") Integer senderID, @FormDataParam("receiverID") Integer receiverID, @FormDataParam("message") String message){
        try {
            if (courseID == null || senderID == null || receiverID == null || message == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request.");
            }
            System.out.println("notifications/create");
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Notifications (CourseID, SenderID, ReceiverID, Message) VALUES (?, ?, ?, ?)");
            ps.setInt(1, courseID);
            ps.setInt(2, senderID);
            ps.setInt(3, receiverID);
            ps.setString(4, message);
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
    public String delete(@FormDataParam("notificationId") Integer id, @CookieParam("token") String cookie) {
        try {
            if (id == null) {
                throw new Exception("Form data parameter is missing in the HTTP request.");
            }
            PreparedStatement ps1 = Main.db.prepareStatement("SELECT ReceiverID From Notifications Where NotificationID = ?");
            ps1.setInt(1, id);
            ResultSet results = ps1.executeQuery();
            int userID = results.getInt(1);
            if (validateSessionCookie(cookie) == Integer.parseInt(null)){
                return "{\"error\": \"user not logged in.\"}";
            } else if (validateSessionCookie(cookie) == userID){
                System.out.println("notifications/delete id=" + id);
                PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Notifications where NotificationID = ?");
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
