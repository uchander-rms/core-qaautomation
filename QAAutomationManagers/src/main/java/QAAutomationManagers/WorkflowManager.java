package QAAutomationManagers;

import QAAutomationObjects.Workflow;

import java.sql.ResultSet;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WorkflowManager {
    private String AutomationDatabase;

    public void Initialize(String AutomationDB) {
        AutomationDatabase = AutomationDB;
    }

    public HashMap<Integer, Workflow> GetWorkflows(String workFlowList, boolean flag) {
        HashMap<Integer, Workflow> workFlows = new HashMap<> ();

        try {


            ResultSet rs = LoadWorkflowsResultSet ( workFlowList, AutomationDatabase );

            while (rs.next ()) {

                Date dt = new Date ();
                Workflow workflow = new Workflow () {{
                    Id = rs.getInt ( "WorkFlowId" );
                    Name = rs.getString ( "Name" );
                    Description = rs.getString ( "Description" );
                    Owner = rs.getString ( "Owner" );
                    CronJob = rs.getBoolean ( "CronJob" );
                    TimeInterval = rs.getInt ( "TimeInterval" );
                    IsControl = rs.getBoolean ( "IsControl" );
                    IsDetailed = rs.getBoolean ( "IsDetailed" );
                    WorkFlowType = rs.getString ( "WorkFlowType" );
                    LastModified = dt;
                    CheckLicense = rs.getBoolean ( "CheckLicense" );
                }};

                workFlows.put ( rs.getInt ( "WorkFlowId" ), workflow );
            }

        } catch (Exception ex) {
            System.out.println ( "Exception Occurred: GetWorkflows:" + ex.getMessage () );
            throw new RuntimeException ( ex.getMessage () );
        }

        return workFlows;
    }


    private static ResultSet LoadWorkflowsResultSet(String workflowList, String AutomationDatabase) {

        String sql = String.format ( "SELECT [WorkFlowId],[Name],[Description],[Owner],[CronJob],[TimeInterval],[IsControl],[IsDetailed],[WorkFlowType], [CheckLicense] FROM [dbo].[Workflows] WHERE IsControl = 1" );
        ResultSet rs = null;
        try {
            if ((workflowList != null) && (!workflowList.isEmpty ())) {
                sql = sql + String.format ( " and [Name] IN (%s) ORDER BY WorkflowType", workflowList );
                rs = QAAutomationUtils.SQLDatabase.ExecuteQuery ( AutomationDatabase, sql );
            }
        } catch (Exception ex) {
            System.out.println ( "Exception Occurred: LoadWOrkflowDataset:" );
            throw new RuntimeException ( ex.getMessage () );
        }
        return rs;
    }

    public static ConcurrentHashMap<Integer, Workflow> GetStressWorkflows(String workflowList, String AutomationDB) {
        ConcurrentHashMap<Integer, Workflow> WFs = new ConcurrentHashMap<> ();

        try {
            ResultSet rs = LoadWorkflowsResultSet ( workflowList, AutomationDB );
            while (rs.next ()) {

                Date dt = new Date ();
                Workflow workflow = new Workflow () {{
                    Id = rs.getInt ( "WorkFlowId" );
                    Name = rs.getString ( "Name" );
                    Description = rs.getString ( "Description" );
                    Owner = rs.getString ( "Owner" );
                    CronJob = rs.getBoolean ( "CronJob" );
                    TimeInterval = rs.getInt ( "TimeInterval" );
                    IsControl = rs.getBoolean ( "IsControl" );
                    IsDetailed = rs.getBoolean ( "IsDetailed" );
                    WorkFlowType = rs.getString ( "WorkFlowType" );
                    LastModified = dt;
                    CheckLicense = rs.getBoolean ( "CheckLicense" );
                }};

                WFs.put ( rs.getInt ( "WorkFlowId" ), workflow );
            }
        } catch (Exception e) {
            System.out.println ( e.getMessage () );
        }


        return WFs;
    }
}


