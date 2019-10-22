import org.sqlite.SQLiteConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static Connection db = null;

    public static void main(String[] args) {
        openDatabase("Project-quiz.db");
        Scanner input = new Scanner(System.in);
        /*String username = "";
        String password = "";
        String tags = "";
        String userType = "";
        int score = 0;
        boolean valid = false;
        while (valid == false){
            System.out.println("enter a valid username:");
            username = input.nextLine();
            System.out.println("enter a valid password:");
            password = input.nextLine();
            System.out.println("enter a valid user type:");
            userType = input.nextLine();
            System.out.println("enter tags relating to what you study:");
            tags = input.nextLine();
            if ((usernameValid(username)) && (passwordValid(password)) && (userTypeValid(userType))){
                valid = true;
            }
        }
        System.out.println("Successfully created a new user...");
        Users.addNewUser(username, password, userType, tags, score);*/

        String password = "";
        String username = "";
        String newPassword = "";
        boolean valid = false;
        boolean access = false;
        while (access == false){
            System.out.println("Enter username: ");
            username = input.nextLine();
            System.out.println("Enter password: ");
            password = input.nextLine();
            if (Users.passwordExists(username, password)){
                access = true;
            } else if (Users.usernameExists(username)){
                System.out.println("Password is incorrect");
            } else{
                System.out.println("Username does not exist");
            }
        }
        System.out.println("Successfully logged in...");
        List<String> userRec = new ArrayList<String>();
        userRec = Users.getUserFromName(username);
        int userId = Integer.parseInt(userRec.get(0));
        while (valid == false){
            System.out.println("Enter a new password: ");
            newPassword = input.nextLine();
            System.out.println("enter the new password again: ");
            if (!newPassword.equals(input.nextLine())){
                System.out.println("Passwords don't match try again");
            } else{
                if(passwordValid(newPassword)){
                    valid = true;
                }
            }
        }
        System.out.println("Successfully changed password...");
        Users.updateUserPassword(userId, newPassword);

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
        List<String> errors = new ArrayList<String>();
        int nameLen = username.length();
        if(nameLen > 15){
            valid = false;
            errors.add("must be less than 15 characters long");
        }
        if (Users.usernameExists(username)){
            valid = false;
            errors.add("Username already exists, try another one");
        }
        if (valid == false){
            System.out.println("Inputted username is invalid; " + errors);
        }
        return valid;
    }

    private static boolean passwordValid(String password){
        List<String> errors = new ArrayList<String>();
        boolean valid = true;
        int numCount = 0;
        int upperCount = 0;
        int passLen = password.length();
        if (passLen < 8){
            valid = false;
            errors.add("must be more than 8 characters long");
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
            errors.add("must have at least 2 uppercase characters");
        }
        if (numCount < 3){
            valid = false;
            errors.add("must have at least 3 digits");
        }
        if (valid == false){
            System.out.println("Inputted password is invalid: " + errors);
        }
        return valid;
    }

    private static boolean userTypeValid(String userType){
        boolean valid = true;
        if ((!userType.equalsIgnoreCase("teacher")) && (!userType.equalsIgnoreCase("student"))){
            valid = false;
            System.out.println("Inputted userType is invalid: must be either 'teacher' or 'student'");
        }
        return valid;
    }
}