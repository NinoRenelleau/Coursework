
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Questions {
    public static void addNewQuestion(int quizID, String questionData, int questionTemplateID){
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
    public static void deleteQuestion(int questionID){
        try {

            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Questions WHERE QuestionID == ?");
            ps.setInt(1, questionID);
            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
    public static int countUpQuestions(int quizID){
        int QuestCount = 0;
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT * FROM Questions WHERE QuizID == ?");
            ps.setInt(1, quizID);
            ResultSet results = ps.executeQuery();
            while (results.next()){
                QuestCount +=1;
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
        return QuestCount;
    }
    public static String GetQuestionData(int questionID){
        String questionData = "";
        try {

            PreparedStatement ps = Main.db.prepareStatement("SELECT QuestionData FROM Questions WHERE QuestionID == ?");
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
            PreparedStatement ps = Main.db.prepareStatement("SELECT QuestionTemplateID FROM Questions WHERE QuestionID == ?");
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
