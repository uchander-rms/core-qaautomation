package QAAutomationObjects;
public class TestCaseOutput {

    public TestCaseOutput()
    {
        ExposureId = 0;
        ExposureType = "";
        AnalysisId = 0;
    }

    public long Id ;

    public int EnvironmentId ;

    public int WorkflowId ;

    public int ExposureId ;

    public String ExposureType ;

    public int AnalysisId ;

    public String DestEDM ;

    public String ThreadGuid ;

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public int getEnvironmentId() {
        return EnvironmentId;
    }

    public void setEnvironmentId(int environmentId) {
        EnvironmentId = environmentId;
    }

    public int getWorkflowId() {
        return WorkflowId;
    }

    public void setWorkflowId(int workflowId) {
        WorkflowId = workflowId;
    }

    public int getExposureId() {
        return ExposureId;
    }

    public void setExposureId(int exposureId) {
        ExposureId = exposureId;
    }

    public String getExposureType() {
        return ExposureType;
    }

    public void setExposureType(String exposureType) {
        ExposureType = exposureType;
    }

    public int getAnalysisId() {
        return AnalysisId;
    }

    public void setAnalysisId(int analysisId) {
        AnalysisId = analysisId;
    }

    public String getDestEDM() {
        return DestEDM;
    }

    public void setDestEDM(String destEDM) {
        DestEDM = destEDM;
    }

    public String getThreadGuid() {
        return ThreadGuid;
    }

    public void setThreadGuid(String threadGuid) {
        ThreadGuid = threadGuid;
    }
}
