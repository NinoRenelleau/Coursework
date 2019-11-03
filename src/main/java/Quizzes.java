

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Quizzes {
    public static void listQuizzes(int courseID){
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT CourseName, QuizID, QuizName, Rating FROM Quizzes " +
                    "INNER JOIN Courses ON Courses.CourseID = Quizzes.CourseID WHERE Quizzes.CourseID = ?");
            ps.setInt(1, courseID);
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                String coursename = results.getString(1);
                int quizID = results.getInt(2);
                String quizname = results.getString(3);
                String rating = results.getString(4);
                System.out.println("ID: " + quizID + " Course: " + coursename + " Title: " + quizname + " Rating: " + rating);
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
    public static void addNewQuiz(String quizName, String courseID){
        try {

            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Quizzes (QuizName, CourseID) VALUES (?, ?)");
            ps.setString(1, quizName);
            ps.setString(2, courseID);

            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
    public static void deleteQuiz(String quizID) {
        try {
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Quizzes where QuizID == ?");
            ps.setString(1, quizID);
            ps.executeUpdate();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
    public static int countUpQuizzes(int courseID){
        int QuizCount = 0;
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT * FROM Quizzes WHERE CourseID == ?");
            ps.setInt(1, courseID);
            ResultSet results = ps.executeQuery();
            while (results.next()){
                QuizCount +=1;
            }
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
        return QuizCount;
    }
}
