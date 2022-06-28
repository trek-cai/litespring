package org.litespring.core.io;

import org.litespring.util.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileSystemResource implements Resource {

    private String filePath;
    private File file;

    public FileSystemResource(File file) {
        this.filePath = file.getPath();
        this.file = file;
    }
    public FileSystemResource(String filePath) {
        Assert.notNull(filePath, filePath + " must not be null");
        this.filePath = filePath;
        this.file = new File(filePath);
    }

    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

    public String getDescription() {
        return "file [" + file.getAbsolutePath() + "]";
    }
}
