package com.BharatCrypto.service;

import com.BharatCrypto.config.JwtProvider;
import com.BharatCrypto.domain.VerificationType;
import com.BharatCrypto.domain.UserStatus;
import com.BharatCrypto.exception.UserException;
import com.BharatCrypto.model.*;
import com.BharatCrypto.repository.UserRepository;
import com.BharatCrypto.repository.WatchlistRepository;
import com.BharatCrypto.repository.WalletRepository;
import com.BharatCrypto.request.SignUpRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private WatchlistRepository watchlistRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public User findUserProfileByJwt(String jwt) throws UserException {
        String email = JwtProvider.getEmailFromJwtToken(jwt);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserException("user not exist with email " + email);
        }
        return user;
    }

    @Override
    public User findUserByEmail(String email) throws UserException {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return user;
        }
        throw new UserException("user not exist with email " + email);
    }

    @Override
    public User findUserById(Long userId) throws UserException {
        Optional<User> opt = userRepository.findById(userId);
        if (opt.isEmpty()) {
            throw new UserException("user not found with id " + userId);
        }
        return opt.get();
    }

    @Override
    @Transactional
    public User registerUser(SignUpRequest req) throws UserException {
        try {
            System.out.println("📝 Registering new user: " + req.getEmail());
            
            User user = new User();
            user.setEmail(req.getEmail());
            user.setFullName(req.getFullName());
            user.setPassword(passwordEncoder.encode(req.getPassword()));
            user.setStatus(UserStatus.PENDING);
            user.setVerified(false);


            User savedUser = userRepository.save(user);
            System.out.println("✅ User saved with ID: " + savedUser.getId());


            Watchlist watchlist = new Watchlist();
            watchlist.setUser(savedUser);
            watchlist.setCoins(new ArrayList<>());
            Watchlist savedWatchlist = watchlistRepository.save(watchlist);
            System.out.println("✅ Watchlist created with ID: " + savedWatchlist.getId());


            Wallet wallet = new Wallet();
            wallet.setUser(savedUser);
            wallet.setBalance(BigDecimal.ZERO);
            Wallet savedWallet = walletRepository.save(wallet);
            System.out.println("Wallet created with ID: " + savedWallet.getId());

            return savedUser;
        } catch (Exception e) {
            System.err.println(" Error registering user: " + e.getMessage());
            throw new UserException("Error registering user: " + e.getMessage());
        }
    }

    @Override
    public User verifyUser(User user) throws UserException {
        return userRepository.save(user);
    }

    @Override
    public User enabledTwoFactorAuthentication(VerificationType verificationType, String sendTo, User user) throws UserException {
        return userRepository.save(user);
    }

    @Override
    public User updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    @Override
    public User updateUserProfile(String jwt, User user) throws UserException {
        return userRepository.save(user);
    }

    @Override
    public void sendUpdatePasswordOtp(String email, String otp) {
        // Implementation here
    }

    @Override
    public User updateUserProfileImage(String jwt, String base64Image) throws UserException {
        User user = findUserProfileByJwt(jwt);
        user.setPicture(base64Image);
        return userRepository.save(user);
    }
}
