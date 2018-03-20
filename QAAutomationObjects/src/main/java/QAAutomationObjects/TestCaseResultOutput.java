package QAAutomationObjects;
public class TestCaseResultOutput {

    public String TestCaseNumber ;

    public String TestCaseDescription ;

    public String TestCaseType ;

    public int FailedTestCases ;

    public int PassedTestCases ;

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

    public int getFailedTestCases() {
        return FailedTestCases;
    }

    public void setFailedTestCases(int failedTestCases) {
        FailedTestCases = failedTestCases;
    }

    public int getPassedTestCases() {
        return PassedTestCases;
    }

    public void setPassedTestCases(int passedTestCases) {
        PassedTestCases = passedTestCases;
    }
}
