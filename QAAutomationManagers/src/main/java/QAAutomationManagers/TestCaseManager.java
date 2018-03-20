package QAAutomationManagers;
import QAAutomationObjects.TestCaseType;
import QAAutomationObjects.Workflow;
import QAAutomationObjects.WorkflowTestCase;

import QAAutomationUtils.SQLDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestCaseManager {

    //region Member variables

    private String AutomationDatabase;

    //endregion

    //region Public methods

    public void Initialize(String AutomationDB) {
        AutomationDatabase = AutomationDB;
    }

    public List<WorkflowTestCase> GetTestCases(Workflow workflow,
                                               HashMap<String, TestCaseType> testCaseTypes,
                                               String ExecutionGuid, QAAutomationObjects.Environment env, boolean checkLicense) {
        List<WorkflowTestCase> workflowTestCases = null;
        try {

            ResultSet rs = LoadWFTestCases ( workflow.Id, AutomationDatabase );
            workflowTestCases = new ArrayList<WorkflowTestCase> ();
            if (rs != null) {
                while (rs.next ()) {
                    WorkflowTestCase workflowTestCase = new WorkflowTestCase ();

                    workflowTestCase.Id = rs.getLong ( "Id" );
                    workflowTestCase.TestCaseNumber = rs.getString ( "TestCaseNumber" );
                    workflowTestCase.TestCaseDescription = rs.getString ( "TestCaseDescription" );
                    workflowTestCase.DependentTestCase = rs.getString ( "DependentTestCase" );
                    workflowTestCase.SequenceId = rs.getInt ( "SequenceId" );
                    workflowTestCase.IsControl = rs.getBoolean ( "IsControl" );
                    String type = rs.getString ( "TestCaseType" );

                    TestCaseType testCaseType = testCaseTypes.get ( type );

                    workflowTestCase.TestCaseType = testCaseType;
                    workflowTestCase.Workflow = workflow;
                    workflowTestCase.ExecutionGuid = ExecutionGuid;

                    workflowTestCases.add ( workflowTestCase );
                }

            }
        } catch (Exception e) {
            System.out.println ( "Exception Occurred: GetTestCases:" + e.getMessage () );
            throw new RuntimeException ( e.getMessage () );
        }

        return workflowTestCases;
    }


    private static ResultSet LoadWFTestCases(int WorkflowId, String AutomationDB) {
        ResultSet rs = null;
        try {
           // String sql = String.format ( "SELECT [Id], [WorkFlowId], [SequenceId], [TestCaseType], [TestCaseNumber], [TestCaseDescription], " +
             //       " [DependentTestCase], [IsControl] FROM [dbo].[WorkflowTestCases] " +
               //     " WHERE [WorkFlowId] = %s AND IsControl = 1 ORDER BY SEQUENCEID", WorkflowId );
            String procedureName = "usp_LoadWFTestCases";
            String [] parameters = {String.valueOf ( WorkflowId )};

            rs = SQLDatabase.ExecuteProcedure ( AutomationDB, procedureName,parameters );


        } catch (Exception e) {
            System.out.println ( "Exception Occurred: Test Case Manager " + e.getMessage () );
            System.exit ( 1 );
        }
        return rs;
    }

    private String GetWorkflowList(HashMap<Integer, Workflow> workflowHashMap) {
        List<Integer> keyList = new ArrayList<Integer> ( workflowHashMap.keySet () );
        return keyList.toString ().substring ( 1, keyList.size () - 1 );
    }


}