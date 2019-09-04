import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Notifications {
    public static void listNotifications(){
        try {

            PreparedStatement ps = Main.db.prepareStatement("SELECT SenderID, ReceiverID, CourseID, Message FROM Notifications");
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                int senderID = results.getInt(1);
                int courseID = results.getInt(2);
                String message = results.getString(3);
                PreparedStatement ps2 = Main.db.prepareStatement("SELECT Username FROM Users WHERE UserID == ?");
                ps2.setInt(1, senderID);
                ResultSet results2 = ps2.executeQuery();
                String username = results2.getString(1);
                PreparedStatement ps3 = Main.db.prepareStatement("SELECT CourseName FROM Courses WHERE CourseID == ?");
                ps3.setInt(1, courseID);
                ResultSet results3 = ps3.executeQuery();
                String coursename = results3.getString(1);
                System.out.println("Course: " + coursename + " From: " + username + " Message: " + message);
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    private static void addNewNotification(String coursename, String tags){
        try {

            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Courses (CourseName, Tags) VALUES (?, ?)");
            ps.setString(1, coursename);
            ps.setString(2, tags);

            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    private static void deleteCourse(String courseID) {
        try {
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Courses where CourseID == ?");
            ps.setString(1, courseID);
            ps.executeUpdate();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
}
