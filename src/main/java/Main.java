import org.sqlite.SQLiteConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

public class Main {

    public static Connection db = null;

    public static void main(String[] args) {
        openDatabase("Project-quiz.db");
        Scanner input = new Scanner(System.in);
        boolean valid = false;
        while (valid == false){
            System.out.println("enter a valid username:");
            String username = input.nextLine();
            System.out.println("enter a valid password:");
            String password = input.nextLine();
            if ((usernameValid(username) == true))
        }

        Users.login();

        closeDatabase();
    }

    private static void openDatabase(String dbFile) {
        try  {
            Class.forName("org.sqlite.JDBC");
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            db = DriverManager.getConnection("jdbc:sqlite:resources/" + dbFile, config.toProperties());
            System.out.println("Database connection successfully established.");
        } catch (Exception exception) {
            System.out.println("Database connection error: " + exception.getMessage());
        }

    }

    private static void closeDatabase(){
        try {
            db.close();
            System.out.println("Disconnected from database.");
        } catch (Exception exception) {
            System.out.println("Database disconnection error: " + exception.getMessage());
        }
    }

    private static boolean usernameValid(String username){
        boolean valid = true;
        int nameLen = username.length();
        if(nameLen > 15){
            valid = false;
            System.out.println("Inputted username is invalid; must be less than 15 characters long");
        }
        if (Users.usernameExists(username))valid = false;
        return valid;
    }

    private static boolean passwordValid(String password){
        boolean valid = true;
        int numCount = 0;
        int upperCount = 0;
        int passLen = password.length();
        if (passLen > 8){
            valid = false;
            System.out.println("Inputted password is invalid; must be more than 8 characters long");
        }
        for(int x = 0; x < passLen; x++){
            if (Character.isUpperCase(password.charAt(x))){
                upperCount += 1;
            }else if (Character.isDigit(password.charAt(x))){
                numCount += 1;
            }
        }
        if (upperCount < 2){
            valid = false;
            System.out.println("Inputted password is invalid; must have at least 2 uppercase characters");
        }
        if (numCount < 3){
            valid = false;
            System.out.println("Inputted password is invalid; must have at least 3 digits");
        }
        return valid;
    }
}