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

    @GET
    @Path("getCoordinates/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCoordinates(@PathParam("id") Integer id){
        System.out.println("Objects/getCoordinates" + id);
        JSONObject item = new JSONObject();
        try {
            if (id == null) {
                throw new Exception("Object ID is missing in the HTTP request's URL.");
            }
            PreparedStatement ps = Main.db.prepareStatement("SELECT Coordinates FROM Object WHERE ObjectID == ?");
            ps.setInt(1, id);
            ResultSet results = ps.executeQuery();
            if (results.next()) {
                item.put("Cooridnates", results.getString(1));
            }
            return item.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to get item, please see server console for more info.\"}";
        }
    }

    @GET
    @Path("getTypes/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getTypes(@PathParam("id") Integer id){
        System.out.println("Objects/getCoordinates" + id);
        JSONObject item = new JSONObject();
        try {
            if (id == null) {
                throw new Exception("Object ID is missing in the HTTP request's URL.");
            }
            PreparedStatement ps = Main.db.prepareStatement("SELECT ObjectType FROM Object WHERE ObjectID == ?");
            ps.setInt(1, id);
            ResultSet results = ps.executeQuery();
            if (results.next()) {
                item.put("Type", results.getString(1));
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
            @FormDataParam("TemplateId") Integer id, @FormDataParam("type") String type, @FormDataParam("coordinates") String coordinates){
        try {
            if (id == null || type == null || coordinates == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request.");
            }
            System.out.println("objects/create");
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Object (QuestionTemplateID, ObjectType, Coordinates) VALUES (?, ?, ?)");
            ps.setInt(1, id);
            ps.setString(2, type);
            ps.setString(3, coordinates);
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
            PreparedStatement ps = Main.db.prepareStatement("SELECT * FROM Object WHERE QuestionTemplateID = ?");
            ps.setInt(1, id);
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                JSONObject item = new JSONObject();
                item.put("objectID", results.getInt(1));
                item.put("templateID", results.getInt(2));
                item.put("Type", results.getString(3));
                item.put("coordinates", results.getString(4));
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
        JSONArray list = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT * FROM Object WHERE QuestionTemplateID = ?");
            ps.setInt(1, id);
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                JSONObject item = new JSONObject();
                item.put("Object ID", results.getInt(1));
                item.put("Template ID", results.getInt(2));
                item.put("Type", results.getString(3));
                item.put("coordinates", results.getString(4));
                list.add(item);
            }
            return list.toString();
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
