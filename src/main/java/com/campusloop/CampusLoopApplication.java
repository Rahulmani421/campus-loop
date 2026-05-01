package com.campusloop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.campusloop.model.Category;
import com.campusloop.model.Condition;
import com.campusloop.model.Product;
import com.campusloop.model.User;
import com.campusloop.service.ProductService;
import com.campusloop.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CampusLoopApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampusLoopApplication.class, args);
	}

	@Bean
	public CommandLineRunner dataLoader(UserService userService, ProductService productService) {
		return args -> {
			// Check if a user already exists
			if (userService.findByUsername("rahul_student").isEmpty()) {
				// Seed Dummy User
				User testUser = User.builder()
						.username("rahul_student")
						.email("rahul@college.edu")
						.password("password123")
						.fullName("Rahul")
						.hostelBlock("Block A")
						.department("Computer Science")
						.role("ROLE_USER")
						.verified(true)
						.build();
				
				// Register encodes the password inside the service
				userService.registerUser(testUser);

				// Seed Admin User
				User adminUser = User.builder()
						.username("admin_campus")
						.email("admin@college.edu")
						.password("admin123")
						.fullName("Admin Head")
						.role("ROLE_ADMIN")
						.verified(true)
						.build();
				userService.registerUser(adminUser);

				// Seed Dummy Item
				Product p1 = Product.builder()
						.title("Engineering Mathematics 3")
						.description("Used it for last semester. Good condition.")
						.category(Category.BOOKS)
						.price(300.0)
						.itemCondition(Condition.GOOD)
						.location("Library Gate")
						.seller(userService.findByUsername("rahul_student").get())
						.sold(false)
						.build();
				productService.saveProduct(p1);
			}
		};
	}
}
