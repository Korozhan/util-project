package com.korozhan.app;

import com.github.napp.kaptcha.Kaptcha;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by veronika.korozhan on 13.10.2017.
 */
public class CaptchaService {

    private static CaptchaService instance;
    private static Map<String, String> captchas;


    public static CaptchaService getInstance() {
        if (instance == null) {
            instance = new CaptchaService();
            captchas = new HashMap<String, String>();
        }
        return instance;
    }

    public static byte[] newCaptcha(String id) throws IOException {
        String encryptedText = String.valueOf(new Random().nextInt(99999999));

        Kaptcha kaptcha = new Kaptcha();
        ByteArrayOutputStream imgOutputStream = new ByteArrayOutputStream();
        BufferedImage captchaImage = kaptcha.createImage(encryptedText);
        ImageIO.write(captchaImage, "png", imgOutputStream);
        byte[] captchaBytes = imgOutputStream.toByteArray();

        captchas.put(id, encryptedText);
        return captchaBytes;
    }

    public boolean validateCaptcha(String id, String inputChars) {
        String encryptedText = captchas.get(id);
        if (encryptedText != null) {
            captchas.remove(id);
            return encryptedText.equals(inputChars);
        }
        return false;
    }

}
