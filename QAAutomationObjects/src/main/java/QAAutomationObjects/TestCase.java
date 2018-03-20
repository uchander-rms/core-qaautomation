package QAAutomationObjects;
import java.util.Date;
import java.util.HashMap;

public class TestCase {

    Date date = new Date();
    public TestCase()
    {
        StartTime = (Date) date.clone() ;
        EndTime = (Date) date.clone();
        DependentTestCase = "";
    }

    public String TestCaseNumber ;

    public String TestCaseDescription ;

    public String TestCaseType ;

    public int EnvironmentId ;

    public String EnvironmentName ;

    public String EnvironmentType ;

    public int WorkflowId ;

    public String WorkflowName ;

    public String WorkflowOwner ;

    public String AutomationDB ;

    public String ExecutionGuid ;

    public String ExecutedBy ;

    public int Iteration ;

    public boolean IsDependent ;

    public Date StartTime ;

    public Date EndTime ;

    public int JobId ;

    public String JobStatusCode ;

    public HashMap<String, Object> KeyValues ;

    public String DependentTestCase ;

    public String TestCaseDetails ;

    public boolean IsDetailed ;

    public String WorkFlowType ;

    public String ThreadGuid ;

    public String ClusterName ;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTestCaseNumber() {
        return TestCaseNumber;
    }

    public void setTestCaseNumber(String testCaseNumber) {
        TestCaseNumber = testCaseNumber;
    }

    public String getTestCaseDescription() {
        return TestCaseDescription;
    }

    public void setTestCaseDescription(String testCaseDescription) {
        TestCaseDescription = testCaseDescription;
    }

    public String getTestCaseType() {
        return TestCaseType;
    }

    public void setTestCaseType(String testCaseType) {
        TestCaseType = testCaseType;
    }

    public int getEnvironmentId() {
        return EnvironmentId;
    }

    public void setEnvironmentId(int environmentId) {
        EnvironmentId = environmentId;
    }

    public String getEnvironmentName() {
        return EnvironmentName;
    }

    public void setEnvironmentName(String environmentName) {
        EnvironmentName = environmentName;
    }

    public String getEnvironmentType() {
        return EnvironmentType;
    }

    public void setEnvironmentType(String environmentType) {
        EnvironmentType = environmentType;
    }

    public int getWorkflowId() {
        return WorkflowId;
    }

    public void setWorkflowId(int workflowId) {
        WorkflowId = workflowId;
    }

    public String getWorkflowName() {
        return WorkflowName;
    }

    public void setWorkflowName(String workflowName) {
        WorkflowName = workflowName;
    }

    public String getWorkflowOwner() {
        return WorkflowOwner;
    }

    public void setWorkflowOwner(String workflowOwner) {
        WorkflowOwner = workflowOwner;
    }

    public String getAutomationDB() {
        return AutomationDB;
    }

    public void setAutomationDB(String automationDB) {
        AutomationDB = automationDB;
    }

    public String getExecutionGuid() {
        return ExecutionGuid;
    }

    public void setExecutionGuid(String executionGuid) {
        ExecutionGuid = executionGuid;
    }

    public String getExecutedBy() {
        return ExecutedBy;
    }

    public void setExecutedBy(String executedBy) {
        ExecutedBy = executedBy;
    }

    public int getIteration() {
        return Iteration;
    }

    public void setIteration(int iteration) {
        Iteration = iteration;
    }

    public boolean isDependent() {
        return IsDependent;
    }

    public void setDependent(boolean dependent) {
        IsDependent = dependent;
    }

    public Date getStartTime() {
        return StartTime;
    }

    public void setStartTime(Date startTime) {
        StartTime = startTime;
    }

    public Date getEndTime() {
        return EndTime;
    }

    public void setEndTime(Date endTime) {
        EndTime = endTime;
    }

    public int getJobId() {
        return JobId;
    }

    public void setJobId(int jobId) {
        JobId = jobId;
    }

    public String getJobStatusCode() {
        return JobStatusCode;
    }

    public void setJobStatusCode(String jobStatusCode) {
        JobStatusCode = jobStatusCode;
    }

    public HashMap<String, Object> getKeyValues() {
        return KeyValues;
    }

    public void setKeyValues(HashMap<String, Object> keyValues) {
        KeyValues = keyValues;
    }

    public String getDependentTestCase() {
        return DependentTestCase;
    }

    public void setDependentTestCase(String dependentTestCase) {
        DependentTestCase = dependentTestCase;
    }

    public String getTestCaseDetails() {
        return TestCaseDetails;
    }

    public void setTestCaseDetails(String testCaseDetails) {
        TestCaseDetails = testCaseDetails;
    }

    public boolean isDetailed() {
        return IsDetailed;
    }

    public void setDetailed(boolean detailed) {
        IsDetailed = detailed;
    }

    public String getWorkFlowType() {
        return WorkFlowType;
    }

    public void setWorkFlowType(String workFlowType) {
        WorkFlowType = workFlowType;
    }

    public String getThreadGuid() {
        return ThreadGuid;
    }

    public void setThreadGuid(String threadGuid) {
        ThreadGuid = threadGuid;
    }

    public String getClusterName() {
        return ClusterName;
    }

    public void setClusterName(String clusterName) {
        ClusterName = clusterName;
    }
}
