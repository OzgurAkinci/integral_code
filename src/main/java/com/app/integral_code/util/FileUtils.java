package com.app.integral_code.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class FileUtils {

    public FileUtils() {}

    public static String createTexFileAndConvertToBase64(String latexContent) throws IOException {
        // Create a temporary file with .tex extension
        File tempFile = File.createTempFile("tempfile", ".tex");

        // Write the LaTeX content to the file
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(latexContent);
        }

        // Read the file content and encode it in base64
        String encodedContent = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(tempFile.getAbsolutePath())));

        // Delete the temporary file
        tempFile.delete();

        return encodedContent;
    }

    public static Boolean isFileExist(Path path) {
        return path != null && Files.exists(path);
    }
}