import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class QuestionTemplates {
    public static void createTemplate(String name, String instruction){
        try {

            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO QuestionTemplates (Instruction, TemplateName) VALUES (?, ?)");
            ps.setString(1, instruction);
            ps.setString(2, name);
            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    public static String getTemplateInstruction(int templateID){
        String instruction = "";
        try {

            PreparedStatement ps = Main.db.prepareStatement("SELECT Instruction FROM QuestionTemplates WHERE QuestionTemplateID == ?");
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

    public static void listTemplates(){
        try {

            PreparedStatement ps = Main.db.prepareStatement("SELECT TemplateName FROM QuestionTemplates");
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                String name = results.getString(1);
                System.out.println("Template Name: " + name);
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
}
