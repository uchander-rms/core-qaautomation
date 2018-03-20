package QAAutomationCore;

import QAAutomationConfiguration.RBUserConfig;
import QAAutomationConfiguration.TestRail;
import QAAutomationManagers.*;
import QAAutomationObjects.*;

import QAAutomationUtils.*;
import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;
import org.apache.commons.lang.NullArgumentException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import QAAutomationTestInterface.ITestEngine;
import QAAutomationTestEngineFactory.TestEngineFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static java.lang.System.in;

public class ExecutionManager
{
    private static final Logger logger = Logger.getLogger ( ExecutionManager.class );

    //region Public methods

    public void RunWorkflows(String environmentName, String automationDb, String workflowList, boolean isLicenseWF)
    {
        try
        {

                //region Validate database connection


            logger.debug("Verifying database connection...");
            System.out.println("Verifying database connection...");

            //SQLDatabase.OpenConnection ( automationDb, RBUserConfig.GetInstance ().getConnection ().getJdbc_driver ());

                //endregion

                //region Generate GUID and Timestamp

            String executionGuid = UUID.randomUUID ().toString ();
            Date dt = new Date (  );

            logger.debug("Execution GUID: %s" + executionGuid);
            System.out.println("Execution GUID: " + executionGuid);

                //endregion

                //region Verify environment details exist

            logger.debug("Loading environment details...");
            System.out.println("Loading environment details...");

            EnvironmentManager environmentManager = new EnvironmentManager();
            environmentManager.Initialize(automationDb);

            Environment environment = environmentManager.GetEnvironmentByName(environmentName);
            environment.LicenseWF = isLicenseWF;

            if (environment.getName () == null)
            {
                logger.error("environment doesn't exist: " + environmentName);
                throw new RuntimeException ("environment "  + environmentName + "  doesn't exist." );
            }

                //endregion

                //region Load test case types

            logger.debug("Loading test case types...");
            System.out.println("Loading test case types...");

            TestCaseTypeManager testCaseTypeManager = new TestCaseTypeManager();
            testCaseTypeManager.Initialize(automationDb);

            HashMap<String, TestCaseType> testCaseTypes = testCaseTypeManager.GetTypes();
            if (testCaseTypes == null || testCaseTypes.size () == 0)
            {
                logger.error("Test case types don't exist in database.");
                throw new RuntimeException ("Test case types don't exist in database.: ExecutionManager");
            }

            //endregion

            //region Load workflows

            logger.debug("Loading workflow details...");
            System.out.println("Loading workflow details...");

            WorkflowManager workflowManager = new WorkflowManager();
            workflowManager.Initialize(automationDb);

            HashMap<Integer, Workflow> workflowHashMap = workflowManager.GetWorkflows(GetWorkflowList(workflowList), false);
            if (workflowHashMap == null || workflowHashMap.size () == 0)
            {
                logger.error("No workflows to run : Execution Manager.");
                throw new RuntimeException ("No workflows to run: Execution Manager ");
            }

            //endregion

            //region Retrieve and run TestCases

            HashMap<Integer, Workflow> processedWorkflowHashMap = new HashMap<Integer, Workflow> ();


            for (Workflow workflow:workflowHashMap.values ()
                 ) {

                    logger.debug("START: WORKFLOW: " + workflow.Name+ "  - Running..." );
                    System.out.println("START: WORKFLOW: " + workflow.Name + " - Running...");

                    if (workflow.IsControl == false)
                    {
                        logger.debug("Workflow is disabled to run.");
                        System.out.println("Workflow is disabled to run.");
                        continue;
                    }

                    logger.debug("Loading test cases for Workflow: " + workflow.Name + "  ..." );
                    System.out.println("Loading test cases for Workflow: " + workflow.Name +" ..." );

                    //region Load test cases

                    TestCaseManager testCaseManager = new TestCaseManager();
                    testCaseManager.Initialize(automationDb);

                    List<WorkflowTestCase> workflowTestCases = testCaseManager.GetTestCases(workflow, testCaseTypes, executionGuid, environment, workflow.CheckLicense);
                    if (workflowTestCases == null || workflowTestCases.size () == 0)
                    {
                        logger.debug("No test cases to run.");
                        System.out.println("No test cases to run.");
                        continue;
                    }

                    //endregion

                    //region Execute test cases in sequence order


                    RunTestCases(environment, automationDb, workflowTestCases);

                    logger.debug("END: WORKFLOW: " + workflow.Name + " - Finished.");
                    System.out.println("END: WORKFLOW: " + workflow.Name + " - Finished.");

                    processedWorkflowHashMap.put(workflow.Id, workflow);

                    //endregion
        }

                //endregion

                //region Send email

        logger.debug("START: Sending email...");
        System.out.println("START: Sending email...");

        Date executionTime = new Date();
        SendEmail(environment, automationDb, processedWorkflowHashMap, executionTime, executionGuid);

        logger.debug("END: Sent email.");
        System.out.println("END: Sent email.");

                //endregion
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage ());
            System.out.println(ex.getMessage ());
            throw new RuntimeException ( "Execution Manager: " + ex.getMessage () );
        }
    }


    public void RunWorkflows(Environment environment, String automationDb, BlockingQueue<Workflow> workflowQueue, Workflow workflow, HashMap<String, TestCaseType> testCaseTypes, String executionGuid, boolean repeatWF)
    {

        if (workflowQueue == null)
        {
            logger.debug ( "Workflow processing queue empty");
            throw new NullArgumentException ("workflowQueue");
        }

        try
        {
            //region Retrieve and run TestCases

           // Workflow workflow = wf;
            //workflow = workflowQueue.peek ();

            // logger.debug(String.format ( "Thread Id: %s. Processing workflow id: %s", Thread.currentThread ().getId (), workflow.Id));
             //   System.out.println(String.format  ("Thread Id: %s. Processing workflow id: %s", Thread.currentThread ().getId (), workflow.Id));

                if (workflow.IsControl == false)
                {
                    logger.debug ("Workflow is disabled to run.");
                    System.out.println ("Workflow is disabled to run.");
                    //continue;
                }

                logger.debug ( "Loading test cases for Workflow: {0} ..." + workflow.Name);
                System.out.println ( "Loading test cases for Workflow: {0} ..." + workflow.Name);

                //region Load test cases

                TestCaseManager testCaseManager = new TestCaseManager();
                testCaseManager.Initialize(automationDb);

                List<WorkflowTestCase> workflowTestCases = testCaseManager.GetTestCases(workflow, testCaseTypes, executionGuid, environment, workflow.CheckLicense);
                if (workflowTestCases == null || workflowTestCases.size () == 0)
                {
                    logger.debug ("No test cases to run.");
                    System.out.println ("No test cases to run.");
                }

                //endregion

                 //region Execute test cases in sequence order

                RunTestCases(environment, automationDb, workflowTestCases);

                logger.debug (String.format ("Thread Id: %s. Processed workflow id: %s", Thread.currentThread ().getId (), workflow.Id));
                System.out.println (String.format ( "Thread Id: %s. Processed workflow id: %s", Thread.currentThread ().getId (), workflow.Id));
                //workflowQueue.remove (  );
                    //endregion

                //endregion
        }
        catch (Exception ex)
        {
            logger.error ( ex.getMessage (), ex);
            System.out.println ( ex.getMessage () );
            throw ex;
        }
    }

    public void SendEmail(Environment environment, String automationDb, HashMap<Integer, Workflow> workflows, Date executionTime, String executionGuid) throws IOException, APIException {

        String HostName = getComputerName ();
        logger.debug(String.format ("Preparing email for environment.Name:%s, environment.Description:%s, System.Environment.HostName:%s, System.Environment.UserName:%s, executionTime:%s, executionGuid:%s", environment.Name, environment.Description, HostName, System.getProperty("user.name"), executionTime, executionGuid));
        String tableStart = "<table border=\"1\" cellspacing=\"0\" width=\"1000\" style=\"font: 12px Arial, Helvetica, sans-serif;\">";

        int passedTestCases = 0;
        int failedTestCases = 0;
        int skippedTestCases = 0;

        TestCaseResultManager testCaseResultManager = new TestCaseResultManager ();
        testCaseResultManager.Initialize(automationDb);

        StringBuilder tableRows = new StringBuilder();
        for (Workflow workflow : workflows.values ()
             ) {
                if (workflow.IsControl == false)
                    continue;

                logger.debug(String.format ("Preparing email for workflowId:%s, workflowName:%s, workflowDesc:%s", workflow.Id, workflow.Name, workflow.Description));

                String workflowIdRow = String.format ("<tr><td align=\"center\" colspan=\"4\" bgcolor=//3399FF><b>Workflow Id: %s</b></td></tr>", workflow.Id);
                String workflowNameRow = String.format ("<tr><td colspan=\"4\">Workflow Name: %s</td></tr>", workflow.Name);
                String workflowDescRow = String.format ("<tr><td colspan=\"4\">Workflow Description: %s</td></tr>", workflow.Description);

                tableRows.append (workflowIdRow).append (workflowNameRow).append (workflowDescRow);

                List<TestCaseResult> testCaseResults = testCaseResultManager.GetTestCases(environment.Id, workflow.Id, executionGuid);
                if (testCaseResults != null && testCaseResults.size () > 0)
                {

                    String testCaseHeader = "<tr><td><b>testCase Number</b></td><td><b>testCase Description</b></td><td><b>testCase Status</b></td><td><b>testCase Details</b></td></tr>";
                    tableRows.append (testCaseHeader);

                    for (TestCaseResult testCaseResult: testCaseResults)
                    {
                        logger.debug(String.format ("Adding details for Test cases with TestCaseNumber:%s, TestCaseDescription:%s, TestCaseDetail:%s", testCaseResult.TestCaseNumber, testCaseResult.TestCaseDescription, testCaseResult.TestCaseDetail));

                        String tableRow ="";
                        if (testCaseResult.TestCaseStatus == TestRailStatus.PASSED)
                        {
                            logger.debug(String.format ("Test case passed: %s", testCaseResult.TestCaseNumber));
                            tableRow =
                                    String.format("<tr><td>%s</td><td>%s</td><td bgcolor=lightgreen>%s</td><td>%s</td></tr>", testCaseResult.TestCaseNumber, testCaseResult.TestCaseDescription, "PASSED", testCaseResult.TestCaseDetail);
                            passedTestCases++;
                        }
                        else
                        {
                            logger.debug(String.format ("Test case failed: %s", testCaseResult.TestCaseNumber));
                            tableRow =
                                    String.format("<tr><td>%s</td><td>%s</td><td bgcolor=red>%s</td><td>%s</td></tr>", testCaseResult.TestCaseNumber, testCaseResult.TestCaseDescription, "FAILED", testCaseResult.TestCaseDetail);
                            failedTestCases++;
                        }
                        if(tableRow!=null && tableRow.trim  ().length ()!=0)
                            tableRows.append (tableRow);
                    }

                    if( RBUserConfig.GetInstance ().getTestRail ().getTrUpdateStatus ().toString ().toUpperCase ().equals ( "YES" ))
                        TestRailClient.UpdateResults (RBUserConfig.GetInstance ().getTestRail ().getTrUser ().toString ().trim (),
                                RBUserConfig.GetInstance ().getTestRail ().getTrPass ().toString ().trim (),
                                RBUserConfig.GetInstance ().getTestRail ().getTrRunId ().toString ().trim () ,
                                RBUserConfig.GetInstance ().getTestRail ().getTrURL ().trim (),
                                testCaseResults );

                }
                else
                {
                    logger.debug("No test case run.");
                    String tableRow = "<tr><td colspan=\"4\"><b>Warning: </b>No test case run.</td></tr>";
                    tableRows.append (tableRow);
                }
            }

            String tableEnd = "</ table >";

            String htmlStart =
                    String.format("<html><body style=\"font: 12px Arial, Helvetica, sans-serif; \"><p><b>environment:</b> %s -> %s</p><p><b>Machine:</b> %s</p><p><b>Executed By:</b> %s</p><p><b>Executed On:</b> %s</p><p><b>Execution GUID:</b> %s</p>", environment.Name, environment.Description, HostName, System.getProperty("user.name"), executionTime, executionGuid);
            String htmlMiddle = tableStart + tableRows + tableEnd;
            String htmlEnd = "<body></html>";

            logger.debug(String.format ("RiskLink/RiskBrowser Test Results for environment:%s - %s. Total:%d, Passed:%d, Failed:%d, Skipped:%d.", environment.Name, environment.Description, passedTestCases + failedTestCases + skippedTestCases, passedTestCases, failedTestCases, skippedTestCases));

            String emailSubject =
                    String.format("RiskLink/RiskBrowser Test Results for environment:%s - %s. Total:%s, Passed:%s, Failed:%s, Skipped:%s.", environment.Name, environment.Description, passedTestCases + failedTestCases + skippedTestCases, passedTestCases, failedTestCases, skippedTestCases);
            StringBuilder emailBody = new StringBuilder();
            emailBody.append(htmlStart).append(htmlMiddle).append(htmlEnd);


            if (environment.Name.toUpperCase ().contains ( "-31")) {
                PostSlackMessage(environment, automationDb, workflows, executionTime, executionGuid, emailBody.toString());
            }

            new Email().SendEmail( "EMAILSERVER",
                    80,
                    RBUserConfig.GetInstance ().getEmail ().getEmailTo ().toString ().trim (),
                    "",
                    false,
                    emailSubject,
                    emailBody.toString(),
                    ""
                    );
    }
    //endregion

    //region stress status email
    public void SendStressStatusEmail(Environment environment, ConcurrentHashMap<Integer, Workflow> workflows, String automationDB, String executionGuid) {
        logger.debug ( String.format ( "Preparing email for environment.Name:%s, environment.Description:%s, System.Environment.HostName:%s, System.Environment.UserName:%s,  executionGuid:%s", environment.Name, environment.Description, getComputerName (), System.getProperty ( "user.name" ), executionGuid ) );

        try {
            String tableStart = "<table border=\"1\" cellspacing=\"0\" width=\"1000\" style=\"font: 12px Arial, Helvetica, sans-serif;\">";

            int passedTestCases = 0;
            int failedTestCases = 0;
            int skippedTestCases = 0;

            TestCaseResultManager testCaseResultManager = new TestCaseResultManager ();
            testCaseResultManager.Initialize ( automationDB );

            StringBuilder tableRows = new StringBuilder ();

            String testCaseHeader = "<caption><b>Workflow Job Submission Status</b></caption><tr><td><b>Workflow Name</b></td><td><b>Workflow Description</b></td><td><b>Stress interval (in minutes)</b></td><td><b>TotalPassedSubmission</b></td><td><b>TotalFailedSubmissions</b></td></tr>";
            tableRows.append ( testCaseHeader );

            for (Workflow workflow : workflows.values ()
                    ) {
                logger.debug ( String.format ( "Preparing email for workflowId:%s, workflowName:%s, workflowDesc:%s", workflow.Id, workflow.Name, workflow.Description ) );


                TestCaseResultOutput testCaseResultOutputs = testCaseResultManager.GetStressTestCasesOutput ( environment.Id, workflow.Id, executionGuid );

                if (testCaseResultOutputs != null && testCaseResultOutputs.getPassedTestCases () > 0 || testCaseResultOutputs.getFailedTestCases () > 0) {
                    String tableRow;
                    tableRow =
                            String.format ( "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", workflow.Name, workflow.Description, workflow.TimeInterval,
                                    testCaseResultOutputs.getPassedTestCases (), testCaseResultOutputs.getFailedTestCases () );
                    tableRows.append ( tableRow );

                }
            }

            String tableEnd = "</ table >";

            //region get RMS hydra job database status & create html table
            ResultSet rs = testCaseResultManager.GetHydraJobStatus ( 0 );

            String hydraTableStart = "<table border=\"1\" cellspacing=\"0\" width=\"1000\" style=\"font: 12px Arial, Helvetica, sans-serif;;margin-top: 50px\">";
            StringBuilder hydraTableRows = new StringBuilder ();
            String hydraTableHeader = "<caption><b>RMS Hydra Job Status</b></caption><tr><td><b>JobStatus</b></td><td><b>TotalCount</b></td><td><b>JobType</b></td></tr>";
            hydraTableRows.append ( hydraTableHeader );
            while (rs.next ()) {

                String hydraTableRow = String.format ( "<tr><td>%s</td><td>%s</td><td>%s</td></tr>", rs.getString ( "JobStatus" ), rs.getString ( "JobCount" ), rs.getString ( "JobType" ) );
                hydraTableRows.append ( hydraTableRow );
            }

            //endregion

            String htmlStart =
                    String.format ( "<html><body style=\"font: 12px Arial, Helvetica, sans-serif; \"><p><b>environment:</b> %s -> %s</p><p><b>Machine:</b> %s</p><p><b>Executed By:</b> %s</p><p><b>Execution GUID:</b> %s</p>", environment.Name, environment.Description, getComputerName (), System.getProperty ( "user.name" ), executionGuid );
            String htmlMiddleTestStatus = tableStart + tableRows + tableEnd;
            String htmlMiddleHydraStatus = hydraTableStart + hydraTableRows + tableEnd;

            String htmlEnd = "<body></html>";

            logger.debug ( String.format ( "RiskBrowser Stress Test Results for environment:%s - %s. Total:%s, Passed:%s, Failed:%s, Skipped:%s.", environment.Name, environment.Description, passedTestCases + failedTestCases + skippedTestCases, passedTestCases, failedTestCases, skippedTestCases ) );
            String emailSubject =
                    String.format ( "RiskBrowser Stress Test Results for environment:%s ", environment.Name );

            StringBuilder emailBody = new StringBuilder ();
            emailBody.append ( htmlStart ).append ( htmlMiddleTestStatus ).append ( htmlMiddleHydraStatus ).append ( htmlEnd );

            System.out.println ( emailBody );

            new Email().SendEmail( "EMAILSERVER",
                    80,
                    RBUserConfig.GetInstance ().getEmail ().getEmailTo ().toString ().trim (),
                    "",
                    false,
                    emailSubject,
                    emailBody.toString(),
                    ""
            );


        } catch (Exception ex) {
            throw new RuntimeException ( "Exception occurred SendStressEmailStatus: " + ex.getMessage () );
        }
    }
    //endregion

    //region Private methods
    private void RunTestCases(final QAAutomationObjects.Environment environment, final String automationDb, List<WorkflowTestCase> workflowTasks)
    {
        Iterable<List<WorkflowTestCase>> workflowTestCasesList = GetSortedList(workflowTasks);

        for (List<WorkflowTestCase> workflowTestCases: workflowTestCasesList
             )
        {
            final String threadGuid = UUID.randomUUID ().toString ();

            final boolean isDependent = (workflowTestCases.size () > 1);

            if (isDependent)
            {
                CleanupTestTables(automationDb, environment.Id, workflowTestCases.get ( 0 ).Workflow.Id, threadGuid);
            }

            for (final WorkflowTestCase workflowTestCase: workflowTestCases
                 ) {
                    logger.debug(String.format ("START: Workflow-%s,testCase-%s :: Starting...", workflowTestCase.Workflow.Name, workflowTestCase.TestCaseNumber));
                    System.out.println(String.format ("START: Workflow-%s,testCase-%s :: Starting...", workflowTestCase.Workflow.Name, workflowTestCase.TestCaseNumber));

                    try{
                        if (workflowTestCase.Workflow.IsControl == false)
                        {
                            logger.debug(String.format ("WORKFLOW:%s - SKIPPED", workflowTestCase.Workflow.Name));
                            System.out.println(String.format (ConsoleColors.YELLOW + "WORKFLOW:%s - SKIPPED", workflowTestCase.Workflow.Name + ConsoleColors.RESET));

                            continue;
                        }

                        if (workflowTestCase.IsControl == false)
                        {
                            logger.debug(String.format ( "TESTCASE:%s - SKIPPED", workflowTestCase.TestCaseNumber));
                            System.out.println(String.format (ConsoleColors.YELLOW + "TESTCASE:%s - SKIPPED", workflowTestCase.TestCaseNumber + ConsoleColors.RESET));
                            continue;
                        }

                        final TestCase task = new TestCase() {
                            {
                                TestCaseNumber = workflowTestCase.TestCaseNumber;
                                TestCaseDescription = workflowTestCase.TestCaseDescription;
                                TestCaseType = workflowTestCase.TestCaseType.Type;
                                EnvironmentId = environment.Id;
                                EnvironmentName = environment.Name;
                                EnvironmentType = environment.Description;
                                AutomationDB = automationDb;
                                WorkflowId = workflowTestCase.Workflow.Id;
                                WorkflowName = workflowTestCase.Workflow.Name;
                                WorkflowOwner = workflowTestCase.Workflow.Owner;
                                ExecutionGuid = workflowTestCase.ExecutionGuid;
                                Iteration = 1;
                                ExecutedBy = System.getProperty ( "user.name" );
                                IsDependent = isDependent;
                                DependentTestCase = workflowTestCase.DependentTestCase;
                                IsDetailed = workflowTestCase.Workflow.IsDetailed;
                                WorkFlowType = workflowTestCase.Workflow.WorkFlowType;
                                ThreadGuid = threadGuid;
                                ClusterName = environment.ClusterName;
                            }
                        };

                        if (RunTestCase(workflowTestCase.TestCaseType.Module, task) == false)
                        {
                            break;
                        }
                    }
                    finally
                    {
                        logger.debug(String.format ("END: Workflow-%s,testCase-{1} :: Finished.", workflowTestCase.Workflow.Name, workflowTestCase.TestCaseNumber));
                        System.out.println(String.format ("END: Workflow-%s,testCase-%s :: Finished.", workflowTestCase.Workflow.Name, workflowTestCase.TestCaseNumber));
                    }
            }
        }
    }

    private  Iterable<List<WorkflowTestCase>> GetSortedList(List<WorkflowTestCase> workflowTasks)
    {
        List<List<WorkflowTestCase>> workflowTestCaseLists = new ArrayList<List<WorkflowTestCase>> ();

        try {
            List<WorkflowTestCase> independentTestCases = workflowTasks.stream ().filter ( (s) -> s.DependentTestCase==null ).collect ( Collectors.toList () );

            for (WorkflowTestCase workflowTestCase : independentTestCases) {
                List<WorkflowTestCase> workflowTestCases = new ArrayList<WorkflowTestCase> ();

                AddDependentTestCase ( workflowTestCase, workflowTasks, workflowTestCases );

                workflowTestCaseLists.add ( workflowTestCases );
            }
        }
        catch (Exception e)
        {
            System.out.println ( "Exception Occurred: GetSortedList:" + e.getMessage ());
            throw  new RuntimeException ( e.getMessage () );
        }

        return workflowTestCaseLists;
    }

    private void AddDependentTestCase(final WorkflowTestCase workflowTestCase, List<WorkflowTestCase> workflowTasks, List<WorkflowTestCase> workflowTestCases)
    {
        workflowTestCases.add(workflowTestCase);

        /*
        WorkflowTestCase dependentTestCase = workflowTasks.stream ().filter ( s-> s.DependentTestCase==workflowTestCase.TestCaseNumber && s.Id != workflowTestCase.Id ).findFirst ().get ();

        if (dependentTestCase != null)
        {
            AddDependentTestCase(dependentTestCase, workflowTasks, workflowTestCases);
        }
        */
    }

    private boolean RunTestCase(String module, TestCase testCase)
    {
        try
        {
            //ITestEngine testEngine;

            ITestEngine testEngine = TestEngineFactory.GetEngine(module);

            if (testEngine == null)
            {
                logger.error(String.format ( "Failed to get engine for module: " + module, new Exception("Failed to get engine for module: " + module)));
                throw new Exception("Failed to get engine for module: " + module);
            }

            if (!testEngine.GetInput(testCase))
            {
                logger.error(String.format ( "GetInputs() failed. " + testCase.TestCaseDetails, new Exception("GetInputs() failed. " + testCase.TestCaseDetails)));
                throw new Exception("GetInputs() failed. " + testCase.TestCaseDetails);
            }

            if (!testEngine.Run(testCase))
            {
                logger.error(String.format ( "Run() failed. " + testCase.TestCaseDetails, new Exception("Run() failed. " + testCase.TestCaseDetails)));
                throw new Exception("Run() failed. " + testCase.TestCaseDetails);
            }

            if (testCase.IsDetailed)
            {
                if (!testEngine.Validate(testCase))
                {
                    logger.error(String.format ( "Validate() failed. " + testCase.TestCaseDetails, new Exception("Validate() failed. " + testCase.TestCaseDetails)));
                    throw new Exception("Validate() failed. " + testCase.TestCaseDetails);
                }
            }
            OnSuccess(testCase);


            logger.debug(String.format (ConsoleColors.GREEN + "TESTCASE:%s - PASSED", testCase.TestCaseNumber ));
           // logger.debug ( ConsoleColors.RESET);
            System.out.println(String.format (ConsoleColors.GREEN + "TESTCASE:%s - PASSED", testCase.TestCaseNumber));
            System.out.println (  ConsoleColors.RESET);


            return true;
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage ());
            System.out.println(ex.getStackTrace ());

            logger.error(ex.getMessage (), ex);
            OnError(testCase, ex.getMessage ());


            logger.debug(String.format ( ConsoleColors.RED + "TESTCASE:%s - FAILED", testCase.TestCaseNumber ));
            //logger.debug( ConsoleColors.RESET);
            System.out.println(String.format ( ConsoleColors.RED + "TESTCASE:%s - FAILED", testCase.TestCaseNumber));
            System.out.println (  ConsoleColors.RESET);

        }

        return false;
    }

    private static String GetWorkflowList(String workflowList)
    {
        String []  wfList = workflowList.split ( "," );
        String wfLists="";
        for (String wf: wfList
             ) {
            wfLists = wfLists + "'" + wf.trim () + "',";

        }

            if(wfLists.endsWith ( "," ))
                return wfLists.substring ( 0, wfLists.length ()-1 );
            else
                return wfLists;
    }

    private static void OnSuccess(final TestCase testCase)
    {
        try
        {
            final TestCaseResultManager testCaseResultManager = new TestCaseResultManager();
            testCaseResultManager.Initialize(testCase.AutomationDB);

            final TestCaseResult testCaseResult = new TestCaseResult(){
                {
                    TestCaseNumber = testCase.TestCaseNumber;
                    TestCaseDescription = testCase.TestCaseDescription;
                    TestCaseType = testCase.TestCaseType;
                    WorkflowId = testCase.WorkflowId;
                    WorkflowName = testCase.WorkflowName;
                    WorkflowOwner = testCase.WorkflowOwner;
                    TestCaseStatus = TestRailStatus.PASSED;
                    EnvironmentId = testCase.EnvironmentId;
                    EnvironmentName = testCase.EnvironmentName;
                    TestCaseDetail = testCase.TestCaseDetails == null ? "" : testCase.TestCaseDetails;
                    ExecutionGuid = testCase.ExecutionGuid;
                    ExecutedBy = testCase.ExecutedBy;
                    Iteration = testCase.Iteration;
                    JobId = testCase.JobId;
                    JobStatusCode = testCase.JobStatusCode;
                    StartTime = testCase.StartTime;
                    EndTime = testCase.EndTime;
                }
            };

            testCaseResultManager.UpdateStatus(testCaseResult);
        }
        catch (Exception ex)
        {
            logger.error( ex.getMessage (), ex);
            System.out.println(ex.getMessage ());
        }
    }

    private void OnError(final TestCase testCase, final String error)
    {
        try
        {
            final TestCaseResultManager testCaseResultManager = new TestCaseResultManager();
            testCaseResultManager.Initialize(testCase.AutomationDB);

            if (!(testCase.JobStatusCode==null || testCase.JobStatusCode.trim ()==""))
                if (testCase.JobStatusCode.length () > 50)
                    testCase.JobStatusCode = testCase.JobStatusCode.substring (0, Math.min (testCase.JobStatusCode.length (), 49));

            final TestCaseResult testCaseResult = new TestCaseResult(){
                {
                    TestCaseNumber = testCase.TestCaseNumber;
                    TestCaseDescription = testCase.TestCaseDescription;
                    TestCaseType = testCase.TestCaseType;
                    WorkflowId = testCase.WorkflowId;
                    WorkflowName = testCase.WorkflowName;
                    WorkflowOwner = testCase.WorkflowOwner;
                    TestCaseStatus = TestRailStatus.FAILED;
                    EnvironmentId = testCase.EnvironmentId;
                    EnvironmentName = testCase.EnvironmentName;
                    TestCaseDetail = error;
                    ExecutionGuid = testCase.ExecutionGuid;
                    ExecutedBy = testCase.ExecutedBy;
                    Iteration = testCase.Iteration;
                    JobId = testCase.JobId;
                    JobStatusCode = testCase.JobStatusCode;
                    StartTime = testCase.StartTime;
                    EndTime = testCase.EndTime;
                }
            };


            testCaseResultManager.UpdateStatus(testCaseResult);
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage (), ex);
            System.out.println(ex.getMessage ());

        }
    }

    private void CleanupTestTables(String automationDb, int environmentId, int workflowId, String threadGuid)
    {
        TestCaseOutputManager outputManager = new TestCaseOutputManager();
        outputManager.Initialize(automationDb);

        outputManager.Delete(environmentId, workflowId, threadGuid);
    }

    private String getComputerName()
    {
        Map<String, String> env = System.getenv();
        if (env.containsKey("COMPUTERNAME"))
            return env.get("COMPUTERNAME");
        else if (env.containsKey("HOSTNAME"))
            return env.get("HOSTNAME");
        else
            return "Unknown Computer";
    }

    public void PostSlackMessage(Environment environment, String automationDb, HashMap<Integer, Workflow> workflows, Date executionTime, String executionGuid, String emailBody)
    {

        String message;
        JSONArray workflowsList = new JSONArray ();
        JSONObject targetEnvDetails = new JSONObject ();

        targetEnvDetails.put ( "*Detailed Test Report for: *", environment.ClusterName );
        //targetEnvDetails.Add("```");
        targetEnvDetails.put ( "*EnvironmentName*", environment.ClusterName );
        targetEnvDetails.put ( "*EnvironmentType*", environment.EnvironmentType );
        targetEnvDetails.put ( "*EnvironmentDescription*", environment.Description );
        targetEnvDetails.put ( "*ExecutionTime*", executionTime );
        targetEnvDetails.put ( "*ExecutionGUID*", executionGuid );

        TestCaseResultManager testCaseResultManager = new TestCaseResultManager ();
        testCaseResultManager.Initialize ( automationDb );

        StringBuilder tableRows = new StringBuilder ();

        for (Map.Entry<Integer, Workflow> entry : workflows.entrySet ()) {
            Workflow workflow = entry.getValue ();
            int passedTestCases = 0;
            int failedTestCases = 0;
            JSONObject jsonWf = new JSONObject ();

            if (workflow.IsControl == false) {
                continue;
            }

            logger.debug ( String.format ( "Preparing json for workflowId:%s, workflowName:%s, workflowDesc:%s", workflow.Id, workflow.Name, workflow.Description ) );

            List<TestCaseResult> testCaseResults = testCaseResultManager.GetTestCases ( environment.Id, workflow.Id, executionGuid );
            if (testCaseResults != null && testCaseResults.size () > 0) {

                for (TestCaseResult testCaseResult : testCaseResults) {
                    logger.debug ( "Adding test summary details" );

                    if (testCaseResult.TestCaseStatus == TestRailStatus.PASSED) {
                        logger.debug ( String.format ( "Test case passed: %s", testCaseResult.TestCaseNumber ) );
                        passedTestCases++;
                    } else {
                        logger.debug ( String.format ( "Test case failed: %s", testCaseResult.TestCaseNumber ) );
                        failedTestCases++;
                    }

                }
            } else {
                logger.debug ( "No test case run." );
            }

            jsonWf.put ( "`WorkflowId`", workflow.Id );
            jsonWf.put ( "`WorkflowName`", workflow.Name );
            jsonWf.put ( "`WorkflowDescription`", workflow.Description );
            jsonWf.put ( "`TotalTestCasesExecuted`", passedTestCases + failedTestCases );
            jsonWf.put ( "`TotalTestCasesPassed`", passedTestCases );
            jsonWf.put ( "`TotalTestCasesFailed`", failedTestCases );
            jsonWf.put ( "`WorkflowConfidence`", ((int) (0.5f + ((100f * passedTestCases) / (failedTestCases + passedTestCases)))) + "%" );
            workflowsList.add ( jsonWf );


        }
        targetEnvDetails.put ( "`WorkflowTestDetails`", workflowsList );
        //targetEnvDetails.Add("```");


        String testResultPath = "/Users/uchander/Documents/" + environment.ClusterName + executionGuid + ".html";

        try {
            try {
                Files.write ( Paths.get ( testResultPath ), emailBody.getBytes (), StandardOpenOption.WRITE );
            } catch (IOException ex) {
                logger.debug ( ex.getMessage () );
            }
            targetEnvDetails.put ( "`*DetailedReportPath*`", testResultPath.toString () );

            //JSONObject jsonObject = new JSONObject (  );
            //jsonObject.put ( "data", targetEnvDetails );
           // jsonObject.put ( "channel", "#rlrb_automationreport" );
            //jsonObject.put ( "username", "CI" );



            String urlWithAccessToken = "https://hooks.slack.com/services/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";


            SlackApi api = new SlackApi(urlWithAccessToken);
            api.call(new SlackMessage ("#rlrb_automationreport", "CI", targetEnvDetails.toJSONString ()));

            /*
            Client client = JerseyClientBuilder.createClient ();

            Response response = client.target ( urlWithAccessToken )
                    .request ( MediaType.APPLICATION_JSON )
                    .post ( Entity.entity (jsonObject, MediaType.APPLICATION_JSON) );

            System.out.println ( response.getStatus () );
            String resp = response.readEntity (String.class  );

            /*
            System.out.println ( jsonObject );
            //System.out.println ( message );
            String urlWithAccessToken = "https://hooks.slack.com/services/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";


            HttpClient httpclient = HttpClients.createDefault ();

            HttpPost httppost = new HttpPost ( urlWithAccessToken );

            httppost.addHeader ( "Content-type", "application/json" );
            StringEntity params = new StringEntity ( "{\"text\" : \"" + jsonObject.toJSONString () + "\"}", "UTF-8" );


            params.setContentType ( "application/json" );
            httppost.setEntity ( params );

            HttpResponse response = httpclient.execute ( httppost );
            HttpEntity entity = response.getEntity ();
            */

        }
        catch (Exception e) {
            System.out.println ( e.getMessage () );
            logger.debug ( "Exception occurred Execution Manager: Post Slack Message:" + e.getMessage () );
        }

    }
}

