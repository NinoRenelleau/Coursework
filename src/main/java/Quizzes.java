import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Quizzes {
    public static void listQuizzes(int courseID){
        try {

            PreparedStatement ps = Main.db.prepareStatement("SELECT QuizID, QuizName, Rating FROM Quizzes WHERE courseID = ?");
            ps.setInt(1, courseID);
            ResultSet results = ps.executeQuery();
            PreparedStatement ps2 = Main.db.prepareStatement("SELECT CourseName FROM Courses WHERE CourseID == ?");
            ps2.setInt(1, courseID);
            ResultSet results2 = ps2.executeQuery();
            String coursename = results2.getString(1);
            while (results.next()) {
                int quizID = results.getInt(1);
                String quizname = results.getString(2);
                String rating = results.getString(3);
                System.out.println("ID: " + quizID + " Course: " + coursename + " Title: " + quizname + " Rating: " + rating);
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    private static void addNewQuiz(String quizName, String courseID){
        try {

            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Quizzes (QuizName, CourseID) VALUES (?, ?)");
            ps.setString(1, quizName);
            ps.setString(2, courseID);

            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    private static void deleteQuiz(String quizID) {
        try {
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Quizzes where QuizID == ?");
            ps.setString(1, quizID);
            ps.executeUpdate();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }



}
