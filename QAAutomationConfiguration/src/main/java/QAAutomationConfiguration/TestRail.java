package QAAutomationConfiguration;

public final class TestRail {



    private String trUpdateStatus;
    private String trURL;
    private String trUser;
    private String trPass;
    private String trRunId;

    public String getTrURL() {
        return trURL;
    }

    public void setTrURL(String trURL) {
        this.trURL = trURL;
    }

    public String getTrUser() {
        return trUser;
    }

    public void setTrUser(String trUser) {
        this.trUser = trUser;
    }

    public String getTrPass() {
        return trPass;
    }

    public void setTrPass(String trPass) {
        this.trPass = trPass;
    }

    public String getTrRunId() {
        return trRunId;
    }

    public void setTrRunId(String trRunId) {
        this.trRunId = trRunId;
    }

    public String getTrUpdateStatus() { return trUpdateStatus; }

    public void setTrUpdateStatus(String trUpdateStatus) { this.trUpdateStatus = trUpdateStatus; }
}
