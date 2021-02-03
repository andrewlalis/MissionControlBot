package util;

import java.io.IOException;
import java.util.Properties;

/**
 * Singleton that contains the application's properties loaded once on startup.
 */
public class AppProperties {
	private static AppProperties instance;

	public static AppProperties getInstance() {
		if (instance == null) {
			try {
				instance = new AppProperties();
			} catch (IOException e) {
				throw new RuntimeException("Could not initialize application properties singleton.", e);
			}
		}
		return instance;
	}


	private final Properties properties;

	private AppProperties() throws IOException {
		this.properties = new Properties();
		this.properties.load(getClass().getClassLoader().getResourceAsStream("app.properties"));
	}

	public String get(String key) {
		return this.properties.getProperty(key);
	}
}
