package com.revpay.user_service.service;

import com.revpay.user_service.dto.*;
import com.revpay.user_service.entity.User;
import com.revpay.user_service.enums.BusinessVerificationStatus;
import com.revpay.user_service.enums.UserRole;
import com.revpay.user_service.exception.UserAlreadyExistsException;
import com.revpay.user_service.repository.UserRepository;
import com.revpay.user_service.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger =
            LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public UserResponse register(RegisterRequest request) {
        logger.info("Registering new user with email: {}",
                request.getEmail());

        // Check email unique
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Email already exists: {}",
                    request.getEmail());
            throw new UserAlreadyExistsException(
                    "Email already registered: " + request.getEmail());
        }

        // Check phone unique
        if (userRepository.existsByPhone(request.getPhone())) {
            logger.warn("Phone already exists: {}",
                    request.getPhone());
            throw new UserAlreadyExistsException(
                    "Phone already registered: " + request.getPhone());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(
                passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(UserRole.valueOf(request.getRole()));
        user.setSecurityQuestion(request.getSecurityQuestion());
        user.setSecurityAnswer(
                passwordEncoder.encode(
                        request.getSecurityAnswer().toLowerCase()));

        // Business specific validations
        if (UserRole.valueOf(request.getRole()) == UserRole.BUSINESS) {
            logger.info("Validating business fields for: {}",
                    request.getEmail());

            // Check taxId unique
            if (request.getTaxId() != null &&
                    userRepository.existsByTaxId(request.getTaxId())) {
                logger.warn("TaxId already exists: {}",
                        request.getTaxId());
                throw new UserAlreadyExistsException(
                        "Tax ID already registered: " + request.getTaxId());
            }

            // Check businessContact unique
            // BUT allow if businessContact == own phone number
            if (request.getBusinessContact() != null) {
                boolean contactTakenByOthers =
                        userRepository.existsByBusinessContact(
                                request.getBusinessContact());

                if (contactTakenByOthers) {
                    logger.warn("Business contact already exists: {}",
                            request.getBusinessContact());
                    throw new UserAlreadyExistsException(
                            "Business contact already registered: "
                                    + request.getBusinessContact());
                }
            }

            user.setBusinessName(request.getBusinessName());
            user.setBusinessType(request.getBusinessType());
            user.setTaxId(request.getTaxId());
            user.setBusinessAddress(request.getBusinessAddress());
            user.setBusinessContact(request.getBusinessContact());
            user.setVerificationStatus(
                    BusinessVerificationStatus.PENDING);
        }

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with id: {}",
                savedUser.getId());

        return mapToUserResponse(savedUser);
    }

    public LoginResponse login(LoginRequest request) {
        logger.info("Login attempt for: {}",
                request.getEmailOrPhone());

        // Find by email or phone
        User user = userRepository
                .findByEmailOrPhone(
                        request.getEmailOrPhone(),
                        request.getEmailOrPhone())
                .orElseThrow(() -> {
                    logger.warn("Login failed - user not found: {}",
                            request.getEmailOrPhone());
                    return new RuntimeException(
                            "Invalid email/phone or password");
                });

        if (!passwordEncoder.matches(
                request.getPassword(), user.getPassword())) {
            logger.warn("Login failed - wrong password for: {}",
                    request.getEmailOrPhone());
            throw new RuntimeException(
                    "Invalid email/phone or password");
        }

        if (!user.isActive()) {
            logger.warn("Login failed - account deactivated: {}",
                    request.getEmailOrPhone());
            throw new RuntimeException("Account is deactivated");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getId()
        );

        logger.info("Login successful for user id: {}",
                user.getId());

        return new LoginResponse(
                token,
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                user.getId()
        );
    }

    public UserResponse getUserById(Long id) {
        logger.debug("Fetching user by id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with id: {}", id);
                    return new RuntimeException(
                            "User not found with id: " + id);
                });
        return mapToUserResponse(user);
    }

    public UserResponse getUserByEmail(String email) {
        logger.debug("Fetching user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                        "User not found with email: " + email));
        return mapToUserResponse(user);
    }

    public UserResponse updateProfile(Long id,
                                      UpdateProfileRequest request) {
        logger.info("Updating profile for user id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "User not found with id: " + id));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new UserAlreadyExistsException(
                        "Phone already in use: " + request.getPhone());
            }
            user.setPhone(request.getPhone());
        }

        if (user.getRole() == UserRole.BUSINESS) {
            if (request.getBusinessName() != null) {
                user.setBusinessName(request.getBusinessName());
            }
            if (request.getBusinessType() != null) {
                user.setBusinessType(request.getBusinessType());
            }
            if (request.getBusinessAddress() != null) {
                user.setBusinessAddress(request.getBusinessAddress());
            }

            // Validate businessContact unique on update
            if (request.getBusinessContact() != null) {
                boolean takenByOthers =
                        userRepository.existsByBusinessContact(
                                request.getBusinessContact());

                // Allow if it's their own current contact
                boolean isOwnContact = request.getBusinessContact()
                        .equals(user.getBusinessContact());

                if (takenByOthers && !isOwnContact) {
                    throw new UserAlreadyExistsException(
                            "Business contact already in use: "
                                    + request.getBusinessContact());
                }
                user.setBusinessContact(request.getBusinessContact());
            }
        }

        User updated = userRepository.save(user);
        logger.info("Profile updated for user id: {}", id);
        return mapToUserResponse(updated);
    }

    public void changePassword(Long id,
                               ChangePasswordRequest request) {
        logger.info("Changing password for user id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "User not found with id: " + id));

        if (!passwordEncoder.matches(
                request.getCurrentPassword(), user.getPassword())) {
            logger.warn("Password change failed - wrong current "
                    + "password for user id: {}", id);
            throw new RuntimeException(
                    "Current password is incorrect");
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        logger.info("Password changed successfully for user id: {}",
                id);
    }

    public void setPin(Long id, SetPinRequest request) {
        logger.info("Setting PIN for user id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "User not found with id: " + id));

        user.setTransactionPin(
                passwordEncoder.encode(request.getPin()));
        userRepository.save(user);
        logger.info("PIN set successfully for user id: {}", id);
    }

    public boolean verifyPin(Long id, String pin) {
        logger.debug("Verifying PIN for user id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "User not found with id: " + id));

        return passwordEncoder.matches(
                pin, user.getTransactionPin());
    }
    public SecurityQuestionCheckResponse getSecurityQuestion(
            SecurityQuestionRequest request) {
        logger.info("Fetching security question for: {}",
                request.getEmailOrPhone());

        User user = userRepository
                .findByEmailOrPhone(
                        request.getEmailOrPhone(),
                        request.getEmailOrPhone())
                .orElseThrow(() -> {
                    logger.warn("User not found for forgot password: {}",
                            request.getEmailOrPhone());
                    return new RuntimeException(
                            "No account found with that email or phone");
                });

        return new SecurityQuestionCheckResponse(
                request.getEmailOrPhone(),
                user.getSecurityQuestion().getQuestion()
        );
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        logger.info("Processing forgot password for: {}",
                request.getEmailOrPhone());

        User user = userRepository
                .findByEmailOrPhone(
                        request.getEmailOrPhone(),
                        request.getEmailOrPhone())
                .orElseThrow(() -> new RuntimeException(
                        "No account found with that email or phone"));

        // Verify security answer
        boolean answerMatches = passwordEncoder.matches(
                request.getSecurityAnswer().toLowerCase(),
                user.getSecurityAnswer()
        );

        if (!answerMatches) {
            logger.warn("Wrong security answer for: {}",
                    request.getEmailOrPhone());
            throw new RuntimeException(
                    "Security answer is incorrect");
        }

        // Reset password
        user.setPassword(
                passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        logger.info("Password reset successful for: {}",
                request.getEmailOrPhone());
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole().name());
        response.setVerified(user.isVerified());
        response.setActive(user.isActive());
        response.setCreatedAt(user.getCreatedAt());

        if (user.getRole() == UserRole.BUSINESS) {
            response.setBusinessName(user.getBusinessName());
            response.setBusinessType(user.getBusinessType());
            response.setTaxId(user.getTaxId());
            response.setBusinessAddress(user.getBusinessAddress());
            response.setBusinessContact(user.getBusinessContact());
            if (user.getVerificationStatus() != null) {
                response.setVerificationStatus(
                        user.getVerificationStatus().name());
            }
        }

        return response;
    }
}