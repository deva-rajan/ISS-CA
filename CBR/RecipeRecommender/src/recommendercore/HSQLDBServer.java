package recommendercore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import jcolibri.test.database.SqlFile;
import jcolibri.util.FileIO;

import org.hsqldb.Server;


/**
 * Creates a data base server with the tables for the examples/tests using the HSQLDB library.
 * @author Juan A. Recio-Garcia
 * @version 1.0
 */
public class HSQLDBServer
{
    static boolean initialized = false;

    private static Server server;

    /**
     * Initialize the server
     */
    public static void init()
    {
	if (initialized)
	    return;
        org.apache.commons.logging.LogFactory.getLog(HSQLDBServer.class).info("Creating data base ...");

	server = new Server();
	server.setDatabaseName(0, "recipe");
	server.setDatabasePath(0, "mem:recipe;sql.enforce_strict_size=true");
	
	/*server.setDatabaseName(1, "travelext");
	server.setDatabasePath(1, "mem:travelext;sql.enforce_strict_size=true");*/
	
	server.setLogWriter(null);
	server.setErrWriter(null);
	server.setSilent(true);
	server.start();

	initialized = true;
	try
	{
	    Class.forName("org.hsqldb.jdbcDriver");

	    PrintStream out = new PrintStream(new ByteArrayOutputStream());
	    Connection conn = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/recipe", "sa", "");
	    SqlFile file = new SqlFile(new
	    File(FileIO.findFile("/home/deva/workspace/luna/RecipeRecommender/src/recommendercore/recipe.sql").getFile()),false,new HashMap());
	    file.execute(conn,out,out, true);
	    
	   /* Connection connExt = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/travelext", "sa", "");
	    SqlFile fileExt = new SqlFile(new
	    File(FileIO.findFile("jcolibri/test/database/travelext.sql").getFile()),false,new HashMap());
	    fileExt.execute(connExt,out,out, true);*/
       /* Statement stmt = conn.createStatement();*/
        //(caseId,CuisineType,DietType,MealType,ingredients,dishName,method)
     /*   ResultSet rs=stmt.executeQuery("insert into recipe values(1,'Indian','Vegetarian','Breakfast','Tomato','Upma','dummy')");
        while(rs.next()){
        	System.err.println("********* "+rs.getString("CuisineType"));
        }
        */
	    org.apache.commons.logging.LogFactory.getLog(HSQLDBServer.class).info("Data base generation finished");
	    
	} catch (Exception e)
	{
	    org.apache.commons.logging.LogFactory.getLog(HSQLDBServer.class).error(e);
	}

    }

    /**
     * Shutdown the server
     */
    public static void shutDown()
    {

	if (initialized)
	{
	    server.stop();
	    initialized = false;
	}
    }

    /**
     * Testing method
     */
    public static void main(String[] args)
    {
    HSQLDBServer.init();
    HSQLDBServer.shutDown();
	System.exit(0);
	
    }

}