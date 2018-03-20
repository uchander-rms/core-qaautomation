package DLMTestEngine;

import QAAutomationConfiguration.RBUserConfig;
import QAAutomationTestEngine.TestEngine;
import QAAutomationManagers.*;
import QAAutomationObjects.*;
import QAAutomationUtils.RBCommon;
import QAAutomationUtils.SQLDatabase;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import QAAutomationConfiguration.Configuration;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.sql.ResultSet;
import java.util.Arrays;

public class DLMTests extends TestEngine {

    public boolean Run(TestCase testCase) {
        String authenticationToken = "";

        try {
            // region get authentication token
            authenticationToken = RBCommon.GetAuthToken ( RBUserConfig.GetInstance ().getEnvironment ().getName (),
                    RBUserConfig.GetInstance ().getEnvironment ().getAuthenticationPort (),RBUserConfig.GetInstance ().getUsers ());
            //end region

            String serviceName ="";

            //region Get input details i.e. account
            try {
               serviceName  = testCase.KeyValues.get ( "Services" ).toString ();
            }
            catch (Exception ex){}

            /*
            try {
                 serviceName = testCase.KeyValues.get ( "LayerList" ).toString ();
            }
            catch (Exception ex){}
            */
            //end region

            if(serviceName==null & serviceName.isEmpty ())
                throw  new RuntimeException ( "Exception Occurred: Services/LayerList missing" );

            //region get Profiles/Services details from webDB
            int[] services = Arrays.stream ( GetDependentServices ( serviceName, testCase.getAutomationDB (), testCase.getEnvironmentId () ).split ( "," ) ).mapToInt ( Integer::parseInt ).toArray ();

            //end region

            //region process account

            String URL = RBUserConfig.GetInstance ().getEnvironment ().getName ()+  "/v1/accounts/process";
            JSONObject dlmInput = new JSONObject ();
            JSONArray jsonArray = new JSONArray ();

            for (int i : services
                    ) {
                jsonArray.add ( i );

            }

            dlmInput.put ( "id", testCase.KeyValues.get ( "ExposureID" ) );
            dlmInput.put ( "edm", testCase.KeyValues.get ( "EdmDatasource" ) );
            dlmInput.put ( "user", testCase.KeyValues.get ( "UserName" ) );
            dlmInput.put ( "analysisname", testCase.TestCaseNumber );
            dlmInput.put ( "profiles", jsonArray );

            System.out.println ( dlmInput.toJSONString () );
            String jobId = RBCommon.ProcessAccount ( URL, authenticationToken, dlmInput );

            System.out.println ( "Job id returned from the service is : " + jobId );

            //end region
        }
        catch (Exception ex)
        {
            throw new RuntimeException ( "Exception Occurred at DLM Test Engine: " + ex.getMessage () );
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
            String sql = "SELECT  Serviceid, fieldtext FROM Service " +
                    "WHERE FieldName = '" + serviceName + "'";

            ResultSet rs = SQLDatabase.ExecuteQuery ( connectionString, sql );
            String fieldText = "";
            while (rs.next ())
            {
                dependentServices = dependentServices + rs.getString ( "ServiceId" );
                fieldText = rs.getString ( "FieldText" );
            }

            fieldText = ParseString(fieldText);
            dependentServices = dependentServices + "," + fieldText;

        }

        catch (Exception ex)
        {
            System.out.println ( ex.getMessage () );
        }
        return dependentServices;

    }


    public static String ParseString(String text)
    {
        String[] str = text.split ( "=" );
        String [] fieldTexts = str[1].split ( "-" );

        String fieldText = fieldTexts[0].trim ().replace ( " ", "," );
        return  fieldText;
    }
}
