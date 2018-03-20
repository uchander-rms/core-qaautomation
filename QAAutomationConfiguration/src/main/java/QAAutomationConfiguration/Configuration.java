package QAAutomationConfiguration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

public class Configuration
{

    //static variable declaration
    private static Configuration config = null;

    private Connection connection;
    private HashMap< String, String > users;
    private Environment environment;
    private Email email;
    private TestRail testRail;

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

    private  Configuration(){}

    public static  Configuration GetInstance()
    {
        if(config == null)
        {
            synchronized (Configuration.class)
            {
                if(config ==null)
                    config = new Configuration ();
            }

        }
        return config;
    }




}
