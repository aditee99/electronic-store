package com.lcwd.electroic.store;

import com.lcwd.electroic.store.entities.Role;
import com.lcwd.electroic.store.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.UUID;

@SpringBootApplication
@EnableWebMvc
public class ElectronicStoreApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ElectronicStoreApplication.class, args);
	}
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private RoleRepository roleRepository;
	@Value("${admin.role.id}")
	String roleAdminId;
	@Value("${normal.role.id}")
	String roleNormalId;

	@Override
	public void run(String... args) throws Exception {
		System.out.println(passwordEncoder.encode("tsdfsdt73"));
		try{
			Role roleAdmin = Role.builder().roleId(roleAdminId).roleName("ROLE_ADMIN").build();
			Role roleNormal = Role.builder().roleId(roleNormalId).roleName("ROLE_NORMAL").build();
			roleRepository.save(roleAdmin);
			roleRepository.save(roleNormal);
		}catch(Exception e){

		}
	}
}
