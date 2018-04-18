package com.korozhan.app.mail;

/**
 * Created by veronika.korozhan on 20.09.2017.
 */
public class Attachment {
    private String fileName;
    private byte[] bytes;

    public Attachment(String fileName, byte[] bytes) {
        this.fileName = fileName;
        this.bytes = bytes;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
