package QAAutomationManagers;

import QAAutomationObjects.Environment;
import QAAutomationUtils.SQLDatabase;

import java.sql.ResultSet;

public class EnvironmentManager {

    private String AutomationDatabase;

    public void Initialize(String AutomationDB)
    {
        AutomationDatabase = AutomationDB;
    }


    public Environment GetEnvironmentByName(String envName){

        Environment env = null;

        try {

            System.out.println ( "Checking whether Environment exists:" );

            boolean envExists = IsEnvironmentExists ( envName );

            if (envExists) {

                String procedureName = "usp_GetEnvironment";
                String [] parameters = {envName};
                ResultSet rs = SQLDatabase.ExecuteProcedure ( AutomationDatabase,procedureName,parameters );
                while (rs.next ())
                {
                    env = new Environment(){
                        {
                            Id = rs.getInt ( "EnvID");
                            Name = rs.getString ( "EnvironmentName");
                            Description = rs.getString ("Description");
                            ClusterName = rs.getString ("ClusterName");
                            EnvironmentType = rs.getString ("EnvType");
                        }
                    };
                }


            } else {
                System.out.println ( "Environment doesn't exists: exiting the application" );
                System.exit ( 0 );
            }
        }
        catch (Exception e)
        {
            System.out.println ( "Exception Occurred: Environment Manager" );
            throw  new RuntimeException ( e.getMessage () );
        }
        return  env;
    }

    public static boolean IsEnvironmentExists(String Name)
    {
        return true;
    }


}
