package com.imperva.stepping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Gabi Beyo on 16/09/2015.
 */
class SteppingProperties extends Properties {
    private final Logger LOGGER = LoggerFactory.getLogger(SteppingProperties.class.getName());
    private String propertiesRelativeFileLocation = "stepping.properties";
    private static SteppingProperties SteppingProperties; //* todo: should be ok as it dose not keep state. To check!

    static synchronized SteppingProperties getInstance() {
        if (SteppingProperties == null) {
            SteppingProperties = new SteppingProperties();
            return SteppingProperties;
        }
        return SteppingProperties;
    }

    private SteppingProperties() {
        super();
        try (InputStream inputStream = SteppingProperties.class.getClassLoader().getResourceAsStream(propertiesRelativeFileLocation)){
            this.load(inputStream);
        } catch (Exception e) {
            LOGGER.error("Failed loading properties", e);
        }
    }
}
