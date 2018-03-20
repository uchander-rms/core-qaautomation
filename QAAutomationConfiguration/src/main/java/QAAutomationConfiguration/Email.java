package QAAutomationConfiguration;

public final class Email {
    private String emailTo;
    private String emailCC;



    private int emailInterval;

    public String getEmailTo() {
        return emailTo;
    }
    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }
    public String getEmailCC() {
        return emailCC;
    }
    public void setEmailCC(String emailCC) {
        this.emailCC = emailCC;
    }
    public int getEmailInterval() { return emailInterval; }
    public void setEmailInterval(int emailInterval) { this.emailInterval = emailInterval; }


}
