package com.nit.placement_portal;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PlacementPortalApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		setIfPresent(dotenv, "SPRING_DATA_MONGODB_URI");
		setIfPresent(dotenv, "APP_JWT_SECRET");
		setIfPresent(dotenv, "APP_JWT_EXPIRATION_MS");
		setIfPresent(dotenv, "APP_BOOTSTRAP_ADMIN_ENABLED");
		setIfPresent(dotenv, "APP_BOOTSTRAP_ADMIN_USERNAME");
		setIfPresent(dotenv, "APP_BOOTSTRAP_ADMIN_PASSWORD");
		SpringApplication.run(PlacementPortalApplication.class, args);
	}

	private static void setIfPresent(Dotenv dotenv, String key) {
		String value = dotenv.get(key);
		if (value != null && !value.isBlank()) {
			System.setProperty(key, value);
		}
	}

}
