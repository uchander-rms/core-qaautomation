package QAAutomationObjects;
import java.util.Date;

public class Workflow {

    public int Id ;

    public String Name ;

    public String Description ;

    public String Owner ;

    public boolean CronJob ;

    public int TimeInterval ;

    public boolean IsControl ;

    public boolean IsDetailed ;

    public String WorkFlowType ;

    public Date LastModified ;

    public boolean CheckLicense ;

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

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }

    public boolean isCronJob() {
        return CronJob;
    }

    public void setCronJob(boolean cronJob) {
        CronJob = cronJob;
    }

    public int getTimeInterval() {
        return TimeInterval;
    }

    public void setTimeInterval(int timeInterval) {
        TimeInterval = timeInterval;
    }

    public boolean isControl() {
        return IsControl;
    }

    public void setControl(boolean control) {
        IsControl = control;
    }

    public boolean isDetailed() {
        return IsDetailed;
    }

    public void setDetailed(boolean detailed) {
        IsDetailed = detailed;
    }

    public String getWorkFlowType() {
        return WorkFlowType;
    }

    public void setWorkFlowType(String workFlowType) {
        WorkFlowType = workFlowType;
    }

    public Date getLastModified() {
        return LastModified;
    }

    public void setLastModified(Date lastModified) {
        LastModified = lastModified;
    }

    public boolean isCheckLicense() {
        return CheckLicense;
    }

    public void setCheckLicense(boolean checkLicense) {
        CheckLicense = checkLicense;
    }
}

