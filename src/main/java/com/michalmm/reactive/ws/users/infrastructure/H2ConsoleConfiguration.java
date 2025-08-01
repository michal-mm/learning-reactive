package com.michalmm.reactive.ws.users.infrastructure;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

import java.sql.SQLException;

import org.h2.tools.Server;

@Configuration
//@Profile({"dev", "test"})
@Profile("!prod & !production")
public class H2ConsoleConfiguration {

	private Server webServer;
	
	@EventListener(ApplicationStartedEvent.class)
	public void start() throws SQLException {
		String WEB_PORT = "8082";
		webServer.createWebServer("-webPort", WEB_PORT).start();
	}
	
	@EventListener(ContextClosedEvent.class)
	public void stop() {
		webServer.stop();
	}
}
