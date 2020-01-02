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
@Path("objects/")

public class Objects {

    @POST
    @Path("create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String create(
            @FormDataParam("QuestionId") Integer id, @FormDataParam("type") String type, @FormDataParam("coordinates") String coordinates, @FormDataParam("font") Integer font, @FormDataParam("content") String content){
        try {
            if (id == null || type == null || coordinates == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request.");
            }
            System.out.println("objects/create");
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Object (QuestionID, ObjectType, Coordinates, Font, Content) VALUES (?, ?, ?, ?, ?)");
            ps.setInt(1, id);
            ps.setString(2, type);
            ps.setString(3, coordinates);
            ps.setInt(4, font);
            ps.setString(5, content);
            ps.executeUpdate();
            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to create new item, please see server console for more info.\"}";
        }
    }

    @POST
    @Path("update")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String update(
            @FormDataParam("objectID") Integer id, @FormDataParam("type") String type, @FormDataParam("coordinates") String coordinates, @FormDataParam("font") Integer font, @FormDataParam("content") String content){
        try {
            if (id == null || type == null || coordinates == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request.");
            }
            System.out.println("objects/update");
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Object SET ObjectType = ?, Coordinates = ?, Font = ?, Content = ? WHERE ObjectID = ?");
            ps.setInt(5, id);
            ps.setString(1, type);
            ps.setString(2, coordinates);
            ps.setInt(3, font);
            ps.setString(4, content);
            ps.executeUpdate();
            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to create new item, please see server console for more info.\"}";
        }
    }

    @GET
    @Path("list/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String list(@PathParam("id") Integer id){
        System.out.println("objects/list/"+id);
        JSONArray list = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT * FROM Object WHERE QuestionID = ?");
            ps.setInt(1, id);
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                JSONObject item = new JSONObject();
                item.put("objectID", results.getInt(1));
                item.put("quizID", results.getString(2));
                item.put("Type", results.getString(3));
                item.put("coordinates", results.getString(4));
                item.put("font", results.getInt(5));
                item.put("content", results.getString(6));
                list.add(item);
            }
            return list.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return"{\"error\": \"Unable to list items, please see server console for more info.\"}";
        }
    }

    @GET
    @Path("getObjects/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getObject(@PathParam("id") Integer id){
        System.out.println("objects/getObject");
        JSONObject item = new JSONObject();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT * FROM Object WHERE ObjectID = ?");
            ps.setInt(1, id);
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                item.put("objectID", results.getInt(1));
                item.put("quizID", results.getString(2));
                item.put("Type", results.getString(3));
                item.put("coordinates", results.getString(4));
                item.put("font", results.getInt(5));
                item.put("content", results.getString(6));
            }
            return item.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return"{\"error\": \"Unable to list items, please see server console for more info.\"}";
        }
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String delete(@FormDataParam("objectId") Integer id) {
        try {
            if (id == null) {
                throw new Exception("Form data parameter is missing in the HTTP request.");
            }
            System.out.println("objects/delete id=" + id);
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Object WHERE ObjectID = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to delete item, please see server console for more info.\"}";
        }
    }


}
