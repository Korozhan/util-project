package com.korozhan.app.util;

import com.jcraft.jsch.*;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Created by veronika.korozhan on 09.01.2018.
 */
public class SFTPHelper {
    private String host;
    private Integer port;
    private String user;
    private String password;

    public SFTPHelper(String host, Integer port, String user, String password) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public boolean configured() {
        return host != null && port != null && user != null && password != null;
    }

    public String sendData(InputStream data, String destination) {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp sftpChannel = null;
        Channel channel = null;
        try {
            session = jsch.getSession(user, host, port);
            session.setPassword(password);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");

            session.setConfig(config);
            session.connect();
            System.out.println("Host connected.");

            channel = session.openChannel("sftp");
            channel.connect();
            System.out.println("SFTP channel opened and connected.");

            sftpChannel = (ChannelSftp) channel;
            sftpChannel.cd(destination);
            sftpChannel.setInputStream(data);
            System.out.println("File transfered to host successfully.");
            return "OK";
        } catch (Exception e) {
            System.out.println("Exception found while tranfer the response." + e);
        } finally {
            if (sftpChannel != null) {
                sftpChannel.exit();
                System.out.println("SFTP channel exited.");
            }
            if (channel != null) {
                channel.disconnect();
                System.out.println("Channel disconnected.");
            }
            if (session != null) {
                session.disconnect();
                System.out.println("Session host disconnected.");
            }
        }
        return "ERROR";
    }

    private File getFile(String sourcePath) {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp sftpChannel = null;
        Channel channel = null;
        try {
            session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            System.out.println("Establishing Connection...");
            session.connect();
            System.out.println("Connection established.");

            System.out.println("Creating SFTP Channel.");
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            System.out.println("SFTP Channel created.");
            InputStream out = null;
            out = sftpChannel.get(sourcePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(out));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            File sourceFile = File.createTempFile("filled", ".txt");
            IOUtils.write(builder, new FileOutputStream(sourceFile), StandardCharsets.UTF_8);

            return sourceFile;
        } catch (JSchException | SftpException | IOException e) {
            System.out.println(e);
        } finally {
            if (sftpChannel != null) {
                sftpChannel.exit();
                System.out.println("SFTP channel exited.");
            }
            if (channel != null) {
                channel.disconnect();
                System.out.println("Channel disconnected.");
            }
            if (session != null) {
                session.disconnect();
                System.out.println("Session host disconnected.");
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 0;
        String user = "user";
        String password = "password";

        SFTPHelper helper = new SFTPHelper(host, port, user, password);
        try {
            helper.sendData(new FileInputStream(File.createTempFile("filled", ".txt")), "/opt/in");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

