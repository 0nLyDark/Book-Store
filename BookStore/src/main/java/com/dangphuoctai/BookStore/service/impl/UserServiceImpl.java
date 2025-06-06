package com.dangphuoctai.BookStore.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dangphuoctai.BookStore.config.AppConstants;
import com.dangphuoctai.BookStore.entity.Address;
import com.dangphuoctai.BookStore.entity.RefreshToken;
import com.dangphuoctai.BookStore.entity.Role;
import com.dangphuoctai.BookStore.entity.User;
import com.dangphuoctai.BookStore.exceptions.APIException;
import com.dangphuoctai.BookStore.exceptions.ResourceNotFoundException;
import com.dangphuoctai.BookStore.payloads.dto.AddressDTO;
import com.dangphuoctai.BookStore.payloads.dto.UserDTO.UserDTO;
import com.dangphuoctai.BookStore.payloads.dto.UserDTO.UserPassword;
import com.dangphuoctai.BookStore.payloads.dto.UserDTO.UserRole;
import com.dangphuoctai.BookStore.payloads.response.UserResponse;
import com.dangphuoctai.BookStore.repository.AddressRepo;
import com.dangphuoctai.BookStore.repository.RefreshTokenRepo;
import com.dangphuoctai.BookStore.repository.RoleRepo;
import com.dangphuoctai.BookStore.repository.UserRepo;
import com.dangphuoctai.BookStore.service.FileService;
import com.dangphuoctai.BookStore.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private RefreshTokenRepo refreshTokenRepo;

    @Autowired
    private AddressRepo addressRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @Override
    public UserDTO getUserInfor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO getUserById(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return userDTO;
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        String roles = jwt.getClaim("scope");
        List<String> roleList = Arrays.asList(roles.split(" "));
        if (!userId.equals(userDTO.getUserId()) && !roleList.contains("ADMIN")) {
            throw new APIException("Bạn không có quyền cập nhật thông tin người dùng này");
        }
        User user = userRepo.findById(userDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userDTO.getUserId()));
        user.setFullName(userDTO.getFullName());
        user.setMobileNumber(userDTO.getMobileNumber());
        user.setAddress(convertToAddress(userDTO.getAddress()));

        userRepo.save(user);

        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO updateUserAvatar(Long userId, MultipartFile image) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long id = jwt.getClaim("userId");
        String roles = jwt.getClaim("scope");
        List<String> roleList = Arrays.asList(roles.split(" "));
        if (!id.equals(userId) && !roleList.contains("ADMIN")) {
            throw new APIException("Bạn không có quyền cập nhật thông tin người dùng này");
        }
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        String fileName = fileService.uploadImage(path, image);
        user.setAvatar(fileName);
        userRepo.save(user);

        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<User> pageUsers = userRepo.findAll(pageDetails);

        List<User> users = pageUsers.getContent();
        List<UserDTO> userDTOs = users.stream().map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());

        UserResponse userResponse = new UserResponse();
        userResponse.setContent(userDTOs);
        userResponse.setPageNumber(pageUsers.getNumber());
        userResponse.setPageSize(pageUsers.getSize());
        userResponse.setTotalElements(pageUsers.getTotalElements());
        userResponse.setTotalPages(pageUsers.getTotalPages());
        userResponse.setLastPage(pageUsers.isLast());

        return userResponse;
    }

    @Override
    public String changeRole(UserRole userRole) {
        User user = userRepo.findById(userRole.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userRole.getUserId()));
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getRoleId().equals(AppConstants.ADMIN_ID));
        if (isAdmin) {
            throw new AccessDeniedException("Bạn không thể thay đổi quyền của tài khoản admin!");
        }
        List<Role> roles = roleRepo.findAll();
        if (AppConstants.ADMIN_ID.equals(userRole.getRoleId())) {
            user.getRoles().addAll(roles);
        } else if (AppConstants.STAFF_ID.equals(userRole.getRoleId())) {
            for (Role r : roles) {
                if (!r.getRoleId().equals(AppConstants.ADMIN_ID)) {
                    user.getRoles().add(r);
                }
            }
        } else if (AppConstants.USER_ID.equals(userRole.getRoleId())) {
            user.getRoles().clear();
            roles.stream()
                    .filter(r -> r.getRoleId().equals(AppConstants.USER_ID))
                    .findFirst()
                    .ifPresent(user.getRoles()::add);
        }

        return "Thay đổi quyền của user thành công";
    }

    @Override
    public String changePassword(String currentPassword, String newPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        boolean checkPassword = passwordEncoder.matches(currentPassword, user.getPassword());
        if (!checkPassword) {
            throw new APIException("Mật khẩu hiện tại không đúng");
        }
        String encodedPass = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPass);

        userRepo.save(user);

        return "Password changed successfully";
    }

    @Override
    public String changeAccountStatus(Long userId, Boolean status) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        if ("admin".equals(user.getUsername())) {
            throw new AccessDeniedException("Đây là tài khoản chính không thể thay đổi trạng thái!");
        }
        user.setEnabled(status);
        if (!status) {
            user.getRefreshTokens().clear();
        }
        userRepo.save(user);

        return "Account status updated successfully";
    }

    @Override
    public String resetPassword(Long userId, String newPassword) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        if ("admin".equals(user.getUsername())) {
            throw new AccessDeniedException("Đây là tài khoản chính không thể thay đổi password!");
        }
        String encodedPass = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPass);

        userRepo.save(user);

        return "Password reset successfully";
    }

    // Hàm chuyển đổi AddressDTO -> Address
    private Address convertToAddress(AddressDTO dto) {
        if (dto == null) {
            return null;
        }
        String country = dto.getCountry();
        String district = dto.getDistrict();
        String city = dto.getCity();
        String ward = dto.getWard();
        String buildingName = dto.getBuildingName();
        Address address = addressRepo
                .findByCountryAndDistrictAndCityAndWardAndBuildingName(
                        country, district,
                        city, ward, buildingName);
        if (address == null) {
            address = new Address(country, district, city, ward, buildingName);
            address = addressRepo.save(address);
        }
        return address;
    }
}
