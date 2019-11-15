package Controller;

import Server.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Notifications {
    public static void list(int userID){
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT Username, CourseName, Message FROM Notifications INNER JOIN Users ON Notifications.SenderID = Users.UserID INNER JOIN Courses ON Courses.CourseID = Notifications.CourseID WHERE ReceiverID = ?");
            ps.setInt(1, userID);
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                String username = results.getString(1);
                String coursename = results.getString(2);
                String message = results.getString(3);
                System.out.println("Course: " + coursename + " From: " + username + " Message: " + message);
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    public static void create(int courseID, int senderID, int receiverID, String message){
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Notifications (CourseID, SenderID, ReceiverID, Message) VALUES (?, ?, ?, ?)");
            ps.setInt(1, courseID);
            ps.setInt(2, senderID);
            ps.setInt(3, receiverID);
            ps.setString(4, message);
            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    public static void delete(String notificationID) {
        try {
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Notifications where NotificationID = ?");
            ps.setString(1, notificationID);
            ps.executeUpdate();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
}
