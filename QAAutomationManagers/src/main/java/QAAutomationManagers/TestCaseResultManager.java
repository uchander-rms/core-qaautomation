package QAAutomationManagers;

import QAAutomationObjects.*;
import QAAutomationUtils.SQLDatabase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
public class TestCaseResultManager {
    private String AutomationDatabase;

    public void Initialize(String AutomationDB){
        AutomationDatabase = AutomationDB;
    }

    public List<TestCaseResult> GetTestCases(int environmentId, int workflowId, String executionGuid)
    {
        List<TestCaseResult> testCaseResults = new ArrayList<TestCaseResult> (  );

        try
        {
            String procedureName  = "usp_GetTestCaseResults";
            String[] parameters = {String.valueOf (  environmentId), String.valueOf ( workflowId), executionGuid};
            ResultSet rs = QAAutomationUtils.SQLDatabase.ExecuteProcedure ( AutomationDatabase, procedureName, parameters);

            TestCaseResult testCaseresults = new TestCaseResult ();

            while (rs.next ()) {
                String testStatus = rs.getString ( "TestStatus" ) ;
                TestRailStatus testRailStatus = null;
                if(testStatus.equals ( "1" ))
                    testRailStatus =  TestRailStatus.PASSED ;
                else
                    testRailStatus =  TestRailStatus.FAILED ;

                TestRailStatus finalTestRailStatus = testRailStatus;

                TestCaseResult testCaseResult = new TestCaseResult()
                {
                    {
                    TestCaseResultsId = rs.getInt (  "Id");
                    TestCaseNumber = rs.getString ( "TestCaseNumber");
                    TestCaseType = rs.getString (  "TestCaseType");
                    TestCaseDescription = rs.getString (  "TestCaseDescription");
                    TestCaseStatus = finalTestRailStatus;
                    TestCaseDetail = rs.getString ( "TestDetails");
                }};

                testCaseResults.add (testCaseResult);

            }

        }
        catch (Exception ex)
        {
            System.out.println ( "Exception Occurred: TestCaseResultManager->GetTestCases:" + ex.getMessage ());
            throw  new RuntimeException ( ex.getMessage () );
        }

        return testCaseResults;
    }

    public int GetTotalTestCaseRuns(int environmentId, int workflowId, String executionGuid, Date stressInstanceFinishTime, Date stressInstanceStartTime)
    {

        try {
         String sql = "SELECT count(*) count " +
                    "FROM [dbo].[TestCaseResults]" +
                    String.format(" WHERE workflowId = %s AND ", workflowId) +
                 String.format("EnvId = %s AND ", environmentId) +
                 String.format("executionGuid = '%s' AND ", executionGuid) +
                 String.format("StartTime >= '%s' AND ", stressInstanceStartTime) +
                 String.format("EndTime <= '%s' ", stressInstanceFinishTime) +
                    "Group By workflowId,  EnvId , executionGuid ";
         ResultSet rs = SQLDatabase.ExecuteQuery ( AutomationDatabase, sql );
         while (rs.next ()) {
             return rs.getInt ( "count" );
         }
        }
        catch (Exception e)
        {
            System.out.println ( "Exception Occurred: TestCaseResultManager-> GetTotalTestCaseRuns:" + e.getMessage () );
            throw new RuntimeException ( e.getMessage () );
        }
        return 0;
    }

    public void UpdateStatus(TestCaseResult TestCaseResult)
    {
            try {
                String sql = "INSERT INTO [dbo].[TestCaseResults] ([TestCaseNumber], [TestCaseDescription], [WorkFlowId], [WorkFlowName], " +
                        "[WorkflowOwner], [TestCaseType], [TestStatus], [TestDetails], [EnvID], [executionGuid], [TestIteration], [ExecutedBy], " +
                        "[EnviormentRanOn], [StartTime], [EndTime], [JobId], [JobStatusCode]) VALUES" +
                        "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";


                PreparedStatement preparedStatement = SQLDatabase.GetConnection ( AutomationDatabase ).prepareStatement ( sql );
                preparedStatement.setString ( 1, TestCaseResult.TestCaseNumber );
                preparedStatement.setString ( 2, TestCaseResult.TestCaseDescription );
                preparedStatement.setInt ( 3, TestCaseResult.WorkflowId );
                preparedStatement.setString ( 4, TestCaseResult.WorkflowName );
                preparedStatement.setString ( 5, TestCaseResult.WorkflowOwner );
                preparedStatement.setString ( 6, TestCaseResult.TestCaseType );

                boolean status = false;
                if(TestCaseResult.TestCaseStatus.toString ().toUpperCase ().equals ( "PASSED"))
                    status = true;

                preparedStatement.setBoolean ( 7,  status);
                preparedStatement.setString ( 8, TestCaseResult.TestCaseDetail );
                preparedStatement.setInt ( 9, TestCaseResult.EnvironmentId );
                preparedStatement.setString ( 10, TestCaseResult.ExecutionGuid );
                preparedStatement.setInt ( 11, TestCaseResult.Iteration );
                preparedStatement.setString ( 12, TestCaseResult.ExecutedBy );
                preparedStatement.setString ( 13, TestCaseResult.EnvironmentName );

                java.sql.Timestamp startTime = new java.sql.Timestamp (  TestCaseResult.getStartTime ().getTime ());
                java.sql.Timestamp endTime = new java.sql.Timestamp ( TestCaseResult.getEndTime ().getTime () );

                preparedStatement.setTimestamp ( 14,  startTime);
                preparedStatement.setTimestamp ( 15, endTime);
                preparedStatement.setInt ( 16, TestCaseResult.JobId );
                preparedStatement.setString ( 17, TestCaseResult.JobStatusCode );

                preparedStatement.executeUpdate ();


            }
            catch (ClassNotFoundException c)
            {
                System.out.println ( "Exception Occurred: UpdateStatus:"+ c.getMessage () );
                throw  new RuntimeException ( c.getMessage () );
            }
            catch (SQLException ex)
            {
                System.out.println ( "Exception Occurred: UpdateStatus:"+ ex.getMessage () );
                throw  new RuntimeException ( ex.getMessage () );
            }
        }

    public TestCaseResultOutput GetStressTestCasesOutput(int environmentId, long workflowId, String executionGuid)
    {


        TestCaseResultOutput testCaseResultOutput = new TestCaseResultOutput ();
        try {

            int passedCounter = 0;
            int failedCounter = 0;
            String procedureName = "usp_GetTestCaseResults";
            String[] parameters = {String.valueOf ( environmentId ), String.valueOf ( workflowId ), executionGuid};
            ResultSet rs = QAAutomationUtils.SQLDatabase.ExecuteProcedure ( AutomationDatabase, procedureName, parameters );

            TestCaseResult testCaseresults = new TestCaseResult ();

            while (rs.next ()) {
                String testStatus = rs.getString ( "TestStatus" );
                TestRailStatus testRailStatus = null;
                if (testStatus.equals ( "1" ))
                    testCaseResultOutput.setPassedTestCases ( passedCounter++ );
                else
                    testCaseResultOutput.setFailedTestCases ( failedCounter++ );
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException ( "Exception occurred GetStressTestCasesOutput: " + ex.getMessage () );
        }
        return  testCaseResultOutput;

    }

    public ResultSet GetHydraJobStatus(int jobId)
    {


        ResultSet rs = null;
        try {

            String procedureName = "usp_GetHydraJobStatus";
            String[] parameters = {String.valueOf ( jobId )};
            rs = QAAutomationUtils.SQLDatabase.ExecuteProcedure ( AutomationDatabase, procedureName, parameters );



        }
        catch (Exception ex)
        {
            throw new RuntimeException ( "Exception occurred GetStressTestCasesOutput: " + ex.getMessage () );
        }
        return  rs;

    }
}




