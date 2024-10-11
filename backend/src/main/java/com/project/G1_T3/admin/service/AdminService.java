package com.project.G1_T3.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.project.G1_T3.authentication.service.PasswordGeneratorServiceImpl;
import com.project.G1_T3.email.service.EmailService;
import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.model.UserDTO;
import com.project.G1_T3.user.model.UserRole;
import com.project.G1_T3.user.repository.UserRepository;
import com.project.G1_T3.user.service.UserService;

import jakarta.transaction.Transactional;

@Service
@PreAuthorize("hasRole('ADMIN')")
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordGeneratorServiceImpl passwordGeneratorService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public UserDTO registerAdmin(String username, String email) {

        String password = passwordGeneratorService.generatePassword();
        UserDTO userDTO = userService.registerUser(username, email, password, UserRole.ADMIN);

        logger.info("Admin registered successfully: {}:{}", username, password);

        emailService.sendTempPasswordEmail(userDTO, password);
        return userDTO;
    }

    public Page<UserDTO> getPaginatedUsers(int page, int size, String field, String order) {

        Sort sort = order.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(field).ascending()
                : Sort.by(field).descending();

        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<User> userPage = userRepository.findAll(pageable);

        return userPage.map(UserDTO::fromUser);
    }
}
