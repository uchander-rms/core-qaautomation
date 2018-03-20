package QAAutomationTestEngine;

import QAAutomationManagers.TestCaseOutputManager;
import QAAutomationObjects.TestCase;
import QAAutomationObjects.TestCaseOutput;
import QAAutomationTestInterface.ITestEngine;
import QAAutomationManagers.TestCaseInputManager;

import java.util.HashMap;

abstract public class TestEngine implements ITestEngine
{

    private boolean disposed = false;
    private String automationDatabase;

    protected String category;
    protected String description;
    protected int priority;
    protected String testId;
    protected String testType;
    protected String testDataSQLQuery;



    public String getAutomationDatabase() {
        return automationDatabase;
    }

    public void setAutomationDatabase(String automationDatabase) {
        this.automationDatabase = automationDatabase;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getTestDataSQLQuery() {
        return testDataSQLQuery;
    }

    public void setTestDataSQLQuery(String testDataSQLQuery) {
        this.testDataSQLQuery = testDataSQLQuery;
    }

    public boolean GetInput(TestCase TestCase)
    {
        TestCaseInputManager testCaseInputManger = new TestCaseInputManager();
        testCaseInputManger.Initialize(TestCase.AutomationDB);

        HashMap<String, Object> keyValues = testCaseInputManger.GetTestData(TestCase.TestCaseNumber, TestCase.TestCaseType);
        if (keyValues == null || keyValues.size () == 0)
        {
            throw new RuntimeException ("Failed to find the test case.");
        }

        if(TestCase.IsDependent)
        {
            TestCaseOutputManager outputManager = new TestCaseOutputManager();
            outputManager.Initialize(TestCase.AutomationDB);

            TestCaseOutput output = outputManager.GetOutput(TestCase.EnvironmentId, TestCase.WorkflowId, TestCase.ThreadGuid);
            if(output != null)
            {
                Object value = null;

                value = keyValues.get ( "ExposureID" );
                if ( value != null)
                    keyValues.replace ( "ExposureID", output.ExposureId);

                value = keyValues.get ( "ExposureType" );
                if ( value != null)
                    keyValues.replace ( "ExposureType" , output.ExposureType);

                value = keyValues.get ( "EdmName" );
                if ( value != null)
                    keyValues.replace ( "EdmName" ,output.DestEDM);
            }
        }

        TestCase.KeyValues = keyValues;

        return true;
    }
    //endregion
    abstract public boolean Run( TestCase TestCase);

    abstract public boolean Validate( TestCase TestCase);
}