package keylivery;

import java.io.*;
import java.util.Properties;

import static keylivery.AppPreferences.Preference.GPGPATH_STR;
import static keylivery.AppPreferences.Preference.PORT_INT;

public class AppPreferences {

    private static AppPreferences preferencesInstance;

    private final String propertiesFile = "keylivery.properties";
    private final String _GPGPATH_STR = "/usr/local/bin/gpg";
    private final String _PORT_INT = "4711";

    private Properties properties;

    private AppPreferences() {
        this.properties = new Properties();
        properties.setProperty(GPGPATH_STR.name(), properties.getProperty(GPGPATH_STR.name(), _GPGPATH_STR));
        properties.setProperty(PORT_INT.name(), properties.getProperty(PORT_INT.name(), _PORT_INT));
        init();
    }

    public static AppPreferences getInstance() {
        if (preferencesInstance == null) {
            preferencesInstance = new AppPreferences();
        }
        return preferencesInstance;
    }

    private void init() {
        try (InputStream inputStream = new FileInputStream(propertiesFile)) {
            properties.load(inputStream);
        } catch (IOException e) {
            System.err.println("Property File missing");
        }
        try (OutputStream outputStream = new FileOutputStream(propertiesFile)) {
            properties.store(outputStream, "Keylivery Properties File");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getInt(Preference preference) {
        return Integer.valueOf(properties.getProperty(preference.name()));
    }

    public String getString(Preference preference) {
        return properties.getProperty(preference.name());
    }

    public enum Preference {
        GPGPATH_STR,
        PORT_INT
    }
}
