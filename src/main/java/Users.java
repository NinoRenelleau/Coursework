
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class Users {
    public static void getUserFromType(String InpUsername){
        int nameLen = InpUsername.length();
        try {

            PreparedStatement ps = Main.db.prepareStatement("SELECT Username FROM Users");

            ResultSet results = ps.executeQuery();
            while (results.next()) {
                String username = results.getString(1);
                if (username.substring(0, nameLen).equals(InpUsername)){
                    System.out.println(username);
                }
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    public static String login(String username, String password){
        int userId;
        String userType = "";
        String tags = "";
        int score;
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
        return (username + "," + password + "," + userType +"," + tags);
    }

    public static int getUserID(String username){
        int userID = 0;
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT UserID FROM Users WHERE Username == ?");
            ps.setString(1, username);
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                userID = results.getInt(1);

            }
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
        return userID;
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
    public static void addNewUser(String username, String password, String UserType, String tags){
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
            PreparedStatement ps = Main.db.prepareStatement("Exists(SELECT * FROM Users WHERE Username == ?)");
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
            PreparedStatement ps = Main.db.prepareStatement("Exists(SELECT * FROM Users WHERE Username == ? AND Password == ?)");
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
