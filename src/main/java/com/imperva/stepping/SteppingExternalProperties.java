package com.imperva.stepping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Properties;

/**
 * Created by Gabi Beyo on 16/09/2015.
 */
public class SteppingExternalProperties extends Properties {
    private final Logger LOGGER = LoggerFactory.getLogger(SteppingExternalProperties.class.getName());
    private String fileLocation;
    private FileTime lastUpdated;

    SteppingExternalProperties(String filepath) {
        super();
        this.fileLocation = filepath;
        load();
    }

    @Override
    public Object get(Object val) {
        tryLoad();
        return super.get(val);
    }

    @Override
    public Object getOrDefault(Object val, Object other) {
        tryLoad();
        return super.getOrDefault(val, other);
    }

    @Override
    public String getProperty(String val, String other) {
        tryLoad();
        return super.getProperty(val, other);
    }


    @Override
    public String getProperty(String val) {
        tryLoad();
        return super.getProperty(val);
    }

    private void tryLoad() {
        try {

            FileTime fileTime = Files.getLastModifiedTime(Paths.get(fileLocation));
            if (lastUpdated == null || fileTime.compareTo(lastUpdated) > 0) {
                lastUpdated = fileTime;
                load();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void load() {
        try (InputStream inputStream = new FileInputStream(new File(fileLocation))) {
            this.load(inputStream);
        } catch (Exception e) {
            LOGGER.error("Failed loading properties", e);
            throw new SteppingException("Failed loading properties", e);
        }
    }
}



