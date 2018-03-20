package QAAutomationConfiguration;

import java.util.HashMap;

public class RBUserConfig {

    //static variable declaration
    private static RBUserConfig config = null;

    private static Connection connection;
    private static HashMap< String, String > users;
    private static Environment environment;
    private static Email email;
    private static TestRail testRail;

    public  TestRail getTestRail() {
        return testRail;
    }
    public  void setTestRail(TestRail testRail) {
        this.testRail = testRail;
    }
    public  Connection getConnection() {

        return connection;
    }
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    public  HashMap<String, String> getUsers() {
        return users;
    }
    public void setUsers(HashMap<String, String> users) {
        this.users = users;
    }
    public  Environment getEnvironment() {
        return environment;
    }
    public  void setEnvironment(Environment environment) {
        this.environment = environment;
    }
    public Email getEmail() {
        return email;
    }
    public  void setEmail(Email email) {
        this.email = email;
    }

    private  RBUserConfig(){}

    public static  RBUserConfig GetInstance()
    {
        if(config == null)
        {
            synchronized (RBUserConfig.class)
            {
                if(config ==null)
                    config = new RBUserConfig ();
            }

        }
        return config;
    }

    public void SetConfiguration(Configuration configuration)
    {
        config.setConnection ( configuration.getConnection () );
        config.setEmail ( configuration.getEmail () );
        config.setEnvironment ( configuration.getEnvironment () );
        config.setTestRail ( configuration.getTestRail () );
        config.setUsers ( configuration.getUsers () );
    }
}
