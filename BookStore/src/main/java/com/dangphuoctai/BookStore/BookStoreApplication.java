package com.dangphuoctai.BookStore;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import com.dangphuoctai.BookStore.config.AppConstants;
import com.dangphuoctai.BookStore.entity.Role;
import com.dangphuoctai.BookStore.repository.RoleRepo;

@EnableScheduling
@SpringBootApplication
public class BookStoreApplication implements CommandLineRunner {

	@Autowired
	private RoleRepo roleRepo;

	public static void main(String[] args) {
		SpringApplication.run(BookStoreApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			Role adminRole = new Role();
			adminRole.setRoleId(AppConstants.ADMIN_ID);
			adminRole.setRoleName("ADMIN");

			Role userRole = new Role();
			userRole.setRoleId(AppConstants.USER_ID);
			userRole.setRoleName("USER");

			Role staffRole = new Role();
			staffRole.setRoleId(AppConstants.STAFF_ID);
			staffRole.setRoleName("STAFF");

			List<Role> roles = List.of(userRole, staffRole, adminRole);

			List<Role> savedRoles = roleRepo.saveAll(roles);

			savedRoles.forEach(System.out::println);

		} catch (Exception e) {
			e.printStackTrace();

		}
	}
}
