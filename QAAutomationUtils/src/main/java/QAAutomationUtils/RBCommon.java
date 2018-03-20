package QAAutomationUtils;


import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONString;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;


public  class RBCommon{

    private static final Logger logger = Logger.getLogger ( RBCommon.class );

    public static String GetAuthToken(String environment,String authPort , HashMap<String, String> users)
    {
        String authenticationToken = "";
        final String[] userName = {""};
        final String[] userPass = {""};
        users.forEach((k, v) -> {
            userName[0] = k;
            userPass[0] = v;
        });


        try {
            JSONObject input = new JSONObject();
            input.put ( "username", userName[0].toString () );
            input.put ( "password", userPass[0].toString () );
            input.put ( "tenant", 1 );

            Client client = JerseyClientBuilder.createClient ();

            String URL =  environment + "/v1/token/create";

            Response response = client.target ( URL )
                    .request ( MediaType.APPLICATION_JSON )
                    .post ( Entity.entity (input, MediaType.APPLICATION_JSON) );

            if (response.getStatus() != 200) {
                logger.error ( "Failed: HTTP Error Code: " + response.getStatus () );
                System.out.println ( "Failed: HTTP Error Code: " + response.getStatus () );
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }


            Map<String, String> authJson = response.readEntity( new GenericType<Map<String,String>> (  ){});
            authenticationToken = "Bearer " + authJson.get ( "token" );

            /*String [] authTokens = authenticationToken.split ( ":" );
            authenticationToken = ("Bearer " + authTokens[1].replace ("\"", "").trim ().replace ( "}","" )).
                    replaceAll ("\r" ,"" ).replaceAll ( "\n", "" );*/

            System.out.println("Output from Server .... \n" + authenticationToken);

        } catch (Exception e) {

            e.printStackTrace();

        }


        return  authenticationToken;
    }

    public static String ProcessAccount(String URL, String authToken, JSONObject dlmInput){

        String jobId ="";


        System.out.println ( URL );

        try {

            Client client = JerseyClientBuilder.createClient ();

            Response response = client.target ( URL )
                    .request ( MediaType.APPLICATION_JSON )
                    .header ( "Authorization", authToken  )
                    .post ( Entity.entity (dlmInput, MediaType.APPLICATION_JSON) );

            if (response.getStatus () != 201) {
                logger.error ( "Failed: HTTP Error Code: " + response.getStatus () );
                System.out.println ( "Failed: HTTP Error Code: " + response.getStatus () );
                throw new RuntimeException ( "Failed : HTTP error code : "
                        + response.getStatus () );


            }

/*
            Client client =  Client.create ();

            //client.addFilter ( new LoggingFilter ( System.out ) );
            WebResource resource = client.resource ( URL );

            resource.header ( "Authorization",   authToken);
            System.out.println ( dlmInput.toJSONString () );

            ClientResponse response = resource.header ( "Authorization", authToken ).type ( MediaType.APPLICATION_JSON_TYPE ).post ( ClientResponse.class, dlmInput.toJSONString () );

            if (response.getStatus () != 201) {
                logger.error ( "Failed: HTTP Error Code: " + response.getStatus () );
                System.out.println ( "Failed: HTTP Error Code: " + response.getStatus () );
                throw new RuntimeException ( "Failed : HTTP error code : "
                        + response.getStatus () );


            }
*/

            jobId = response.readEntity(String.class);
            JSONObject json = (JSONObject) new JSONParser ().parse(jobId);

            jobId =  json.get ( "JobId" ).toString ();

        }
        catch (Exception ex)
        {
            throw new RuntimeException ( ex.getMessage () );
        }


        return  jobId;
    }

    public static String GeocodeAccount(String URL, String authToken)
    {

        String jobId ="";

        try
        {
            Client client = JerseyClientBuilder.createClient ();

            Response response = client.target ( URL )
                    .request ( MediaType.APPLICATION_JSON )
                    .header ( "Authorization", authToken  )
                    .post ( Entity.json(""));

            if (response.getStatus () != 201) {
                logger.error ( "Failed: HTTP Error Code: " + response.getStatus () );
                System.out.println ( "Failed: HTTP Error Code: " + response.getStatus () );
                throw new RuntimeException ( "Failed : HTTP error code : "
                        + response.getStatus () );
            }


            jobId = response.readEntity (String.class);
        }
        catch (Exception ex)
        {
            throw  new RuntimeException ( ex.getMessage () );
        }
/*
        try {
            Client client =  Client.create ();

            //client.addFilter ( new LoggingFilter ( System.out ) );
            WebResource resource = client.resource ( URL );

            ClientResponse response = resource.header ( "Authorization", authToken ).type ( MediaType.APPLICATION_JSON_TYPE ).post ( ClientResponse.class );

            if (response.getStatus () != 201) {
                logger.error ( "Failed: HTTP Error Code: " + response.getStatus () );
                System.out.println ( "Failed: HTTP Error Code: " + response.getStatus () );
                throw new RuntimeException ( "Failed : HTTP error code : "
                        + response.getStatus () );
            }


            jobId = response.getEntity(String.class);

        }
        catch (Exception ex)
        {
            throw  new RuntimeException ( ex.getMessage () );
        }
*/
        return  jobId;

    }
}
