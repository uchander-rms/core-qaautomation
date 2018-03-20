package QAAutomationObjects;
public class HostCredentials {

    public String HostName ;

    public String Domain ;

    public String Role ;

    public String UserName ;

    public String Password ;

    public String SQLUserName ;

    public String SQLPassword ;

    public String getHostName() {
        return HostName;
    }

    public void setHostName(String hostName) {
        HostName = hostName;
    }

    public String getDomain() {
        return Domain;
    }

    public void setDomain(String domain) {
        Domain = domain;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getSQLUserName() {
        return SQLUserName;
    }

    public void setSQLUserName(String SQLUserName) {
        this.SQLUserName = SQLUserName;
    }

    public String getSQLPassword() {
        return SQLPassword;
    }

    public void setSQLPassword(String SQLPassword) {
        this.SQLPassword = SQLPassword;
    }
}
