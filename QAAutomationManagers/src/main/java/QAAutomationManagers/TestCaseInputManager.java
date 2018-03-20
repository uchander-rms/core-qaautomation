package QAAutomationManagers;
import QAAutomationUtils.SQLDatabase;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

import java.sql.*;
import java.util.HashMap;

public class TestCaseInputManager {

    private static final Logger logger = Logger.getLogger ( TestCaseInputManager.class );
    private String AutomationDatabase;

    private String GetProcedureName( String testCaseType) {
        switch (testCaseType.toUpperCase()) {

            case "RB_GEOCODE":
                return "usp_GetGeocodeCasesRB";
            case "RB_HAZARD":
                return "usp_GetHazardCasesRB";
            case "RB_DLM":
            case "RB_AUTOSELECT":
                return "usp_GetDLMCasesRB";


        }
            return "";
    }

    public void Initialize(String AutomationDB) {
        AutomationDatabase = AutomationDB;
    }

    public HashMap<String, Object> GetTestData(String testCaseNumber, String testCaseType) {
        HashMap<String, Object> TestData = null;

        try {

            String procedureName = GetProcedureName ( testCaseType );
            String[] parameters = {testCaseNumber};
            ResultSet rs = SQLDatabase.ExecuteProcedure ( AutomationDatabase,procedureName, parameters );

            TestData = ResultSetToHashMap(rs);

        } catch (SQLException se) {
            se.printStackTrace();
            throw new RuntimeException ( se.getMessage () );

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException ( e.getMessage () );

        }
        return TestData;
    }

    private HashMap<String, Object> ResultSetToHashMap(ResultSet rs) throws  SQLException{
            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();
            HashMap<String,Object> testData = new HashMap<String, Object>(columns);

            while (rs.next()){
                for(int i=1; i<=columns; ++i){
                    testData.put(md.getColumnName(i),rs.getObject(i));
                }
            }

            return testData;
        }

}

