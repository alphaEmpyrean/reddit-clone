package com.etherealhazel.redditclone.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etherealhazel.redditclone.dto.AuthenticationResponse;
import com.etherealhazel.redditclone.dto.LoginRequest;
import com.etherealhazel.redditclone.dto.RefreshTokenRequest;
import com.etherealhazel.redditclone.dto.RegisterRequest;
import com.etherealhazel.redditclone.exception.SpringRedditException;
import com.etherealhazel.redditclone.model.NotificationEmail;
import com.etherealhazel.redditclone.model.User;
import com.etherealhazel.redditclone.model.VerificationToken;
import com.etherealhazel.redditclone.repository.UserRepository;
import com.etherealhazel.redditclone.repository.VerificationTokenRepository;
import com.etherealhazel.redditclone.security.JwtProvider;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {
	
	private final RefreshTokenService refreshTokenService;
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final VerificationTokenRepository verificationTokenRepository;
	private final MailService mailService;
	private final AuthenticationManager authenticationManager;
	private final JwtProvider jwtProvider;

	@Transactional
	public void signup(RegisterRequest registerRequest) {
		
		//Build user
		User user = new User();
		user.setUsername(registerRequest.getUsername());
		user.setEmail(registerRequest.getEmail());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setCreated(Instant.now());
		user.setEnabled(false);
		
		//Save user
		userRepository.save(user);
		
		//Generate activation token
		String token = generateVerificationToken(user);
		
		//Send activation link with tokenized link
		mailService.sendMail(new NotificationEmail("Please activate your accout",user.getEmail(), 
				"Thank you for signing up to Spring Reddit, " +
				"please click on the below url to activate your account: " +
						"http://localhost:8080/api/auth/accountVerification/" + token));
	}

	private String generateVerificationToken(User user) {
		String token = UUID.randomUUID().toString();
		VerificationToken verificationToken = new VerificationToken();
		verificationToken.setToken(token);
		verificationToken.setUser(user);
		
		verificationTokenRepository.save(verificationToken);
		return token;
		
	}

	public void verifyAccount(String token) {
		Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
		verificationToken.orElseThrow(() -> new SpringRedditException("Invalid Token"));
		fetchUserAndEnable(verificationToken.get());
		
	}

	@Transactional
	private void fetchUserAndEnable(VerificationToken verificationToken) {
		String username = verificationToken.getUser().getUsername();
		User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditException("User not found: " + username));
		user.setEnabled(true);
		userRepository.save(user);
	}

	public AuthenticationResponse login(LoginRequest loginRequest) {
		Authentication authenticate = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authenticate);
		String token = jwtProvider.generateToken(authenticate);
		
		return AuthenticationResponse.builder()
			.authenticationToken(token)
			.refreshToken(refreshTokenService.generateRefreshToken().getToken())
			.expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
			.username(loginRequest.getUsername())
			.build();
	}

	public User getCurrentUser() {
		Jwt principal = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userRepository.findByUsername(principal.getSubject())
			.orElseThrow(() -> new SpringRedditException("User not found with name -" + principal.getSubject()));
	}

    public AuthenticationResponse refreshToken(@Valid RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken((refreshTokenRequest.getRefreshToken()));
		String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());

		return AuthenticationResponse.builder()
		.authenticationToken(token)
		.refreshToken(refreshTokenRequest.getRefreshToken())
		.expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
		.username(refreshTokenRequest.getUsername())
		.build();
    }

	public boolean isLoggedIn() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
	}
}
