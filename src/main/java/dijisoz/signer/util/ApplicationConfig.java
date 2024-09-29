package dijisoz.signer.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationConfig {
    private static final Properties properties = new Properties();
    public static String LOGIN_URL = "";
    public static String SOCKET_URL = "";
    public static String GET_DOCUMENT_URL = "";
    public static String UPLOAD_SIGNED_DOCUMENT_URL = "";

    static {
        try (InputStream input = ApplicationConfig.class.getClassLoader().getResourceAsStream("configs//application.properties")) {
            if (input == null) {
                throw new IllegalStateException("Unable to find config.properties file");
            }
            properties.load(input);
            LOGIN_URL = properties.getProperty("LOGIN_URL");
            SOCKET_URL = properties.getProperty("SOCKET_URL");
            GET_DOCUMENT_URL = properties.getProperty("GET_DOCUMENT_URL");
            UPLOAD_SIGNED_DOCUMENT_URL = properties.getProperty("UPLOAD_SIGNED_DOCUMENT_URL");
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load config.properties file", ex);
        }       
    }
}
