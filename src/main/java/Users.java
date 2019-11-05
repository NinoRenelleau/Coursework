
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class Users {
    public static void searchUsers(String Inpusername){
        try {

            PreparedStatement ps = Main.db.prepareStatement("SELECT Username FROM Users WHERE Username LIKE ?");
            ps.setString(1, (Inpusername+"%"));
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                String username = results.getString(1);
                System.out.println(username);
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    public static String login(String username, String password){
        int userId;
        String userType = "";
        String tags = "";
        int score = 0;
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT UserID, UserType, Tags, Score FROM Users WHERE (Username = ?) AND (Password = ?) ");
            ps.setString(1, username);
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                userId = results.getInt(1);
                userType = results.getString(2);
                tags = results.getString(3);
                score = results.getInt(4);
            }
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
        return (username + "," + password + "," + userType +"," + tags + "," + score);
    }

    public static List getUserFromName(String username){
        List<String> out = new ArrayList<String>();
        int userID = 0;
        String userType = "";
        String tags = "";
        int score = 0;
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT UserID, UserType, Tags, Score FROM Users WHERE Username == ?");
            ps.setString(1, username);
            ResultSet results = ps.executeQuery();
            userID = results.getInt(1);
            userType = results.getString(2);
            tags = results.getString(3);
            score = results.getInt(4);
            String UserID = String.valueOf(userID);
            String Score = String.valueOf(score);
            out.add(UserID);
            out.add(username);
            out.add(userType);
            out.add(tags);
            out.add(Score);
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
        return out;
    }
    public static String getUserFromID(int userID){
        String username = "";
        String password = "";
        String userType = "";
        String tags = "";
        int score = 0;
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT * FROM Users WHERE UserID == ?");
            ps.setString(1, username);
            ResultSet results = ps.executeQuery();
            username = results.getString(2);
            password = results.getString(3);
            userType = results.getString(4);
            tags = results.getString(5);
            score = results.getInt(6);
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
        return (userID + "," + username + "," + password + "," + userType +"," + tags + "," + score);
    }


    public static void deleteUser(int UserID) {
        try {
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Users where UserID == ?");
            ps.setInt(1, UserID);
            ps.executeUpdate();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
    public static void addNewUser(String username, String password, String UserType, String tags, int score){
        try {

            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Users (Username, Password, UserType, Tags, Score) VALUES (?, ?, ?, ?, ?)");

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, UserType);
            ps.setString(4, tags);
            ps.setInt(5, score);

            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
    public static void updateUserPassword(int UserID, String newPassword){
        try {
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Users SET Password = ? where UserID = ?");
            ps.setInt(2, UserID);
            ps.setString(1, newPassword);
            ps.executeUpdate();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
    public static void updateUserScore(int userID){
        try {
            int newScore = 0;
            PreparedStatement lookupScore = Main.db.prepareStatement("SELECT Score FROM History WHERE UserID == ?");
            lookupScore.setInt(1, userID);
            ResultSet results = lookupScore.executeQuery();
            while (results.next()) {
                newScore += results.getInt(1);
            }
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Users SET Score = ? where UserID = ?");
            ps.setInt(2, userID);
            ps.setInt(1, newScore);
            ps.executeUpdate();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    public static boolean usernameExists(String username){
        boolean found = false;
        try {
            PreparedStatement ps = Main.db.prepareStatement("Select Exists(SELECT * FROM Users WHERE Username == ?)");
            ps.setString(1, username);
            ResultSet results = ps.executeQuery();
            if (results.getBoolean(1) == true){
                found = true;
            } else{
                found = false;
            }
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
        return found;
    }

    public static boolean passwordExists(String username, String password){
        boolean found = false;
        try {
            PreparedStatement ps = Main.db.prepareStatement("Select Exists(SELECT * FROM Users WHERE Username == ? AND Password == ?)");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet results = ps.executeQuery();
            if (results.getBoolean(1) == true){
                found = true;
            } else{
                found = false;
            }
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
        return found;
    }

}
