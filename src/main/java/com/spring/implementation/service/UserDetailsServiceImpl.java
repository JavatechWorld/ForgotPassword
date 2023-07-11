package com.spring.implementation.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.spring.implementation.DTO.UserDTO;
import com.spring.implementation.model.PasswordResetToken;
import com.spring.implementation.model.User;
import com.spring.implementation.repository.TokenRepository;
import com.spring.implementation.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	
	@Autowired
	UserRepository userRepository;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	@Autowired
	JavaMailSender javaMailSender;
	
	@Autowired
	TokenRepository tokenRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
				new HashSet<GrantedAuthority>());
	}


	public User save(UserDTO userDTO) {
		User user = new User();
		user.setEmail(userDTO.getEmail());
		user.setName(userDTO.getName());
		user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		return userRepository.save(user);
	}


	public String sendEmail(User user) {
		try {
			String resetLink = generateResetToken(user);

			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setFrom("");// input the senders email ID
			msg.setTo(user.getEmail());

			msg.setSubject("Welcome To My Channel");
			msg.setText("Hello \n\n" + "Please click on this link to Reset your Password :" + resetLink + ". \n\n"
					+ "Regards \n" + "ABC");

			javaMailSender.send(msg);

			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}

	}


	public String generateResetToken(User user) {
		UUID uuid = UUID.randomUUID();
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDateTime expiryDateTime = currentDateTime.plusMinutes(30);
		PasswordResetToken resetToken = new PasswordResetToken();
		resetToken.setUser(user);
		resetToken.setToken(uuid.toString());
		resetToken.setExpiryDateTime(expiryDateTime);
		resetToken.setUser(user);
		PasswordResetToken token = tokenRepository.save(resetToken);
		if (token != null) {
			String endpointUrl = "http://localhost:8080/resetPassword";
			return endpointUrl + "/" + resetToken.getToken();
		}
		return "";
	}


	public boolean hasExipred(LocalDateTime expiryDateTime) {
		LocalDateTime currentDateTime = LocalDateTime.now();
		return expiryDateTime.isAfter(currentDateTime);
	}
}
