package GeoTestEngine;

import QAAutomationConfiguration.RBUserConfig;
import QAAutomationTestEngine.TestEngine;
import QAAutomationManagers.*;
import QAAutomationObjects.*;
import QAAutomationUtils.RBCommon;
import QAAutomationUtils.SQLDatabase;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import QAAutomationConfiguration.Configuration;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.util.Arrays;

public class GeocodeTest extends TestEngine {



    public boolean Run(TestCase testCase)
    {

        String authenticationToken = "";


        try {
            // region get authentication token
            authenticationToken = RBCommon.GetAuthToken ( RBUserConfig.GetInstance ().getEnvironment ().getName (),
                    RBUserConfig.GetInstance ().getEnvironment ().getAuthenticationPort (),RBUserConfig.GetInstance ().getUsers ());

            String queryParam = " ";
            queryParam = URLEncoder.encode(testCase.KeyValues.get ( "EdmDatasource" ).toString (), "UTF-8");
            String URL = RBUserConfig.GetInstance ().getEnvironment ().getName ()+  "/v1/accounts/" +
                    testCase.KeyValues.get ( "ExposureID" ).toString () +
                    "/geocode" + "?datasource=" + queryParam  ;

            String jobId = RBCommon.GeocodeAccount(URL, authenticationToken);
            System.out.println ( "Job Id returned from geocode service is: "  + jobId);
        }
        catch (Exception ex)
        {
            System.out.println ( ex.getMessage () );
            throw new RuntimeException ( "Exception occurred: GeocodeTest: " + ex.getMessage () );
        }

        return true;
    }

    public boolean Validate(TestCase TestCase)
    {
        return true;
    }
}
