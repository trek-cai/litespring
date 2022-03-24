package org.litespring.core.io;

import org.litespring.util.ClassUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ClassPathResource implements Resource {

    private ClassLoader classLoader;
    private String configFile;

    public ClassPathResource(String configFile) {
        this(configFile, null);

    }

    public ClassPathResource(String configFile, ClassLoader classLoader) {
        this.configFile = configFile;
        this.classLoader = classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader();
    }

    public InputStream getInputStream() throws IOException {
        InputStream is = this.classLoader.getResourceAsStream(configFile);
        if(is == null) {
            throw new FileNotFoundException(configFile + " cannot be opened.");
        }
        return is;
    }

    public String getDescription() {
        return configFile;
    }
}
