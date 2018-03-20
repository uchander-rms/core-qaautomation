package QAAutomationRunner;

import java.util.*;
import java.util.concurrent.*;

import QAAutomationConfiguration.RBUserConfig;
import  QAAutomationCore.ExecutionManager;
import QAAutomationManagers.EnvironmentManager;
import QAAutomationManagers.TestCaseTypeManager;
import QAAutomationManagers.WorkflowManager;
import QAAutomationObjects.Environment;
import QAAutomationObjects.TestCaseType;
import QAAutomationObjects.Workflow;
import org.apache.log4j.Logger;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;


public class StressTest implements Runnable {

    private static final Logger logger = Logger.getLogger ( StressTest.class );

    static String environment = "";
    static String automationDb = "";
    static String workflowList= "";
    static boolean isLicenseWF= false;
    static boolean stressEnabled= false;
    static boolean repeatWFn= false;
    static boolean keepRunning = true;
    static ConcurrentHashMap<Integer, Workflow> workFlowsLists = new ConcurrentHashMap<> (  );
    static private BlockingQueue<Workflow> workflowQueue = new LinkedBlockingDeque<> () ;
    public static void StartStressTest(String environmentName, String autoDb, String wfList, boolean isLicWF, boolean strWF, boolean processSame)
    {
        environment = environmentName;

        automationDb = autoDb;
        workflowList = wfList;
        isLicenseWF = isLicWF;
        stressEnabled = strWF;
        repeatWFn = processSame;


        //region get workflows list

        System.out.println ("Loading workflow details...");

        WorkflowManager wfManager = new WorkflowManager ();
        wfManager.Initialize(automationDb);

        workFlowsLists = wfManager.GetStressWorkflows(GetWorkflowList(workflowList), autoDb);

        System.out.println ("Workflow details added ");

        String executionGuid = UUID.randomUUID ().toString ();
        Date dt = new Date (  );

        //endregion

        //region get environment details
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

        Map<Runnable, Future<?>> cancellableFutures = new HashMap();
        ScheduledFuture<?> stressHandler = null;
        //endregion

        //region run workflows in stress mode
        if(workFlowsLists.size ()>0)
        {

            Set keys = workFlowsLists.keySet ();
            for (Object key: keys
                 ) {

                Workflow wf = workFlowsLists.get ( key );
                try {
                    if(workflowQueue.size ()>0)
                    {
                        workflowQueue.remove ();
                        workflowQueue.add ( wf );
                    }

                    else
                        workflowQueue.add ( wf );
                }
                catch (Exception e){
                    System.out.println ( e.getMessage () );

                }

                ScheduledExecutorService scheduledExecutorService =
                        Executors.newScheduledThreadPool ( 5 );

                //region runnable task
                Runnable task = () -> {
                    try {
                        TimeUnit.SECONDS.sleep ( 5 );
                        new ExecutionManager ().RunWorkflows ( environment, autoDb, workflowQueue, wf, testCaseTypes,executionGuid,false );
                        //System.out.println ( "Hello : Thread Id" + Thread.currentThread ().getId () );

                    } catch (InterruptedException e) {
                        System.err.println ( "task interrupted by user:" );
                        System.out.println ( "Task interrupted by user:" );
                    }
                };
                //endregion

                //region task scheduler
                stressHandler =
                        scheduledExecutorService.scheduleAtFixedRate ( task, 0, wf.getTimeInterval (), TimeUnit.MINUTES );

                cancellableFutures.put ( task ,stressHandler );

                //endregion

                //region cancel task
                if(!keepRunning) {
                    stressHandler.cancel ( true );
                    cancellableFutures.get(task).cancel(true);
                }

                //endregion
            }

        }
        //endregion

        //region send stress email in regular interval
        ScheduledExecutorService scheduledMailExecutorService =
                Executors.newScheduledThreadPool ( 1 );

        Runnable task1 = () -> {
            try {
                TimeUnit.SECONDS.sleep ( 5 );
                new ExecutionManager ().SendStressStatusEmail ( environment, workFlowsLists, autoDb,  executionGuid );


            } catch (InterruptedException e) {
                System.out.println ( "Task interrupted by user:" );
            }
        };

        System.out.println ( RBUserConfig.GetInstance ().getEmail ().getEmailInterval () );
        //region task scheduler
        stressHandler =
                scheduledMailExecutorService.scheduleAtFixedRate ( task1, RBUserConfig.GetInstance ().getEmail ().getEmailInterval (),
                        RBUserConfig.GetInstance ().getEmail ().getEmailInterval (), TimeUnit.MINUTES );

        //endregion

    }


   
    public static String GetWorkflowList(String wfList)
    {
        String[] wfs = wfList.replace ( " ", "" ).split ( "," );
        String Workflows = "'" + String .join ( "','" ,wfs )+ "'" ;
        return  Workflows;

    }

    public void run()
    {
        this.StartStressTest (environment, automationDb,workflowList,isLicenseWF,stressEnabled,repeatWFn);
        System.out.println ( "Running" );
    }
}
