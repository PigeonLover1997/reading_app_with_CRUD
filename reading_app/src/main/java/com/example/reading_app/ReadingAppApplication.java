package com.example.reading_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * このクラスは Spring Boot アプリケーションのエントリーポイントです。
 * 
 * @SpringBootApplication は以下の3つの機能をまとめて有効にします：
 * - @Configuration: このクラスが設定クラスであることを示す
 * - @EnableAutoConfiguration: Spring Boot が自動設定を有効にする
 * - @ComponentScan: 同じパッケージ内（およびその配下）のクラスを自動で探してDI（依存注入）対象とする
 * 
 * → この1つのアノテーションをつけるだけで、アプリ全体が起動可能になります。
 */
@SpringBootApplication
public class ReadingAppApplication {

	public static void main(String[] args) {
		// Spring Boot アプリケーションの起動
		SpringApplication.run(ReadingAppApplication.class, args);
	}

}
