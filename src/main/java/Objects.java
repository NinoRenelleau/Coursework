import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Objects {
    public static String getCoordinates(int objectID){
        String coordinates = "";
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT Coordinates FROM Object WHERE ObjectID == ?");
            ps.setInt(1, objectID);
            ResultSet results = ps.executeQuery();
            while (results.next()){
                coordinates = results.getString(1);
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
        return coordinates;
    }

    public static String getType(int objectID){
        String objectType = "";
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT ObjectType FROM Object WHERE ObjectID == ?");
            ps.setInt(1, objectID);
            ResultSet results = ps.executeQuery();
            while (results.next()){
                objectType = results.getString(1);
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
        return objectType;
    }

    public static void create(int templateID, String type, String coordinates){
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Object (QuestionTemplateID, ObjectType, Coordinates) VALUES (?, ?, ?)");
            ps.setInt(1, templateID);
            ps.setString(2, type);
            ps.setString(3, coordinates);

            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

    public static String list(){
        int ObjectID = 0;
        int QuestionTemplateID = 0;
        String ObjectType = "";
        String Coordinates = "";
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT * FROM Objects");
            ResultSet results = ps.executeQuery();
            while (results.next()){
                ObjectID = results.getInt(1);
                QuestionTemplateID = results.getInt(2);
                ObjectType = results.getString(3);
                Coordinates = results.getString(4);
            }

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
        return (ObjectID + "," + QuestionTemplateID + "," + ObjectType + "," + Coordinates);
    }

    public static void delete(int objectID){
        try {

            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Objectss WHERE ObjectID = ?");
            ps.setInt(1, objectID);
            ps.executeUpdate();

        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
        }
    }

}
