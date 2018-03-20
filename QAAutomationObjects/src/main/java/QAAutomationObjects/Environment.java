package QAAutomationObjects;
public class Environment {

    public int Id ;
    public String Name ;
    public String Description ;
    public String ClusterName ;
    public String EnvironmentType ;
    public boolean LicenseWF ;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getClusterName() {
        return ClusterName;
    }

    public void setClusterName(String clusterName) {
        ClusterName = clusterName;
    }

    public String getEnvironmentType() {
        return EnvironmentType;
    }

    public void setEnvironmentType(String environmentType) {
        EnvironmentType = environmentType;
    }

    public boolean isLicenseWF() {
        return LicenseWF;
    }

    public void setLicenseWF(boolean licenseWF) {
        LicenseWF = licenseWF;
    }
}
