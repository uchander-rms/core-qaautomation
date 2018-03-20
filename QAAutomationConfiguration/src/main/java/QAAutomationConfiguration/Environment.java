package QAAutomationConfiguration;

public final class Environment {

    private String name ;
    private String authenticationPort;
    private String accountPort;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAuthenticationPort() {
        return authenticationPort;
    }
    public void setAuthenticationPort(String authenticationPort) {
        this.authenticationPort = authenticationPort;
    }
    public String getAccountPort() {
        return accountPort;
    }
    public void setAccountPort(String accountPort) {
        this.accountPort = accountPort;
    }
}
