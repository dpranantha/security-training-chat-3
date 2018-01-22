package org.certifiedsecure.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatApplication {
	public static void main(String[] args) {
		String insecure = System.getenv("INSECURE");
		if (!"I_WANT_AN_INSECURE_SYSTEM".equals(insecure)) {
			System.out.println("Do not run this challenge source code on your system");
			return;
		}
		SpringApplication.run(ChatApplication.class, args);
	}
}
