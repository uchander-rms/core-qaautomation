package QAAutomationManagers;

import QAAutomationObjects.HostCredentials;
import QAAutomationUtils.SQLDatabase;

import java.sql.ResultSet;

public class HostCredentialManager {
    private String AutomationDatabase;

    public void Initialize(String AutomationDB) {
        AutomationDatabase = AutomationDB;
    }


    public HostCredentials GetCredential(int environmentId, String role) {
        HostCredentials hostCredentials = null;


        try {
            String sql = "SELECT HostName, Domain, UserName, Password, SQLUser, SQLPassword From dbo.Configuration where role = '" + role + "' and EnvId = " + environmentId;

            ResultSet rs = SQLDatabase.ExecuteQuery ( AutomationDatabase, sql );

            while (rs.next ()) {
                hostCredentials = new HostCredentials () {{

                    HostName = rs.getString ( "HostName" ).toString ();
                    Domain = rs.getString ( "Domain").toString ();
                    Role = Role;
                    UserName = rs.getString ( "UserName").toString ();
                    Password = rs.getString("Password").toString ();
                    SQLUserName = rs.getString("SQLUser").toString ();
                    SQLPassword = rs.getString("SQLPassword").toString ();

                }};
            }
        } catch (Exception e) {

        }

        return hostCredentials;
    }


}

