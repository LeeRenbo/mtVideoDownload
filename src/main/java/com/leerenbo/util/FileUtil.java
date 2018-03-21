package com.leerenbo.util;

import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class FileUtil {
    public static OutputStream getStream(FileSystemResource fileSystemResource) throws IOException {
        fileSystemResource.getFile().getParentFile().mkdirs();
        if (!fileSystemResource.getFile().exists()) {
            fileSystemResource.getFile().createNewFile();
        }
        return fileSystemResource.getOutputStream();
    }

    public static File makeFile(FileSystemResource fileSystemResource) throws IOException {
        fileSystemResource.getFile().getParentFile().mkdirs();
        if (!fileSystemResource.getFile().exists()) {
            fileSystemResource.getFile().createNewFile();
        }
        return fileSystemResource.getFile();
    }

}
