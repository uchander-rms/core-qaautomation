package QAAutomationUtils;


import sun.plugin2.message.Message;


import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;


    public class Email {

        public  void SendEmail(String EmailServer, int EmailServerPort, String EmailTo, String EmailFrom, boolean EnableSSL, String EmailSubject, String EmailBody, String EmailPassword) {
            // Recipient's email ID needs to be mentioned.
            //EmailTo = "umesh.chander@rms.com";

            // Sender's email ID needs to be mentioned
            EmailFrom = "rlrbuser@gmail.com";

            EmailPassword = "@ut0m@t10n";

            // Get system properties
            Properties properties = new Properties (  );

            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");



            properties.setProperty("mail.user", EmailFrom);
            properties.setProperty("mail.password", EmailPassword);

            String finalEmailPassword = EmailPassword;
            String finalEmailFrom = EmailFrom;
            javax.mail.Authenticator auth = new javax.mail.Authenticator (){
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication( finalEmailFrom, finalEmailPassword );
                }
            };

            // Get the default Session object.
            Session session = Session.getInstance(properties,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(finalEmailFrom, finalEmailPassword);
                        }
                    });

            try {
                // Create a default MimeMessage object.
                MimeMessage message = new MimeMessage(session);

                // Set From: header field of the header.
                message.setFrom(new InternetAddress(EmailFrom));

                // Set To: header field of the header.
                message.addRecipient( MimeMessage.RecipientType.TO, new InternetAddress(EmailTo));

                // Set Subject: header field
                message.setSubject(EmailSubject);

                // Send the actual HTML message, as big as you like
                message.setContent(EmailBody,"Text/HTML");

                // Send message
                Transport.send ( message );

                System.out.println("Sent message successfully....");
            } catch (MessagingException mex) {
                mex.printStackTrace();
            }
        }
    }
