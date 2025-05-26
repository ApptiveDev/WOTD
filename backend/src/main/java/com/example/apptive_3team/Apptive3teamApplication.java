package com.example.apptive_3team;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Apptive3teamApplication {

	public static void main(String[] args) {
		// .env 파일 로드
		Dotenv dotenv = Dotenv.load();

		// 로드된 값을 환경 변수로 설정
		System.setProperty("WEATHER_API_KEY", dotenv.get("WEATHER_API_KEY"));
		System.setProperty("WEATHER_API_URL", dotenv.get("WEATHER_API_URL"));

		SpringApplication.run(Apptive3teamApplication.class, args);
	}

}
