package com.BharatCrypto.service;


import com.BharatCrypto.domain.VerificationType;
import com.BharatCrypto.exception.UserException;
import com.BharatCrypto.model.User;
import com.BharatCrypto.request.SignUpRequest;


public interface UserService {

	public User findUserProfileByJwt(String jwt) throws UserException;
	
	public User findUserByEmail(String email) throws UserException;
	
	public User findUserById(Long userId) throws UserException;

	public User verifyUser(User user) throws UserException;

	public User enabledTwoFactorAuthentication(VerificationType verificationType,
											   String sendTo, User user) throws UserException;

	public User updatePassword(User user, String newPassword);

	public User updateUserProfile(String jwt, User user) throws UserException;

	public void sendUpdatePasswordOtp(String email,String otp);

	public User updateUserProfileImage(String jwt, String base64Image) throws UserException;
    User registerUser(SignUpRequest req) throws UserException;
}
