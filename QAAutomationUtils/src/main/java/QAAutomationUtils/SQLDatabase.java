package QAAutomationUtils;

import org.apache.log4j.Logger;

import java.sql.*;

public class SQLDatabase {

    private static final Logger logger = Logger.getLogger ( SQLDatabase.class );

    public static void OpenConnection(String url, String jdbcDriver){
        final String JDBC_DRIVER = jdbcDriver;

        //  Database credentials
        Connection conn = null;
        Statement stmt = null;

        try {
            //STEP 2: Register JDBC driver
            Class.forName ( jdbcDriver );

            //STEP 3: Open a connection
            System.out.println ( "Connecting to database..." );
            conn = DriverManager.getConnection ( url );
        }
        catch (Exception e)
        {
            logger.error ( e.getMessage (), new Exception ( "Exception Occurred while opening SQL Connection : SQLDatabae" ) );
            System.out.println ( e.getMessage() +" Exception Occurred while opening SQL Connection : SQLDatabae" );
        }
    }

    public static ResultSet ExecuteQuery(String url, String sql) throws SQLException, ClassNotFoundException{

        ResultSet rs = null;

        try {

            Connection conn = GetConnection ( url );
            Statement stmt = conn.createStatement ();
            rs = stmt.executeQuery ( sql );
        }

        catch (SQLException ex)
        {
            System.out.println ( "SQL exception: SQL Database: ExecuteQuery" );
            throw new RuntimeException ( ex.getMessage () );
        }
        /*
        finally {
            conn.close ();
        }
        */
        return  rs;
    }

    public static  String CreateParaters(String[] parameters)
    {
        String paramString = "";
        for(int i =1 ; i <= parameters.length; i++)
        {
            if (i ==1)
                paramString = "?";
            else
                paramString = paramString + ",?";
        }

        return  paramString;

    }

    public static ResultSet ExecuteProcedure(String url, String procedureName, String [] parameters)
    {
        ResultSet rs =null;

        try {

            String paramString = CreateParaters ( parameters );
            Connection conn = GetConnection ( url );
            PreparedStatement pstmt = conn.prepareStatement ("{call " + procedureName + "("+ paramString +")}"  );
            int counter = 1;

            for (String param: parameters
                 ) {

                pstmt.setString ( counter,param  );
                counter++;
            }

            rs = pstmt.executeQuery();
        }

        catch (SQLException ex)
        {
            System.out.println ( "SQL exception: SQL Database: ExecuteProcedure" );
            throw new RuntimeException ( ex.getMessage () );
        }
        catch (ClassNotFoundException ex)
        {
            System.out.println ( "Class not found exception: SQL Database: ExecuteProcedure" );
            throw new RuntimeException ( ex.getMessage () );
        }


        return  rs;

    }
    public static Connection GetConnection(String url) throws SQLException, ClassNotFoundException{


        // JDBC driver name and database URL
        String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String DB_URL = url; //"jdbc:mysql://localhost/EMP";


        Connection conn = null;
        Statement stmt = null;

        try {


            //STEP 2: Register JDBC driver
            Class.forName ( "com.microsoft.sqlserver.jdbc.SQLServerDriver" );

            //STEP 3: Open a connection
            System.out.println ( "Connecting to database..." );
            //conn = DriverManager.getConnection ( DB_URL, USER, PASS );

            conn = DriverManager.getConnection ( DB_URL );
        }
        catch (ClassNotFoundException e)
        {
            System.out.println ( "Class not found exception: SQL Database: ExecuteQuery" );
            throw new RuntimeException ( e.getMessage () );
        }
       return conn;
    }

}
