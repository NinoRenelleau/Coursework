

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Quizzes {
    public static void list(int courseID){
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
    public static void create(String quizName, int courseID){
        try {

            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Quizzes (QuizName, CourseID) VALUES (?, ?)");
            ps.setString(1, quizName);
            ps.setInt(2, courseID);

            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
    public static void delete(int quizID) {
        try {
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Quizzes where QuizID = ?");
            ps.setInt(1, quizID);
            ps.executeUpdate();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    public static void update(int quizID, String quizName){
        try {

            PreparedStatement ps = Main.db.prepareStatement("UPDATE Quizzes SET QuizName = ? WHERE QuizID = ?");
            ps.setString(1, quizName);
            ps.setInt(2, quizID);

            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

}
