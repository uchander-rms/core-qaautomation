package HazardTestEngine;

import QAAutomationConfiguration.RBUserConfig;
import QAAutomationManagers.HostCredentialManager;
import QAAutomationObjects.HostCredentials;
import QAAutomationObjects.TestCase;
import QAAutomationTestEngine.TestEngine;
import QAAutomationUtils.RBCommon;
import QAAutomationUtils.SQLDatabase;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.ResultSet;
import java.util.Arrays;

public class HazardTests extends TestEngine
{
    public boolean Run(TestCase testCase) {
        String authenticationToken = "";

        try {
            // region get authentication token
            authenticationToken = RBCommon.GetAuthToken ( RBUserConfig.GetInstance ().getEnvironment ().getName (),
                    RBUserConfig.GetInstance ().getEnvironment ().getAuthenticationPort (),RBUserConfig.GetInstance ().getUsers ());
            //end region

            String serviceName = testCase.KeyValues.get ( "LayerList" ).toString ();

            if(serviceName==null & serviceName.isEmpty ())
                throw  new RuntimeException ( "Exception Occurred: Services/LayerList missing" );

            //region get Profiles/Services details from webDB
            int[] services = Arrays.stream ( GetDependentServices ( serviceName, testCase.getAutomationDB (), testCase.getEnvironmentId () ).split ( "," ) ).mapToInt ( Integer::parseInt ).toArray ();

            //endregion

            //region process account

            String URL = RBUserConfig.GetInstance ().getEnvironment ().getName ()+  "/v1/accounts/process";
            JSONObject hazardInput = new JSONObject ();
            JSONArray jsonArray = new JSONArray ();

            for (int i : services
                    ) {
                jsonArray.add ( i );

            }

            hazardInput.put ( "id", testCase.KeyValues.get ( "ExposureID" ) );
            hazardInput.put ( "edm", testCase.KeyValues.get ( "EdmDatasource" ) );
            hazardInput.put ( "user", testCase.KeyValues.get ( "UserName" ) );
            hazardInput.put ( "analysisname", testCase.TestCaseNumber );
            hazardInput.put ( "profiles", jsonArray );

            System.out.println ( hazardInput.toJSONString () );
            String jobId = RBCommon.ProcessAccount ( URL, authenticationToken, hazardInput );

            System.out.println ( "Job id returned from the service is : " + jobId );

            //end region
        }
        catch (Exception ex)
        {
            throw new RuntimeException ( "Exception Occurred at Hazard Engine: " + ex.getMessage () );
        }

        return true;

    }


    public boolean Validate(TestCase TestCase) {

        System.out.println ( "DLM ENGINE VALIDATION PASSED:" + Thread.currentThread ().getId () );
        return true;
    }


    public static String GetDependentServices(String serviceName, String AutomationDB, int envId) {

        String dependentServices= "";
        try {
            HostCredentialManager hostCredentialManager = new HostCredentialManager ();
            hostCredentialManager.Initialize ( AutomationDB );

            HostCredentials hostCredentials = hostCredentialManager.GetCredential ( envId, "SQL" );
            if (hostCredentials == null)
                throw new RuntimeException ( "Failed to locate environment's SQL Server credentials" );


            String connectionString = "jdbc:sqlserver://" + hostCredentials.HostName + ":1433;databaseName=rms_web_sample;user=" +
                    hostCredentials.getSQLUserName () + ";password=" + hostCredentials.getSQLPassword ();

            String[] serviceNames = serviceName.split ( "," );
            for (String servName:serviceNames
                 ) {

                String sql = "SELECT  Serviceid, fieldtext FROM Service " +
                        "WHERE FieldName = '" + servName + "'";

                ResultSet rs = SQLDatabase.ExecuteQuery ( connectionString, sql );
                String fieldText = "";
                while (rs.next ()) {
                    dependentServices = dependentServices + rs.getString ( "ServiceId" );
                    fieldText = rs.getString ( "FieldText" );
                }

                fieldText = ParseString ( fieldText );
                dependentServices = (dependentServices + "," + fieldText).trim ();
            }

        }

        catch (Exception ex)
        {
            System.out.println ( ex.getMessage () );
        }
        return dependentServices;

    }


    public static String ParseString(String text) {
        if (!text.isEmpty ()) {
            String[] str = text.split ( "=" );
            String[] fieldTexts = str[1].split ( "-" );

            String fieldText = fieldTexts[0].trim ().replace ( " ", "," );
            return fieldText;
        }
        else
            return "";
    }

}
