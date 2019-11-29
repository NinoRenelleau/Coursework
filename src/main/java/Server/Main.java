package Server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;

public class Main {

    public static Connection db = null;

    public static void main(String[] args) {
        openDatabase("Project-quiz.db");
        //Connect to the database file
        ResourceConfig config = new ResourceConfig();
        //Prepares the Jersey Servlet
        config.packages("Controller");
        //Uses the handlers in the 'Controller' package
        config.register(MultiPartFeature.class);
        //Supports multipart HTML forms
        Server server = new Server(8081);
        //Instantiate the Servlet

        ServletHolder servlet = new ServletHolder(new ServletContainer(config));
        //Prepares the Jetty Server to listen on port 8081
        ServletContextHandler context = new ServletContextHandler(server, "/");
        //Instantiate the server
        context.addServlet(servlet, "/*");
        //Connect the Servlet to the Server
         try{
             server.start();//Starts the server
             System.out.println("Server successfully started.");//Success message
             server.join();//The program waits here indefinitely
         } catch (Exception e){
             e.printStackTrace();// error catching
         }
    }

    //A standard method to connect to an SQLite database.
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


}