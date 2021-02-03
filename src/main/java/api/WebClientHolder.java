package api;

import org.springframework.web.reactive.function.client.WebClient;

/**
 * Simple class that allows application-global access to a web client instance
 * for requesting data from the API.
 */
public class WebClientHolder {
	private static WebClient client;

	public static WebClient get() {
		if (client == null) {
			throw new RuntimeException("WebClient not initialized.");
		}
		return client;
	}

	public static void set(WebClient newClient) {
		client = newClient;
	}
}
