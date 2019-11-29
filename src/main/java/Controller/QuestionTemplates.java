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
@Path("templates/")
public class QuestionTemplates {
    @POST
    @Path("create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String create(
            @FormDataParam("TemplateName") String name, @FormDataParam("instruction") String instruction){
        try {
            if (name == null || instruction == null){
                throw new Exception("One or more form data parameters are missing in the HTTP request.");
            }
            System.out.println("templates/create");
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO QuestionTemplates (Instruction, TemplateName) VALUES (?, ?)");
            ps.setString(1, instruction);
            ps.setString(2, name);
            ps.execute();
            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to create new item, please see server console for more info.\"}";
        }
    }

    @GET
    @Path("getTemplate/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getTemplate(@PathParam("id") Integer id){
        System.out.println("templates/getTemplate" + id);
        JSONObject item = new JSONObject();
        try {
            if (id == null) {
                throw new Exception("template ID is missing in the HTTP request's URL.");
            }
            PreparedStatement ps = Main.db.prepareStatement("SELECT * FROM QuestionTemplates WHERE QuestionTemplateID = ?");
            ps.setInt(1, id);
            ResultSet results = ps.executeQuery();
            if (results.next()) {
                item.put("template ID", results.getInt(1));
                item.put("Name", results.getString(2));
                item.put("instruction", results.getString(3));
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
    public String list(){
        System.out.println("templates/list");
        JSONArray list = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT * FROM QuestionTemplates");
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                JSONObject item = new JSONObject();
                item.put("templateID", results.getInt(1));
                item.put("name", results.getString(2));
                item.put("instruction", results.getString(3));
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
    public String delete(@FormDataParam("templateId") Integer id) {
        try {
            if (id == null) {
                throw new Exception("Form data parameter is missing in the HTTP request.");
            }
            System.out.println("templates/delete id=" + id);
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM QuestionTemplates WHERE QuestionTemplateID = ?");
            ps.setInt(1, id);
            ps.execute();
            return "{\"status\": \"OK\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"error\": \"Unable to delete item, please see server console for more info.\"}";
        }
    }
}
