package schoolmanagement.service;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService {
    public boolean isConfigured() {
        return !getConfig("SMTP_FROM", "").isBlank() && !getConfig("SMTP_PASSWORD", "").isBlank();
    }

    public String configurationMessage() {
        if (isConfigured()) {
            return "Email is configured for " + getConfig("SMTP_FROM", "");
        }
        return "Email is not configured yet. Set SMTP_FROM and SMTP_PASSWORD before sending.";
    }

    public void send(String to, String subject, String body) throws MessagingException {
        String from = getConfig("SMTP_FROM", "");
        String password = getConfig("SMTP_PASSWORD", "");
        String host = getConfig("SMTP_HOST", "smtp.gmail.com");
        String port = getConfig("SMTP_PORT", "587");

        if (from.isBlank() || password.isBlank()) {
            throw new MessagingException("Set SMTP_FROM and SMTP_PASSWORD before sending email.");
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);
        Transport.send(message);
    }

    private String getConfig(String name, String defaultValue) {
        String value = System.getProperty(name);
        if (value == null || value.isBlank()) {
            value = System.getenv(name);
        }
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
