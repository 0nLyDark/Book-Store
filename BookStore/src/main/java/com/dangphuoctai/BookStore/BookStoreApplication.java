package com.dangphuoctai.BookStore;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.dangphuoctai.BookStore.config.AppConstants;
import com.dangphuoctai.BookStore.entity.Role;
import com.dangphuoctai.BookStore.entity.User;
import com.dangphuoctai.BookStore.enums.AccountType;
import com.dangphuoctai.BookStore.repository.RoleRepo;
import com.dangphuoctai.BookStore.repository.UserRepo;

@EnableScheduling
@SpringBootApplication
public class BookStoreApplication implements CommandLineRunner {

	@Autowired
	private RoleRepo roleRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

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

			if (!userRepo.existsByUsername("admin")) {
				User admin = new User();
				admin.setFullName("Admin");
				admin.setAccountType(AccountType.USER);
				admin.setEmail("admin@gmail.com");
				admin.setAvatar("default.png");
				admin.setEnabled(true);
				admin.setVerified(true);
				admin.setUsername("admin");
				String encodedPass = passwordEncoder.encode("adsads");
				admin.setPassword(encodedPass);
				admin.setCreatedAt(LocalDateTime.now());
				// Lấy role từ database thay vì tạo mới
				admin.getRoles().addAll(roleRepo.findAll());
				userRepo.save(admin);
			}
			savedRoles.forEach(System.out::println);
			System.out.println("username: " + "admin");
			System.out.println("password: " + "adsads");

		} catch (Exception e) {
			e.printStackTrace();

		}
	}
}
