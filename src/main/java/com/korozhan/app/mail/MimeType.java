package com.korozhan.app.mail;

public enum MimeType {
    TXT("text/plain; charset=UTF-8"),
    HTML("text/html; charset=UTF-8"),
    PNG("image/png"),
    JPG("image/jpeg"),
    JPE("image/jpeg"),
    JPEG("image/jpeg"),
    TIFF("image/tiff"),
    TIF("image/tiff"),
    GIF("image/gif"),
    BMP("image/bmp"),
    MIXED("multipart/mixed"),
    PDF("application/pdf"),
    ZIP("application/zip"),
    DOC("application/msword"),
    DOCX("application/msword"),
    ODT("application/octet-stream"),
    BINARY("application/octet-stream");

    private String content;

    public String getContent() {
        return content;
    }

    private MimeType(String content){
        this.content = content;
    }

    public String extension() {
        return "." + this.name().toLowerCase();
    }
}
