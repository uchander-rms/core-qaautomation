package QAAutomationManagers;

import QAAutomationObjects.TestCaseType;

import java.sql.ResultSet;
import java.util.HashMap;
import QAAutomationUtils.SQLDatabase;

import javax.xml.transform.Result;

public class TestCaseTypeManager {

    private String AutomationDatabase;

    public void Initialize(String automationDatabase)
    {
        AutomationDatabase = automationDatabase;
    }

    public HashMap<String, TestCaseType> GetTypes()
    {
        HashMap<String, TestCaseType> testCaseTypes = new HashMap<> ( );
        try
        {
            String procedureName = "usp_GetTestCaseTypes";
            String [] parameters = {};
            ResultSet rs = SQLDatabase.ExecuteProcedure ( AutomationDatabase, procedureName, parameters );

            while (rs.next ())
            {
                TestCaseType type = new TestCaseType ();
                type.Module = rs.getString ( "Module" );
                type.Name = rs.getString ( "Name" );
                type.Type = rs.getString ( "Type" );
                testCaseTypes.put ( type.Type, type );
            }

        }
        catch (Exception e)
        {
            System.out.println ( "Excption occurred: TestCaseTypeManager:" + e.getMessage () );
            throw  new RuntimeException ( e.getMessage () );
        }
        return testCaseTypes;
    }
}
