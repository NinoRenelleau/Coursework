
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Questions {
    public static void create(int quizID, String questionData, int questionTemplateID){
        try {

            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Questions (QuestionData, QuestionTemplateID, QuizID) VALUES (?, ?, ?)");
            ps.setString(1, questionData);
            ps.setInt(2, questionTemplateID);
            ps.setInt(3, quizID);
            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
    public static void delete(int questionID){
        try {

            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Questions WHERE QuestionID = ?");
            ps.setInt(1, questionID);
            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
    public static String listInQuiz(int quizID){
        int QuestionID = 0;
        int QuestionTemplateID = 0;
        String QuestionData = "";
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT * FROM Questions WHERE QuizID = ?");
            ps.setInt(1, quizID);
            ResultSet results = ps.executeQuery();
            while (results.next()){
                QuestionID = results.getInt(1);
                QuestionTemplateID = results.getInt(2);
                QuestionData = results.getString(3);
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
        return (quizID + "," + QuestionID + "," + QuestionTemplateID + "," + QuestionData);
    }

    public static String GetData(int questionID){
        String questionData = "";
        try {

            PreparedStatement ps = Main.db.prepareStatement("SELECT QuestionData FROM Questions WHERE QuestionID = ?");
            ps.setInt(1, questionID);
            ResultSet result = ps.executeQuery();
            while(result.next()){
                questionData = result.getString(1);
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
        return questionData;
    }
    public static int getTemplateID(int questionID){
        int templateID = 0;
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT QuestionTemplateID FROM Questions WHERE QuestionID = ?");
            ps.setInt(1, questionID);
            ResultSet result = ps.executeQuery();
            while(result.next()){
                templateID = result.getInt(1);
            }
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
        return templateID;
    }
}
