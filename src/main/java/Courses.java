import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Courses {
    public static void listCourses(){
        try {

            PreparedStatement ps = Main.db.prepareStatement("SELECT CourseID, UserID, CourseName, Tags FROM Courses");
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                int courseID = results.getInt(1);
                int userID = results.getInt(2);
                String coursename = results.getString(3);
                String tags = results.getString(4);
                PreparedStatement ps2 = Main.db.prepareStatement("SELECT Username FROM Users WHERE UserID == ?");
                ps2.setInt(1, userID);
                ResultSet results2 = ps2.executeQuery();
                String username = results2.getString(1);
                System.out.println("ID: " + courseID + " Creator: " + username + " Title: " + coursename + " Tags: " + tags);
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    private static void addNewCourse(String coursename, String tags, int userID){
        try {

            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Courses (CourseName, Tags, UserID) VALUES (?, ?, ?)");
            ps.setString(1, coursename);
            ps.setString(2, tags);
            ps.setInt(3, userID);

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
