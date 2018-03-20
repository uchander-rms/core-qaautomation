package QAAutomationManagers;

import QAAutomationObjects.TestCaseOutput;
import QAAutomationUtils.SQLDatabase;

import java.sql.ResultSet;

public class TestCaseOutputManager {
    private String AutomationDatabase;

    public void Initialize(String AutomationDB){
        AutomationDatabase = AutomationDB;
    }

    public TestCaseOutput GetOutput(int EnvironmentId, int WorkflowId, String ThreadGuid) {
        TestCaseOutput testCaseOutput = null;

        try {

            String procedureName = "usp_GetTestCaseoutputsData";
            String [] parameters = {String.valueOf (EnvironmentId), String.valueOf ( WorkflowId ), ThreadGuid};

            ResultSet rs = SQLDatabase.ExecuteProcedure ( AutomationDatabase, procedureName, parameters );

            while (rs.next ())
            {
                testCaseOutput = new TestCaseOutput (){ {
                    EnvironmentId =EnvironmentId;
                    WorkflowId =WorkflowId;
                    ExposureId = rs.getInt ( "ImportExposureId");
                    ExposureType =rs.getString (  "ImportExposureType");

                    AnalysisId =rs.getInt ( "RDMAnalysisId");
                    DestEDM =rs.getString (  "DestEDM");
                    ThreadGuid =ThreadGuid;
                }};
            }
        }
        catch (Exception e)
        {
            System.out.println ( "Exception Occurred: TestCaseOutputManager-> GetOutput:" + e.getMessage () );
            throw  new RuntimeException(e.getMessage ());
        }

        return testCaseOutput;
    }

    public void Delete(int EnvironmentId, int WorkflowId, String GUID)
    {
        System.out.println ( "Delete the environment before running any dependent test case:" );
    }

}
