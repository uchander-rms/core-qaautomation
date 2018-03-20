package QAAutomationObjects;
public class WorkflowTestCase {


    public long Id ;

    public Workflow Workflow ;

    public TestCaseType TestCaseType ;

    public String TestCaseNumber ;

    public String TestCaseDescription ;

    public String DependentTestCase ;

    public String ExecutionGuid ;

    public int SequenceId ;

    public boolean IsControl ;


    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public Workflow getWorkflow() {
        return Workflow;
    }

    public void setWorkflow(Workflow workflow) {
        Workflow = workflow;
    }

    public TestCaseType getTestCaseType() {
        return TestCaseType;
    }

    public void setTestCaseType(TestCaseType testCaseType) {
        TestCaseType = testCaseType;
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

    public String getDependentTestCase() {
        return DependentTestCase;
    }

    public void setDependentTestCase(String dependentTestCase) {
        DependentTestCase = dependentTestCase;
    }

    public String getExecutionGuid() {
        return ExecutionGuid;
    }

    public void setExecutionGuid(String executionGuid) {
        ExecutionGuid = executionGuid;
    }

    public int getSequenceId() {
        return SequenceId;
    }

    public void setSequenceId(int sequenceId) {
        SequenceId = sequenceId;
    }

    public boolean isControl() {
        return IsControl;
    }

    public void setControl(boolean control) {
        IsControl = control;
    }
}

