package QAAutomationConfiguration;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Connection {

    private String url;
    private int poolSize;


    public String getJdbc_driver() {
        return jdbc_driver;
    }

    public void setJdbc_driver(String jdbc_driver) {
        this.jdbc_driver = jdbc_driver;
    }

    private String jdbc_driver;

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public int getPoolSize() {
        return poolSize;
    }
    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }
   /*
    public String getDriver() {
        return driver;
    }
    public void setDriver(String driver) {
        this.driver = driver;
    }
*/


}
