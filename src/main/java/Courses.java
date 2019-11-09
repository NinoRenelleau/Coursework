import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Courses {
    public static void list(){
        try {

            PreparedStatement ps = Main.db.prepareStatement("SELECT CourseID, Username, CourseName, Tags " +
                    "FROM Courses INNER JOIN Users ON Courses.UserID = Users.UserID");
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                int courseID = results.getInt(1);
                String username = results.getString(2);
                String coursename = results.getString(3);
                String tags = results.getString(4);
                System.out.println("ID: " + courseID + " Creator: " + username + " Title: " + coursename + " Tags: " + tags);
            }
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    public static void create(String coursename, String tags, int userID){
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

    public static void delete(int courseID) {
        try {
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Courses where CourseID == ?");
            ps.setInt(1, courseID);
            ps.executeUpdate();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    public static void searchByName(String InpCourse){
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT CourseID, Username, CourseName, Tags " +
                    "FROM Courses INNER JOIN Users ON Courses.UserID = Users.UserID WHERE CourseName LIKE ?");
            ps.setString(1, (InpCourse+"%"));
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                int courseID = results.getInt(1);
                String username = results.getString(2);
                String coursename = results.getString(3);
                String tags = results.getString(4);
                System.out.println("ID: " + courseID + " Creator: " + username + " Title: " + coursename + " Tags: " + tags);
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
    public static void searchByCreator(String InpUsername){
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT CourseID, Username, CourseName, Tags " +
                    "FROM Courses INNER JOIN Users ON Courses.UserID = Users.UserID WHERE Users.Username = ?");
            ps.setString(1, (InpUsername));
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                int courseID = results.getInt(1);
                String username = results.getString(2);
                String coursename = results.getString(3);
                String tags = results.getString(4);
                System.out.println("ID: " + courseID + " Creator: " + username + " Title: " + coursename + " Tags: " + tags);
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
    public static void searchByID(int courseID){
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT CourseID, Username, CourseName, Tags " +
                    "FROM Courses INNER JOIN Users ON Courses.UserID = Users.UserID WHERE CourseID = ?");
            ps.setInt(1, (courseID));
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                String username = results.getString(2);
                String coursename = results.getString(3);
                String tags = results.getString(4);
                System.out.println("ID: " + courseID + " Creator: " + username + " Title: " + coursename + " Tags: " + tags);
            }
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    public static void updateName(String coursename, int courseID){
        try {

            PreparedStatement ps = Main.db.prepareStatement("UPDATE Courses SET CourseName = ? WHERE CourseID = ?");
            ps.setString(1, (coursename));
            ps.setInt(2, (courseID));
            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    public static void searchByTags(String InpTags){
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT CourseID, Username, CourseName, Tags FROM Courses INNER JOIN Users ON Courses.UserID = Users.UserID WHERE Courses.Tags LIKE ?");
            ps.setString(1, ("%"+InpTags+"%"));
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                int courseID = results.getInt(1);
                String username = results.getString(2);
                String coursename = results.getString(3);
                String tags = results.getString(4);
                System.out.println("ID: " + courseID + " Creator: " + username + " Title: " + coursename + " Tags: " + tags);
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

}
