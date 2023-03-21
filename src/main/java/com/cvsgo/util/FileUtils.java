package com.cvsgo.util;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface FileUtils {

    static Optional<String> getExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index == -1 || index + 1 >= fileName.length()) {
            return Optional.empty();
        }
        return Optional.of(fileName.substring(index + 1));
    }

    static String createFilePath(String dirName, String fileName) {
        StringBuilder filePath = new StringBuilder(dirName)
                .append("/")
                .append(LocalDate.now())
                .append("/")
                .append(UUID.randomUUID());
        getExtension(fileName).ifPresent(extension -> filePath.append(".").append(extension));
        return filePath.toString();
    }

}
