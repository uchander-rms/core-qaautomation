package QAAutomationUtils;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import QAAutomationObjects.TestCaseResult;
import QAAutomationObjects.TestRailStatus;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class TestRailClient {

    private boolean caseID;

    public static void UpdateResults(String userName, String userPass, String runId, String URL, List<TestCaseResult>  testCaseResults) throws IOException, APIException {

    try {
        APIClient client = new APIClient ( URL );
        client.setUser ( userName);
        client.setPassword ( userPass );
        JSONArray rObj = null;

        HashMap<String, String> caseIds = new HashMap<> (  );


        try {
            rObj = (JSONArray) client.sendGet ( "get_tests/" + runId);

            for(int i = 0; i < rObj.size (); i ++)
            {
                JSONObject jObj= (JSONObject) rObj.get ( i );
                String title = jObj.get ( "title" ).toString ();
                String caseId = jObj.get ( "case_id" ).toString ();
                caseIds.put ( title, caseId );
            }
           // System.out.println ( rObj.toJSONString () );
        } catch (Exception e) {
            System.out.println ( "Exception" + e.getMessage () );
        }

        for (TestCaseResult testCaseResult: testCaseResults
             ) {
            int result = 5;
            if(testCaseResult.getTestCaseStatus () == TestRailStatus.PASSED)
                result = 1;

            String caseId = caseIds.get("EGC_Stress_Day1"); // ( testCaseResult.getTestCaseNumber ());
            Map data = new HashMap ();
            data.put ( "status_id", result) ;
            data.put ( "comment", "This test completed using automation" );

            try {
                JSONObject r = (JSONObject) client.sendPost ( "add_result_for_case/" + runId + "/" + caseId ,data);
                System.out.println ( r.toJSONString () );
            } catch (Exception e) {
                System.out.println ( "Exception" + e.getMessage () );
            }

        }

    }
    catch (Exception e)
    {
        System.out.println ( "Exception" + e.getMessage () );
    }


}




}
