package com.dangphuoctai.BookStore.service.impl;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dangphuoctai.BookStore.config.AppConstants;
import com.dangphuoctai.BookStore.entity.Address;
import com.dangphuoctai.BookStore.entity.Cart;
import com.dangphuoctai.BookStore.entity.OTP;
import com.dangphuoctai.BookStore.entity.Role;
import com.dangphuoctai.BookStore.entity.User;
import com.dangphuoctai.BookStore.enums.AccountType;
import com.dangphuoctai.BookStore.exceptions.APIException;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.UserRegister;
import com.dangphuoctai.BookStore.payloads.dto.OtpDTO;
import com.dangphuoctai.BookStore.payloads.dto.UserDTO;
import com.dangphuoctai.BookStore.repository.AddressRepo;
import com.dangphuoctai.BookStore.repository.OTPRepo;
import com.dangphuoctai.BookStore.repository.RoleRepo;
import com.dangphuoctai.BookStore.repository.UserRepo;
import com.dangphuoctai.BookStore.service.AuthService;
import com.dangphuoctai.BookStore.service.FileService;
import com.dangphuoctai.BookStore.utils.EmailValidator;

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
    private OTPRepo otpRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    public UserDTO registerUser(UserRegister userRegister) {
        try {
            if (EmailValidator.isValidEmail(userRegister.getUsername())) {
                throw new APIException("Invalid username");
            }
            if (userRegister.getPassword() == null || userRegister.getPassword().isBlank()) {
                throw new APIException("Password is not Valid");
            }
            Optional<User> userOptional = userRepo.findByEmail(userRegister.getEmail());
            User user = null;
            if (userOptional.isPresent()) {
                user = userOptional.get();
                if (user.getVerified()) {
                    throw new APIException("User already exists and is verified");
                }
            }
            String encodedPass = passwordEncoder.encode(userRegister.getPassword());
            Role role = roleRepo.findById(AppConstants.USER_ID).get();
            Cart cart = new Cart();
            user = user != null ? user : new User();
            user.setUsername(userRegister.getUsername());
            user.setEmail(userRegister.getEmail());
            user.setPassword(encodedPass);
            user.setRoles(Set.of(role));
            cart.setUser(user);
            user.setCart(cart);
            user.setFullName(userRegister.getFullName());
            user.setMobileNumber(userRegister.getMobileNumber());
            user.setAvatar("default.png");
            user.setVerified(false);
            user.setAccountType(AccountType.USER);

            String country = userRegister.getAddress().getCountry();
            String district = userRegister.getAddress().getDistrict();
            String city = userRegister.getAddress().getCity();
            String pincode = userRegister.getAddress().getPincode();
            String ward = userRegister.getAddress().getWard();
            String buildingName = userRegister.getAddress().getBuildingName();
            Address address = addressRepo
                    .findByCountryAndDistrictAndCityAndPincodeAndWardAndBuildingName(
                            country, district,
                            city, pincode, ward, buildingName);
            if (address == null) {
                address = new Address(country, district, city, pincode, ward, buildingName);
                address = addressRepo.save(address);
            }
            user.setAddresses(Set.of(address));
            user.setCreatedAt(LocalDateTime.now());
            User registeredUser = userRepo.save(user);

            return modelMapper.map(registeredUser, UserDTO.class);
        } catch (Exception e) {
            throw new APIException("User already exists with emailId: " +
                    userRegister.getEmail() + " And " + e.getMessage());
        }
    }

    @Override
    public UserDTO loginUser(String username, String password) {
        if (password == null) {
            throw new APIException("Password is not Valid");
        }
        User user;
        if (EmailValidator.isValidEmail(username)) {
            user = userRepo.findByEmail(username)
                    .orElseThrow(() -> new ResourceNotFoundException("user", "email", username));
        } else {
            user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("user", "username", username));
        }

        boolean authentication = passwordEncoder.matches(password, user.getPassword());
        if (!authentication) {
            throw new APIException("Invalid password");
        }
        if (user.getEnabled() == false) {
            throw new AccessDeniedException("Account has been locked");
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
                throw new AccessDeniedException("Account has been disabled");
            }
            return modelMapper.map(user, UserDTO.class);
        }
        user = new User();
        Role role = roleRepo.findById(AppConstants.USER_ID).get();
        Cart cart = new Cart();
        user.setEmail(userDTO.getEmail());
        user.setRoles(Set.of(role));
        cart.setUser(user);
        user.setCart(cart);
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
    public String generateOTPEmail(String email) {
        Optional<OTP> optionalOtp = otpRepo.findByEmail(email);
        OTP otp;
        if (optionalOtp.isPresent()) {
            otp = optionalOtp.get();
        } else {
            otp = new OTP();
            otp.setEmail(email);
        }
        SecureRandom secureRandom = new SecureRandom();
        int code = secureRandom.nextInt(90000) + 10000;
        String strOTP = String.valueOf(code);
        otp.setCode(strOTP);
        otp.setExpiryDate(Instant.now().plus(5, ChronoUnit.MINUTES));
        otpRepo.save(otp);
        return strOTP;
    }

    @Override
    public Boolean verityOTPEmail(OtpDTO otpDTO) {
        User user = userRepo.findByEmail(otpDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", otpDTO.getEmail()));
        OTP otp = otpRepo.findByEmail(otpDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("OTP", "email", otpDTO.getEmail()));
        if (!otp.getCode().equals(otpDTO.getCode())) {
            return false;
        }
        if (Instant.now().isAfter(otp.getExpiryDate())) {
            return false;
        }
        user.setVerified(true);
        otpRepo.delete(otp);
        return true;
    }
}
