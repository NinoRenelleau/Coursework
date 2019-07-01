import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Quizzes {
    public static void listQuizzes(){
        try {

            PreparedStatement ps = Main.db.prepareStatement("SELECT QuizID, UserID, QuizName, Rating, NumberOfQuestions, Tags FROM Users");
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                int quizID = results.getInt(1);
                int userID = results.getInt(2);
                String quizname = results.getString(3);
                String rating = results.getString(4);
                int numberOfQuestions = results.getInt(5);
                String tags = results.getString(6);
                System.out.println("ID: " + quizID + " AuthorID: " + userID + " Title: " + quizname + " Rating: " + rating + " Number of Questions: " + numberOfQuestions + " Tags: " + tags);
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    private static void addNewQuiz(String quizName, int numberOfQuestions, String UserType, String tags){
        try {

            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Users (Username, Password, UserType, Tags) VALUES (?, ?, ?, ?)");

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, UserType);
            ps.setString(4, tags);

            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

}
