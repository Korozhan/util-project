package com.korozhan.app.auth;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;


public class GoogleAuthenticator {

    public static String getRandomSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        String secretKey = base32.encodeToString(bytes);
        return secretKey.toLowerCase().replaceAll("(.{4})(?=.{4})", "$1 ");
    }

    public static void generateQRCode(String secretKey, String account, String issuer) throws IOException, com.google.zxing.WriterException {
        String barCodeData = getGoogleAuthenticatorBarCode(secretKey, account, issuer);
        BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE, 400, 400);
        try (FileOutputStream out = new FileOutputStream("images/QRCode.png")) {
            MatrixToImageWriter.writeToStream(matrix, "png", out);
        }
    }

    public static String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) throws UnsupportedEncodingException {
        String normalizedBase32Key = secretKey.replace(" ", "").toUpperCase();

        return "otpauth://totp/"
                + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
                + "?secret=" + URLEncoder.encode(normalizedBase32Key, "UTF-8").replace("+", "%20")
                + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
    }

    public static String getTOTPCode(String secretKey) {
        String normalizedBase32Key = secretKey.replace(" ", "").toUpperCase();
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(normalizedBase32Key);
        String hexKey = Hex.encodeHexString(bytes);
        long time = (System.currentTimeMillis() / 1000) / 30;
        String hexTime = Long.toHexString(time);
        return TOTP.generateTOTP(hexKey, hexTime, "6");
    }

    public static void main(String[] args) throws IOException, WriterException {
        String secret = getRandomSecretKey();
        System.out.println(secret);

        generateQRCode(secret, "account", "issuer");
        System.out.println(getTOTPCode(secret));
    }
}
