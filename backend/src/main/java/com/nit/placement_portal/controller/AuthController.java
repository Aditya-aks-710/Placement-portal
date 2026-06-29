package com.nit.placement_portal.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.nit.placement_portal.model.ActivationToken;
import com.nit.placement_portal.model.User;
import com.nit.placement_portal.dto.RegisterRequest;
import com.nit.placement_portal.exception.BadRequestException;
import com.nit.placement_portal.exception.ResourceNotFoundException;
import com.nit.placement_portal.exception.UnauthorizedException;
import com.nit.placement_portal.repository.ActivationTokenRepository;
import com.nit.placement_portal.repository.StudentRepository;
import com.nit.placement_portal.repository.UserRepository;
import com.nit.placement_portal.security.JwtUtil;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final ActivationTokenRepository tokenRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    @Value("${app.registration.expose-token:false}")
    private boolean exposeActivationToken;

    public AuthController(UserRepository userRepository,
                        StudentRepository studentRepository,
                        ActivationTokenRepository tokenRepository,
                        BCryptPasswordEncoder encoder,
                        JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.tokenRepository = tokenRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

   @PostMapping("/initiate-registration")
   public Map<String, String> initiateRegistration(@RequestBody Map<String, String> request) {

        String regno = request.get("regno");

        if (regno == null || regno.isBlank()) {
            throw new BadRequestException("Registration number is required");
        }

        var studentOpt = studentRepository.findByRegno(regno);
        if(studentOpt.isEmpty()) {
            throw new ResourceNotFoundException("Student not found");
        }

        String tokenValue = UUID.randomUUID().toString();

        ActivationToken token = new ActivationToken();
        token.setRegno(regno);
        token.setToken(tokenValue);
        token.setExpiry(LocalDateTime.now().plusMinutes(15));
        token.setUsed(false);

        tokenRepository.save(token);

        logger.info("Activation link generated for user : {}", regno);
        logger.debug("Activaton Link: http://localhost:8080/api/auth/complete-registration?token={}", tokenValue);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Activation link generated");
        if (exposeActivationToken) {
            response.put("token", tokenValue);
        }
        return response;
   }

   @PostMapping("/complete-registration")
   public String completeRegistration(@RequestBody Map<String, String> request) {

        String tokenValue = request.get("token");
        String password = request.get("password");

        if (tokenValue == null || tokenValue.isBlank()) {
            throw new BadRequestException("Activation token is required");
        }
        if (password == null || password.length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters long");
        }

        ActivationToken token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new BadRequestException("Invalid Token"));
        
        if(token.getUsed()) {
            throw new BadRequestException("Token already used");
        }

        if (token.getExpiry() != null && token.getExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Activation token has expired");
        }

        var student = studentRepository.findByRegno(token.getRegno())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (userRepository.findByUsername(student.getRegNo()).isPresent()) {
            throw new BadRequestException("An account already exists for this student");
        }

        var user = new User();
        user.setUsername(student.getRegNo());
        user.setPassword(encoder.encode(password));
        user.setRole("STUDENT");
        user.setStudentId(student.getId());

        userRepository.save(user);

        token.setUsed(true);
        tokenRepository.save(token);

        return "Registration completed Successfully";
      }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody RegisterRequest request) {

        if (request.getUsername() == null || request.getPassword() == null) {
            throw new BadRequestException("Username and password are required");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));
        
        if(!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        return Map.of(
                "token", token,
                "username", user.getUsername(),
                "role", user.getRole()
        );
    }
    
}
