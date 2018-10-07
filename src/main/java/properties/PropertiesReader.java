package properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader extends Properties {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesReader.class.getName());
    private String propertiesRelativeFileLocation = "properties/algo-container.properties";
    private static PropertiesReader propertiesReader;

    public static void init(String externalPropertiesAbsoluteLocation) {
        if (propertiesReader == null) {
            propertiesReader = new PropertiesReader(externalPropertiesAbsoluteLocation);
        }
    }

    public static PropertiesReader getInstance() {
        if (propertiesReader == null) {
            throw new UnsupportedOperationException();
        }
        return propertiesReader;
    }

    private PropertiesReader(String externalPropertiesAbsoluteLocation) {
        super();
        try (InputStream inputStream = PropertiesReader.class.getClassLoader().getResourceAsStream(propertiesRelativeFileLocation)) {
            this.load(inputStream);
            if (externalPropertiesAbsoluteLocation != null && externalPropertiesAbsoluteLocation.trim().length() > 0) {
                try (InputStream inputStream1 = new FileInputStream(externalPropertiesAbsoluteLocation)) {
                    this.load(inputStream1);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load properties", e);
        }
    }

    public static void clearAll(){
        propertiesReader = null;
    }
}
