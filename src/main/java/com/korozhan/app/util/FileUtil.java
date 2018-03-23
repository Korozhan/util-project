package com.korozhan.app.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.text.StrSubstitutor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Created by veronika.korozhan on 21.01.2018.
 */
public class FileUtil {

    public static FileInputStream fillWithParams(InputStream jsonStream, Map<String, String> questions, String fileExt) throws IOException {
        questions.forEach((key, value) -> {
            if (value == null) questions.replace(key, "");
        });
        String content = IOUtils.toString(jsonStream, StandardCharsets.UTF_8);
        File filledJson = File.createTempFile("filled", fileExt);

        StrSubstitutor sub = new StrSubstitutor(questions, "%(", ")");
        String replaced = sub.replace(content);
        System.out.println("*content=" + replaced);
        IOUtils.write(sub.replace(content), new FileOutputStream(filledJson), StandardCharsets.UTF_8);
        return new FileInputStream(filledJson);
    }

    public static void deleteUserDirectory(String path) {
        System.out.println("deleting...");
        if (deleteDirectory(new File(path)))
            System.out.println("deleteUserDirectory: temp user path=" + path + " successfully deleted");
        System.out.println("deleted");
    }

    public static boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDirectory(children[i]);
                if (!success) {
                    return false;
                }
            }
        }
        System.out.println("deleteUserDirectory: removing file or directory: " + dir.getName());
        return dir.delete();
    }

    public static InputStream getReportName(String fileName) {
        return FileUtil.class.getClassLoader().getResourceAsStream(fileName);
    }

    public static File getReportFile(String fileName) {
        return new File(FileUtil.class.getClassLoader().getResource(fileName).getFile());
    }
}
