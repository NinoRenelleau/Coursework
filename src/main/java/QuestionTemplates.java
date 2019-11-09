import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class QuestionTemplates {
    public static void create(String name, String instruction){
        try {

            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO QuestionTemplates (Instruction, TemplateName) VALUES (?, ?)");
            ps.setString(1, instruction);
            ps.setString(2, name);
            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    public static String getInstruction(int templateID){
        String instruction = "";
        try {

            PreparedStatement ps = Main.db.prepareStatement("SELECT Instruction FROM QuestionTemplates WHERE QuestionTemplateID = ?");
            ps.setInt(1, templateID);
            ResultSet results = ps.executeQuery();
            while (results.next()){
                instruction = results.getString(1);
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
        return instruction;
    }

    public static void list(){
        try {

            PreparedStatement ps = Main.db.prepareStatement("SELECT * FROM QuestionTemplates");
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                int templateID = results.getInt(1);
                String name = results.getString(2);
                String instruction = results.getString(3);
                System.out.println("ID: " + templateID + " Template Name: " + name + " Instructions: " + instruction);
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    public static void delete(int templateID){
        try {

            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM QuestionTemplates WHERE QuestionTemplateID = ?");
            ps.setInt(1, templateID);
            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
}
