package com.dangphuoctai.BookStore.service.impl;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dangphuoctai.BookStore.config.AppConstants;
import com.dangphuoctai.BookStore.entity.Address;
import com.dangphuoctai.BookStore.entity.RefreshToken;
import com.dangphuoctai.BookStore.entity.Role;
import com.dangphuoctai.BookStore.entity.User;
import com.dangphuoctai.BookStore.enums.AccountType;
import com.dangphuoctai.BookStore.enums.OTPType;
import com.dangphuoctai.BookStore.exceptions.APIException;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.dto.OtpDTO;
import com.dangphuoctai.BookStore.payloads.dto.UserDTO.UserDTO;
import com.dangphuoctai.BookStore.payloads.dto.UserDTO.UserRegister;
import com.dangphuoctai.BookStore.repository.AddressRepo;
import com.dangphuoctai.BookStore.repository.RefreshTokenRepo;
import com.dangphuoctai.BookStore.repository.RoleRepo;
import com.dangphuoctai.BookStore.repository.UserRepo;
import com.dangphuoctai.BookStore.security.JWTUtil;
import com.dangphuoctai.BookStore.service.AuthService;
import com.dangphuoctai.BookStore.service.BaseRedisService;
import com.dangphuoctai.BookStore.service.FileService;
import com.dangphuoctai.BookStore.utils.Email;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private AddressRepo addressRepo;

    @Autowired
    private RefreshTokenRepo refreshTokenRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileService fileService;

    @Autowired
    private JWTUtil jwtUtil;

    @Value("${project.image}")
    private String path;

    @Autowired
    private BaseRedisService<String, String, String> otpRedisService;

    private static final String OTP_USER_CACHE_KEY = "otp:user:";

    public UserDTO registerUser(UserRegister userRegister) {
        try {
            if (Email.isValidEmail(userRegister.getUsername())) {
                throw new APIException("Tên người dùng không hợp lệ");
            }
            if (userRegister.getPassword() == null || userRegister.getPassword().isBlank()) {
                throw new APIException("Mật khẩu không hợp lệ");
            }
            Optional<User> userOptional = userRepo.findByEmail(userRegister.getEmail());
            User user = null;
            if (userOptional.isPresent()) {
                user = userOptional.get();
                if (user.getVerified()) {
                    throw new APIException("Người dùng đã tồn tại và đã được xác thực");
                }
            }
            String encodedPass = passwordEncoder.encode(userRegister.getPassword());
            Role role = roleRepo.findById(AppConstants.USER_ID).get();
            user = user != null ? user : new User();
            user.setUsername(userRegister.getUsername());
            user.setEmail(userRegister.getEmail());
            user.setPassword(encodedPass);
            user.setRoles(Set.of(role));
            user.setFullName(userRegister.getFullName());
            user.setMobileNumber(userRegister.getMobileNumber());
            user.setAvatar("default.png");
            user.setVerified(false);
            user.setAccountType(AccountType.USER);
            if (userRegister.getAddress() != null) {
                String country = userRegister.getAddress().getCountry();
                String district = userRegister.getAddress().getDistrict();
                String city = userRegister.getAddress().getCity();
                String ward = userRegister.getAddress().getWard();
                String buildingName = userRegister.getAddress().getBuildingName();
                Address address = addressRepo
                        .findByCountryAndDistrictAndCityAndWardAndBuildingName(
                                country, district,
                                city, ward, buildingName);
                if (address == null) {
                    address = new Address(country, district, city, ward, buildingName);
                    address = addressRepo.save(address);
                }
                user.setAddress(address);
            }
            user.setCreatedAt(LocalDateTime.now());
            User registeredUser = userRepo.save(user);

            return modelMapper.map(registeredUser, UserDTO.class);
        } catch (Exception e) {
            throw new APIException("Người dùng đã tồn tại với email: " +
                    userRegister.getEmail() + " và lỗi: " + e.getMessage());
        }
    }

    @Override
    public UserDTO loginUser(String username, String password) {
        if (password == null) {
            throw new APIException("Mật khẩu không hợp lệ");
        }
        User user;
        if (Email.isValidEmail(username)) {
            user = userRepo.findByEmail(username)
                    .orElseThrow(() -> new ResourceNotFoundException("user", "email", username));
        } else {
            user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("user", "username", username));
        }

        boolean authentication = passwordEncoder.matches(password, user.getPassword());
        if (!authentication) {
            throw new APIException("Mật khẩu không đúng");
        }
        if (user.getEnabled() == false) {
            throw new AccessDeniedException("Tài khoản đã bị khóa");
        }
        if (user.getVerified() == false) {
            throw new AccessDeniedException("Tài khoản chưa được xác thực");
        }
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO loginGoogle(UserDTO userDTO) {
        Optional<User> userOptional = userRepo.findByEmail(userDTO.getEmail());
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (!user.getEnabled()) {
                throw new AccessDeniedException("Tài khoản đã bị khóa");
            }
            return modelMapper.map(user, UserDTO.class);
        }
        user = new User();
        Role role = roleRepo.findById(AppConstants.USER_ID).get();
        user.setEmail(userDTO.getEmail());
        user.setRoles(Set.of(role));
        user.setFullName(userDTO.getFullName());
        if (!userDTO.getAvatar().isBlank()) {
            String avatarFileName = fileService.downloadImageFromUrl(userDTO.getAvatar(), path);
            user.setAvatar(avatarFileName);
        } else {
            user.setAvatar("default.png");
        }
        user.setVerified(true);
        user.setEnabled(true);
        user.setAccountType(AccountType.GOOGLE);
        user.setCreatedAt(LocalDateTime.now());

        userRepo.save(user);

        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO getUserByRefreshToken(String refreshtoken) {
        RefreshToken refreshToken = refreshTokenRepo.findByToken(refreshtoken)
                .orElseThrow(() -> new ResourceNotFoundException("RefreshToken", "token",
                        refreshtoken));
        Instant expiryDate = refreshToken.getExpiryDate();
        if (Instant.now().isAfter(expiryDate)) {
            throw new BadCredentialsException("Refresh token đã hết hạn");
        }

        return modelMapper.map(refreshToken.getUser(), UserDTO.class);
    }

    @Transactional
    @Override
    public String generateRefreshToken(Long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        RefreshToken refreshToken = new RefreshToken();
        String token = jwtUtil.generateRefreshToken(userDTO);
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plus(3, ChronoUnit.DAYS));
        refreshTokenRepo.save(refreshToken);

        return token;

    }

    @Override
    public String generateOTPEmail(String email) {
        String key = OTP_USER_CACHE_KEY + email;
        SecureRandom secureRandom = new SecureRandom();
        int code = secureRandom.nextInt(900000) + 100000;
        String strOTP = String.valueOf(code);
        otpRedisService.set(key, strOTP);
        otpRedisService.setTimeToLive(key, 5, TimeUnit.MINUTES);

        return strOTP;
    }

    @Override
    public Boolean verityOTPEmail(OtpDTO otpDTO) {
        String key = OTP_USER_CACHE_KEY + otpDTO.getEmail();
        String otp = otpRedisService.get(key);
        User user = userRepo.findByEmail(otpDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", otpDTO.getEmail()));
        if (otp == null) {
            return false;
        }
        if (!otp.equals(otpDTO.getCode())) {
            return false;
        }
        user.setVerified(true);
        userRepo.save(user);
        otpRedisService.delete(key);

        return true;
    }
}
