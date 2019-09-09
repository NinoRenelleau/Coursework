import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Objects {
    public static String getObjectCoordinates(int objectID){
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

    public static String getObjectType(int objectID){
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

    public static void createObject(int templateID, String type, String coordinates){
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

}
