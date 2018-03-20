package QAAutomationTestEngineFactory;
import QAAutomationTestInterface.ITestEngine;


public final class TestEngineFactory // implements AutoCloseable
{
    private TestEngineFactory(){}

    public static ITestEngine GetEngine(String engineType)  {


        try {

            switch (engineType.toUpperCase ()) {
                case "RB_GEOCODE":
                    try {
                        return (ITestEngine) Class.forName ( "GeoTestEngine.GeocodeTest" ).newInstance ();
                    } catch (InstantiationException e) {
                        e.printStackTrace ();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace ();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace ();
                    }

                case "RB_DLM":
                    try {
                        return (ITestEngine) Class.forName ( "DLMTestEngine.DLMTests" ).newInstance ();
                    } catch (InstantiationException e) {
                        e.printStackTrace ();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace ();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace ();
                    }

                case "RB_HAZARD":
                    try {
                        return (ITestEngine) Class.forName ( "HazardTestEngine.HazardTests" ).newInstance ();
                    } catch (InstantiationException e) {
                        e.printStackTrace ();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace ();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace ();
                    }

                default:
                    throw new RuntimeException ( String.format ( "Test Engine {0} can not be created", engineType ) );
            }
        }
        catch (Exception e)
        {
            System.out.println ( "Exception Occurred: Test Engine factory:" + e.getMessage () );
            throw new RuntimeException ( e.getMessage () );
        }

    }
/*
    @Override public void close()  {
        throw new RuntimeException ( "TestEngineFactory: AutoCloseable" );

    }
    */
}


