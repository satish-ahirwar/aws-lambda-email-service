package com.email.service;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.*;

public class EmailSenderHandler implements RequestHandler<Object, String> {

    private static final String FROM_EMAIL = "yourgmail@gmail.com";
    private static final String APP_PASSWORD = "your-16-character-app-password";

    private static final String TO_EMAIL = "recipient@gmail.com";
    private static final String SUBJECT = "Automated Email from AWS Lambda (Jakarta Mail)";
    private static final String BODY =
            "Hello!\n\nThis is a test email sent automatically from an AWS Lambda Java function " +
                    "using Jakarta Mail and Gmail SMTP.\n\nRegards,\nLambda Java Service";

    @Override
    public String handleRequest(Object input, Context context) {

        try {
            // SMTP configuration for Gmail
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

            // Create an authenticated mail session
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
                }
            });

            // Compose the message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO_EMAIL));
            message.setSubject(SUBJECT);
            message.setText(BODY);

            // Send it
            Transport.send(message);

            context.getLogger().log("✅ Email sent successfully to " + TO_EMAIL);
            return "✅ Email sent successfully to " + TO_EMAIL;

        } catch (MessagingException e) {
            context.getLogger().log("❌ Error sending email: " + e.getMessage());
            e.printStackTrace();
            return "❌ Error sending email: " + e.getMessage();
        }
    }
}
