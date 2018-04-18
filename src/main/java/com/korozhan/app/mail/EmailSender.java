package com.korozhan.app.mail;

import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;

import com.korozhan.app.util.FileUtil;
import com.sun.mail.util.MailSSLSocketFactory;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.io.IOUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by veronika.korozhan on 20.09.2017.
 */
public class EmailSender {
    private static Logger LOGGER = Logger.getLogger(EmailSender.class.getName());

    public static final String host = "smtp.gmail.com";
    public static final String port = "25";
    public static final boolean isAuth = true;
    public static final boolean isTLS = true;
    public static final boolean isSSL = false;
    public static final String sslPort = "465";
    public static final String userName = "username@gmail.com";
    public static final String password = "password";
    public static final String destination = "username@mail.ru";


    private static void sendEmail(String destination, String subject, String messageText, MultiValuedMap<MimeType, Attachment> attachments) throws MessagingException {
        try {
            Properties properties = new Properties();
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", port);
            System.out.println("EMailSender: sendEmail - host=" + host + ", port=" + port);
            properties.put("mail.smtp.connectiontimeout", "15000");
            properties.put("mail.smtp.timeout", "15000");

            if (isAuth) properties.put("mail.smtp.auth", "true");
            if (isTLS) properties.put("mail.smtp.starttls.enable", "true");
            if (isSSL) {
                properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                properties.put("mail.smtp.socketFactory.port", sslPort);
            }
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            properties.put("mail.smtp.ssl.socketFactory", sf);

            // creates a new session with an authenticator
            Authenticator auth = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(userName, password);
                }
            };
            Session session = Session.getInstance(properties, auth);
            // creates a new e-mail message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(userName));

            InternetAddress toAddress = new InternetAddress(destination);
            message.setRecipient(Message.RecipientType.TO, toAddress);
            message.setSubject(subject);

            if (attachments != null) {
                MimeMultipart multipart = new MimeMultipart();

                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent(messageText.replace("\n", "<br>"), MimeType.HTML.getContent());
                multipart.addBodyPart(messageBodyPart);

                for (Map.Entry<MimeType, Attachment> attachment : attachments.entries()) {
                    DataSource dataSource = new ByteArrayDataSource(attachment.getValue().getBytes(), attachment.getKey().getContent());
                    MimeBodyPart pdfBodyPart = new MimeBodyPart();
                    pdfBodyPart.setDataHandler(new DataHandler(dataSource));
                    pdfBodyPart.setFileName(dataSource.getName());
                    try {
                        pdfBodyPart.setFileName(MimeUtility.encodeText(attachment.getValue().getFileName()) + attachment.getKey().extension());
                    } catch (UnsupportedEncodingException e) {
                        LOGGER.info("EMailSender: sendEmail - unsupported encoding");
                    }
                    multipart.addBodyPart(pdfBodyPart);
                }
                message.setContent(multipart);
            } else {
                message.setContent(messageText.replace("\n", "<br>"), MimeType.HTML.getContent());
            }
            logMessageContent(message);
            Transport.send(message);
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void logMessageContent(MimeMessage message) {
        try {
            String contentType = message.getContentType();
            LOGGER.info("EMailSender: logMessageContent - contentType=" + contentType);
            Address[] fromAddress = message.getFrom();
            String from = fromAddress[0].toString();
            LOGGER.info("EMailSender: logMessageContent - from=" + from);
            Address[] recipients = message.getRecipients(Message.RecipientType.TO);
            for (Address address : recipients) {
                LOGGER.info("EMailSender: logMessageContent - to=" + address.toString());
            }
            String subject = message.getSubject();
            LOGGER.info("EMailSender: logMessageContent - subject=" + subject);
            if (message.getContent() instanceof Multipart) {
                Multipart mp = (Multipart) message.getContent();
                int partsCount = mp.getCount();
                LOGGER.info("EMailSender: logMessageContent - multipart count=" + partsCount);
                for (int i = 0; i < partsCount; i++) {
                    MimeBodyPart part = (MimeBodyPart) mp.getBodyPart(i);
                    LOGGER.info("EMailSender: logMessageContent - multipart [" + i + "] contentType=" + part.getContentType());
                    LOGGER.info("EMailSender: logMessageContent - multipart [" + i + "] desposition=" + part.getDisposition());
                    LOGGER.info("EMailSender: logMessageContent - multipart [" + i + "] encoding=" + part.getEncoding());
                    LOGGER.info("EMailSender: logMessageContent - multipart [" + i + "] filename=" + part.getFileName());
                    LOGGER.info("EMailSender: logMessageContent - multipart [" + i + "] size=" + part.getSize());
                    LOGGER.info("EMailSender: logMessageContent - multipart [" + i + "] contentID=" + part.getContentID());
                    String content;
                    ByteArrayInputStream in = part.getContent() instanceof ByteArrayInputStream ? ((ByteArrayInputStream) part.getContent()) : null;
                    if (in != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buf = new byte[8192];
                        for (; ; ) {
                            int nread = in.read(buf, 0, buf.length);
                            if (nread <= 0) {
                                break;
                            }
                            baos.write(buf, 0, nread);
                        }
                        in.close();
                        baos.close();
                        byte[] bytes = baos.toByteArray();
                        content = DatatypeConverter.printBase64Binary(bytes);
                    } else {
                        content = part.getContent().toString();
                    }
                    LOGGER.info("EMailSender: logMessageContent - multipart [" + i + "] content=" + content);
                }
            } else if (message.getContent() instanceof String) {
                String body = (String) message.getContent();
                LOGGER.info("EMailSender: logMessageContent - body=" + body);
            }
        } catch (MessagingException | IOException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        try {
            MultiValuedMap<MimeType, Attachment> attachments = new ArrayListValuedHashMap<>();
            byte[] byteData = IOUtils.toByteArray(FileUtil.getReportName("lineBreaksData.txt"));
            attachments.put(MimeType.TXT, new Attachment("lineBreaksData", byteData));
            attachments.put(MimeType.TXT, new Attachment("lineBreaksData", byteData));

            sendEmail(destination, "test subject", "test body", attachments);
        } catch (MessagingException e) {
            LOGGER.severe(e.getMessage());
            e.printStackTrace();
        }
    }
}
