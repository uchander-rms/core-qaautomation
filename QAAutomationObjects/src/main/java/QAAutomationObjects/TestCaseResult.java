package QAAutomationObjects;
import java.util.Date;

public class TestCaseResult {

    public int TestCaseResultsId ;

    public int EnvironmentId ;

    public String EnvironmentName ;

    public int WorkflowId ;

    public String WorkflowName ;

    public String WorkflowOwner ;

    public String TestCaseNumber ;

    public String TestCaseDescription ;

    public String TestCaseType ;

    public String TestCaseDetail ;

    public TestRailStatus TestCaseStatus ;

    public int Iteration ;

    public String ExecutedBy ;

    public Date SubmitTime ;

    public Date StartTime ;

    public Date EndTime ;

    public int JobId ;

    public String JobStatusCode ;

    public String ExecutionGuid ;


    public int getTestCaseResultsId() {
        return TestCaseResultsId;
    }

    public void setTestCaseResultsId(int testCaseResultsId) {
        TestCaseResultsId = testCaseResultsId;
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

    public String getTestCaseDetail() {
        return TestCaseDetail;
    }

    public void setTestCaseDetail(String testCaseDetail) {
        TestCaseDetail = testCaseDetail;
    }

    public TestRailStatus getTestCaseStatus() {
        return TestCaseStatus;
    }

    public void setTestCaseStatus(TestRailStatus testCaseStatus) {
        TestCaseStatus = testCaseStatus;
    }

    public int getIteration() {
        return Iteration;
    }

    public void setIteration(int iteration) {
        Iteration = iteration;
    }

    public String getExecutedBy() {
        return ExecutedBy;
    }

    public void setExecutedBy(String executedBy) {
        ExecutedBy = executedBy;
    }

    public Date getSubmitTime() {
        return SubmitTime;
    }

    public void setSubmitTime(Date submitTime) {
        SubmitTime = submitTime;
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

    public String getExecutionGuid() {
        return ExecutionGuid;
    }

    public void setExecutionGuid(String executionGuid) {
        ExecutionGuid = executionGuid;
    }
}
