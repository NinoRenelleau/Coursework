import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class History {
    public static void createHistory(int userID, int quizID, int score, int review){
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
    public static void averageReview(){
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT QuizID FROM Quizzes");
            ResultSet results = ps.executeQuery();
            while (results.next()){
                int rating = 0;
                int num = 0;
                PreparedStatement ps2 = Main.db.prepareStatement("UPDATE Quizzes SET Rating = ? WHERE QuizID == ?");
                ps2.setInt(2, results.getInt(1));
                PreparedStatement ps3 = Main.db.prepareStatement("SELECT Review FROM History WHERE QuizID == ?");
                ps3.setInt(1, results.getInt(1));
                ResultSet results2 = ps3.executeQuery();
                while(results2.next()){
                    rating += results2.getInt(1);
                    num += 1;
                }
                rating = rating/num;
                ps2.setInt(1, rating);
                ps2.executeUpdate();
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }
    public static int totalCourseScore(int userID, int courseID){
        int total=0;
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT QuizID FROM Quizzes WHERE CourseID == ?");
            ps.setInt(1, courseID);
            ResultSet results = ps.executeQuery();
            while(results.next()){
                PreparedStatement ps2 = Main.db.prepareStatement("SELECT Score FROM History WHERE ((QuizID == ?) AND (UserID == ?)) ");
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
}
