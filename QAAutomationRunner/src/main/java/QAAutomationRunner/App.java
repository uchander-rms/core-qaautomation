package QAAutomationRunner;

import QAAutomationConfiguration.Configuration;
import QAAutomationConfiguration.RBUserConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;


public class App {

    private static final Logger logger = Logger.getLogger ( App.class );
    static Configuration config = Configuration.GetInstance ();


    public static void main(String[] args) {

        logger.debug ( "Starting QA automation suite..." );

        try
        {
            ObjectMapper mapper = new ObjectMapper ( new YAMLFactory () );
            mapper.configure ( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false );

            //ClassLoader loader = App.class.getClassLoader ();
            //File file = new File ( loader.getResource ( "configuration.yml" ).getFile () );
            FileInputStream file = new java.io.FileInputStream(args[0]);

            config = mapper.readValue ( file, Configuration.class );

            RBUserConfig userConfig = RBUserConfig.GetInstance ();
            userConfig.SetConfiguration ( config );
        } catch (Exception e) {
            logger.error ( e.getMessage (), new Exception ( e ) );
            System.out.println ( e.getMessage () );
        }

        boolean stressEnabled = false;
        boolean repeatWF = false;
        //MainThread mainThread = null;


        try {

            //region Validate mandatory inputs

            if (args == null || args.length < 5) {
                logger.error ( "Invalid parameters passed.", new Exception ( "Invalid parameters passed." ) );
                throw new Exception ( "Invalid parameters passed." );
            }
            logger.debug ( String.format ( "Number of parameters passed: %s", args.length ) );

            if (args[1] == null || args[1].trim () == "") {
                logger.error ( "Environment name is blank.", new Exception ( "Environment name is blank." ) );
                throw new Exception ( "Environment name is blank." );
            }
            System.out.println ( "ENVIRONMENT: " + args[1] );
            logger.debug ( String.format ( "Environment name passed: %s", args[1] ) );

            if (args[2] == null || args[2].trim ().equals ( "")) {
                //args[1] = config.getURL ();
            }
            System.out.println ( "AUTOMATION DB: " + args[2] );
            logger.debug ( String.format ( "Automation DB details: %s", args[2] ) );

            //endregion

            //region
            if (args[3] == null || args[3].trim () == "") {
                throw new Exception ( "List of workflows to run is blank." );
            }
            System.out.println ( "WORKFLOWS: " + args[3] );
            logger.debug ( String.format ( "List of workflows passed: %s", args[3] ) );
            //endregion

            //region
            if (args[4] == null || args[4].trim () == "")
                stressEnabled = false;
            else if (args[4].toUpperCase ().toString ().equals ( "TRUE" ))
                stressEnabled = true;


            System.out.println ( "Stress mode enabled: " + args[4] );
            logger.debug ( String.format ( "Stress mode enabled %s", args[4] ) );
            //endregion


            //region
            if (args[5] == null || args[5].trim () == "")
                repeatWF = false;
            else if (args[5].toString ().toUpperCase ().equals ( "TRUE" ))
                repeatWF = true;

            System.out.println ( "Repeat same workflow: " + args[5] );
            logger.debug ( String.format ( "Repeat same workflow %s", args[5] ) );
            //endregion


            if (stressEnabled) {
                logger.debug ( "START: Stress WORKFLOWS - Running..." );
                System.out.println ( "START: Stress WORKFLOWS - Running..." );

                //new QAAutomationCore.ExecutionManager ().RunWorkflows ( args[0], args[1], args[2], false, stressEnabled, repeatWF );

                StressTest.StartStressTest ( args[1], args[2], args[3], false, stressEnabled, repeatWF );
            } else {
                logger.debug ( "START: WORKFLOWS - Running..." );
                System.out.println ( "START: WORKFLOWS - Running..." );

                new QAAutomationCore.ExecutionManager ().RunWorkflows ( args[1], args[2], args[3], false );

            }

            logger.debug ( "END: WORKFLOWS - Finished." );
            System.out.println ( "END: WORKFLOWS - Finished." );

        } catch (Exception e) {
            logger.error ( e.getMessage (), new Exception ( e ) );

        }
    }

}