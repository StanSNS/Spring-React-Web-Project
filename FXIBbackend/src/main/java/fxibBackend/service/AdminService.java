package fxibBackend.service;


import fxibBackend.dto.AdminDTOS.AdminUserDTO;
import fxibBackend.dto.AdminDTOS.InquiryDTO;
import fxibBackend.dto.AdminDTOS.ReportDTO;
import fxibBackend.dto.AdminDTOS.RolesAdminDTO;
import fxibBackend.entity.RoleEntity;
import fxibBackend.entity.UserEntity;
import fxibBackend.exception.*;
import fxibBackend.repository.InquiryEntityRepository;
import fxibBackend.repository.ReportEntityRepository;
import fxibBackend.repository.UserEntityRepository;
import fxibBackend.util.ValidateData;
import fxibBackend.util.ValidationUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static fxibBackend.constants.ResponseConst.BANNED_USER_STATUS;
import static fxibBackend.constants.ResponseConst.USER_BANNED_SUCCESSFULLY;
import static fxibBackend.constants.RoleConst.ADMIN_C;

@Service
@RequiredArgsConstructor
public class AdminService {

    /**
     * initializing dependencies with lombok @RequiredArgsConstructor
     */
    private final UserEntityRepository userRepository;
    private final ModelMapper modelMapper;
    private final ValidateData validateData;
    private final ValidationUtil validationUtil;
    private final InquiryEntityRepository inquiryEntityRepository;
    private final ReportEntityRepository reportEntityRepository;
    private final EmailService emailService;

    /**
     * Retrieves a list of All users for the admin page which are not administrators.
     *
     * @param jwtToken JWT token for authentication
     * @param username Username of the requester
     * @return A list of AdminUserDTO objects for non-admin users
     * @throws DataValidationException if data validation fails
     */
    public List<AdminUserDTO> getAllAdminUserDTO(String jwtToken, String username) {
        // Validate the user with JWT token
        validateData.validateUserWithJWT(username, jwtToken);

        // Validate the user's authority
        validateData.validateUserAuthority(username);
        return userRepository.findAll()
                .stream()
                .filter(userEntity -> {
                    Set<RoleEntity> roles = userEntity.getRoles();
                    return roles != null && roles.stream().noneMatch(roleEntity -> ADMIN_C.equals(roleEntity.getName()));
                })
                .map(userEntity -> {
                    AdminUserDTO adminUserDTO = modelMapper.map(userEntity, AdminUserDTO.class);
                    if (!validationUtil.isValid(adminUserDTO)) {
                        throw new DataValidationException();
                    }
                    return adminUserDTO;
                })
                .collect(Collectors.toList());
    }

    /**
     * Administrator is banning a User
     * Sets new roles for a user and updates their biography and agreedToTerms status.
     *
     * @param username       The username of the user whose roles are to be updated.
     * @param roles          A set of RolesAdminDTO representing the new roles to assign.
     * @param jwtToken       JWT token for authentication.
     * @param loggedUsername The username of the requester who is making this change.
     * @throws DataValidationException if data validation fails for user roles or if the requester lacks authority.
     */
    public String banUser(String username, Set<RolesAdminDTO> roles, String jwtToken, String loggedUsername) throws MessagingException {
        // Validate the user with JWT token
        validateData.validateUserWithJWT(loggedUsername, jwtToken);

        // Validate the authority of the requester
        validateData.validateUserAuthority(loggedUsername);

        // Find the user by username
        UserEntity userEntity = userRepository.findByUsername(username).get();

        // Check if the user is banned and update their biography accordingly
        if (!validateData.isUserBanned(userEntity.getRoles())) {
            //Ban the user
            userEntity.setBiography(BANNED_USER_STATUS);
            emailService.sendBanUserEmail(userEntity);
        } else {
            //Unban the user
            emailService.sendUnbanUserEmail(userEntity);
            userEntity.setBiography("");
        }

        // Map and set the new roles for the user
        userEntity.setRoles(roles.stream().map(role -> {
            RoleEntity roleEntity = modelMapper.map(role, RoleEntity.class);
            if (!validationUtil.isValid(roleEntity)) {
                throw new DataValidationException();
            }
            return roleEntity;
        }).collect(Collectors.toSet()));

        // Set agreedToTerms to false
        userEntity.setAgreedToTerms(false);

        // Save the updated user entity
        userRepository.save(userEntity);


        return USER_BANNED_SUCCESSFULLY;
    }


    /**
     * Retrieves all inquiries for a specified user and returns them as a list of InquiryDTO objects.
     *
     * @param currentUsername Username of the requester making the request.
     * @param jwtToken        JWT token for authentication.
     * @param username        Username of the user for whom inquiries are to be retrieved.
     * @return A list of InquiryDTO objects representing the inquiries for the specified user.
     * @throws ResourceNotFoundException if the specified user is not found.
     */
    public List<InquiryDTO> getAllInquiriesForUser(String currentUsername, String jwtToken, String username) {
        validateData.validateUserWithJWT(username, jwtToken);

        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(currentUsername);
        if (userEntityOptional.isEmpty()) {
            throw new ResourceNotFoundException();
        }

        return inquiryEntityRepository
                .findAllByUserEntity_Id(userEntityOptional.get().getId())
                .stream()
                .map(inquiryEntity ->
                        modelMapper.map(inquiryEntity, InquiryDTO.class))
                .toList();
    }


    /**
     * Retrieves a list of report DTOs for a specified user.
     *
     * @param currentUsername The username of the currently authenticated user making the request.
     * @param jwtToken        The JWT token for user validation.
     * @param username        The username for which reports are to be retrieved.
     * @return A list of ReportDTOs associated with the specified user.
     * @throws ResourceNotFoundException If the user with the provided currentUsername is not found.
     */
    public List<ReportDTO> getAllReportsForUser(String currentUsername, String jwtToken, String username) {
        validateData.validateUserWithJWT(username, jwtToken);

        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(currentUsername);
        if (userEntityOptional.isEmpty()) {
            throw new ResourceNotFoundException();
        }

        return reportEntityRepository
                .findAllByUserEntity_Id(userEntityOptional.get().getId())
                .stream()
                .map(reportEntity ->
                        modelMapper.map(reportEntity, ReportDTO.class))
                .toList();
    }
}
