package com.example.laba;

import com.example.laba.services.InitializeDataBaseService;
import org.hibernate.SessionFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@EnableTransactionManagement
@SpringBootApplication
public class LabaApplication {

	public static void main(String[] args) {
		SpringApplication.run(LabaApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(InitializeDataBaseService initializeDataBaseService) {

		return args -> {
			initializeDataBaseService.initialize();
		};
	}

	@Bean
	DataSource dataSource() {
		return DataSourceBuilder.create()
				.driverClassName("org.h2.Driver")
				.url("jdbc:h2:mem:testdb")
				.build();
	}

	@Bean
	SessionFactory entityManagerFactory(DataSource dataSource) {
		LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(dataSource);
		builder.scanPackages("com.example.laba.entities");

		Properties properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto", "create");
		//properties.setProperty("hibernate.show_sql", "true");
		//properties.setProperty("hibernate.format_sql", "true");
		builder.addProperties(properties);
		return builder.buildSessionFactory();
	}
}
