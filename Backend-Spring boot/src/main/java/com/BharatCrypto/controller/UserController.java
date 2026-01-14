package com.BharatCrypto.controller;

import com.BharatCrypto.domain.VerificationType;
import com.BharatCrypto.exception.UserException;
import com.BharatCrypto.model.ForgotPasswordToken;
import com.BharatCrypto.model.User;
import com.BharatCrypto.request.UpdateUserRequest;
import com.BharatCrypto.model.VerificationCode;
import com.BharatCrypto.request.ResetPasswordRequest;
import com.BharatCrypto.request.UpdatePasswordRequest;
import com.BharatCrypto.response.ApiResponse;
import com.BharatCrypto.response.AuthResponse;
import com.BharatCrypto.service.EmailService;
import com.BharatCrypto.service.ForgotPasswordService;
import com.BharatCrypto.service.UserService;
import com.BharatCrypto.service.VerificationService;
import com.BharatCrypto.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.Map;

@RestController
public class UserController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/api/users/profile")
    public ResponseEntity<User> getUserProfileHandler(
    ) throws UserException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findUserByEmail(email);
        user.setPassword(null);

        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }

    @PutMapping("/api/users/profile/update")
    public ResponseEntity<User> updateUserProfileHandler(
            @RequestHeader("Authorization") String jwt,
            @RequestBody @Validated UpdateUserRequest req
            ) throws UserException {
        // 1. Fetch the existing user from the database using the JWT
        User user = userService.findUserProfileByJwt(jwt);

        // 2. Update only the fields that are present in the request
        if (req.getFullName() != null) user.setFullName(req.getFullName());
        if (req.getMobile() != null) user.setMobile(req.getMobile());
        if (req.getDateOfBirth() != null) user.setDateOfBirth(req.getDateOfBirth());
        if (req.getNationality() != null) user.setNationality(req.getNationality());
        if (req.getAddress() != null) user.setAddress(req.getAddress());
        if (req.getCity() != null) user.setCity(req.getCity());
        if (req.getPostcode() != null) user.setPostcode(req.getPostcode());
        if (req.getCountry() != null) user.setCountry(req.getCountry());

        User updatedUser = userService.updateUserProfile(jwt, user);
        updatedUser.setPassword(null);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }
    @PostMapping("/api/users/profile/upload-image")
    public ResponseEntity<User> uploadProfileImage(
            @RequestHeader("Authorization") String jwt,
            @RequestBody Map<String, String> request
    ) throws UserException {
        try {
            String base64Image = request.get("image");
            
            if (base64Image == null || base64Image.isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }

            User updatedUser = userService.updateUserProfileImage(jwt, base64Image);
            updatedUser.setPassword(null);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (UserException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/api/users/{userId}")
    public ResponseEntity<User> findUserById(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String jwt) throws UserException {

        User user = userService.findUserById(userId);
        user.setPassword(null);

        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }

    @GetMapping("/api/users/email/{email}")
    public ResponseEntity<User> findUserByEmail(
            @PathVariable String email,
            @RequestHeader("Authorization") String jwt) throws UserException {

        User user = userService.findUserByEmail(email);

        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }

    @PatchMapping("/api/users/enable-two-factor/verify-otp/{otp}")
    public ResponseEntity<User> enabledTwoFactorAuthentication(
            @RequestHeader("Authorization") String jwt,
            @PathVariable String otp
    ) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);

        VerificationCode verificationCode = verificationService.findUsersVerification(user);

        String sendTo = verificationCode.getVerificationType().equals(VerificationType.EMAIL) 
            ? verificationCode.getEmail() 
            : verificationCode.getMobile();

        boolean isVerified = verificationService.VerifyOtp(otp, verificationCode);

        if (isVerified) {
            User updatedUser = userService.enabledTwoFactorAuthentication(verificationCode.getVerificationType(),
                    sendTo, user);
            verificationService.deleteVerification(verificationCode);
            return ResponseEntity.ok(updatedUser);
        }
        throw new Exception("wrong otp");
    }

    @PatchMapping("/auth/users/reset-password/verify-otp")
    public ResponseEntity<ApiResponse> resetPassword(
            @RequestParam String id,
            @RequestBody ResetPasswordRequest req
            ) throws Exception {
        ForgotPasswordToken forgotPasswordToken = forgotPasswordService.findById(id);

        boolean isVerified = forgotPasswordService.verifyToken(forgotPasswordToken, req.getOtp());

        if (isVerified) {
            userService.updatePassword(forgotPasswordToken.getUser(), req.getPassword());
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setMessage("password updated successfully");
            return ResponseEntity.ok(apiResponse);
        }
        throw new Exception("wrong otp");
    }

    @PostMapping("/auth/users/reset-password/send-otp")
    public ResponseEntity<AuthResponse> sendUpdatePasswordOTP(
            @RequestBody UpdatePasswordRequest req)
            throws Exception {

        User user = userService.findUserByEmail(req.getSendTo());
        String otp = OtpUtils.generateOTP();
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();

        ForgotPasswordToken token = forgotPasswordService.findByUser(user.getId());

        if (token == null) {
            token = forgotPasswordService.createToken(
                    user, id, otp, req.getVerificationType(), req.getSendTo()
            );
        }

        if (req.getVerificationType().equals(VerificationType.EMAIL)) {
            emailService.sendVerificationOtpEmail(
                    user.getEmail(),
                    token.getOtp()
            );
        }

        AuthResponse res = new AuthResponse();
        res.setSession(token.getId());
        res.setMessage("Password Reset OTP sent successfully.");

        return ResponseEntity.ok(res);
    }

    @PatchMapping("/api/users/verification/verify-otp/{otp}")
    public ResponseEntity<User> verifyOTP(
            @RequestHeader("Authorization") String jwt,
            @PathVariable String otp
    ) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);

        VerificationCode verificationCode = verificationService.findUsersVerification(user);

        boolean isVerified = verificationService.VerifyOtp(otp, verificationCode);

        if (isVerified) {
            verificationService.deleteVerification(verificationCode);
            User verifiedUser = userService.verifyUser(user);
            return ResponseEntity.ok(verifiedUser);
        }
        throw new Exception("wrong otp");
    }

    @PostMapping("/api/users/verification/{verificationType}/send-otp")
    public ResponseEntity<String> sendVerificationOTP(
            @PathVariable VerificationType verificationType,
            @RequestHeader("Authorization") String jwt)
            throws Exception {

        User user = userService.findUserProfileByJwt(jwt);

        VerificationCode verificationCode = verificationService.findUsersVerification(user);

        if (verificationCode == null) {
            verificationCode = verificationService.sendVerificationOTP(user, verificationType);
        }

        if (verificationType.equals(VerificationType.EMAIL)) {
            emailService.sendVerificationOtpEmail(user.getEmail(), verificationCode.getOtp());
        }

        return ResponseEntity.ok("Verification OTP sent successfully.");
    }
}
