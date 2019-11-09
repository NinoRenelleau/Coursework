import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class History {
    public static void create(int userID, int quizID, int score, int review){
        try {

            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO History (userID, QuizID, Score, Review) VALUES (?, ?, ?, ?)");
            ps.setInt(1, userID);
            ps.setInt(2, quizID);
            ps.setInt(3, score);
            ps.setInt(4, review);
            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    public static void update(int userID, int quizID, int score, int review){
        try {

            PreparedStatement ps = Main.db.prepareStatement("UPDATE History SET Score = ?, Review = ? WHERE UserID = ?, QuizID = ?");
            ps.setInt(3, userID);
            ps.setInt(4, quizID);
            ps.setInt(1, score);
            ps.setInt(2, review);
            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    public static void averageReview(){
        try {
            PreparedStatement ps = Main.db.prepareStatement(
                    "SELECT QuizID FROM Quizzes");
            ResultSet results = ps.executeQuery();
            while (results.next()){
                int rating = 0;
                int num = 0;
                PreparedStatement ps2 = Main.db.prepareStatement(
                        "UPDATE Quizzes SET Rating = " +
                                "(SELECT AVG(Review) FROM History WHERE QuizID = ?) WHERE QuizID = ?");
                ps2.setInt(1, results.getInt(1));
                ps2.setInt(2, results.getInt(1));
                ps2.executeUpdate();
            }
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    public static int totalCourseScore(int userID, int courseID){
        int total=0;
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT QuizID FROM Quizzes WHERE CourseID = ?");
            ps.setInt(1, courseID);
            ResultSet results = ps.executeQuery();
            while(results.next()){
                PreparedStatement ps2 = Main.db.prepareStatement("SELECT Score FROM History WHERE ((QuizID = ?) AND (UserID = ?)) ");
                ps2.setInt(1, results.getInt(1));
                ps2.setInt(2, userID);
                ResultSet results2 = ps2.executeQuery();
                while(results2.next()){
                    total += results2.getInt(1);
                }
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
        return total;
    }

    public static void list(){
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT * FROM History");
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                int UserID = results.getInt(1);
                int QuizID = results.getInt(2);
                int score = results.getInt(3);
                int review = results.getInt(4);
                System.out.println("User ID: " + UserID + " Quiz ID: " + QuizID + " Score: " + score + " Review: " + review);
            }
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
}
