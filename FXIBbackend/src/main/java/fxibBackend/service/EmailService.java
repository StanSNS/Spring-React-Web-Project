package fxibBackend.service;

import fxibBackend.dto.AuthorizationDTOS.LoginDTO;
import fxibBackend.dto.UserDetailsDTO.LocationDTO;
import fxibBackend.entity.InquiryEntity;
import fxibBackend.entity.ReportEntity;
import fxibBackend.entity.TransactionEntity;
import fxibBackend.entity.UserEntity;
import fxibBackend.exception.ResourceNotFoundException;
import fxibBackend.repository.UserEntityRepository;
import fxibBackend.util.CustomDateFormatter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static fxibBackend.constants.ConfigConst.*;
import static fxibBackend.constants.EmailTemplateConst.*;

import static fxibBackend.constants.ResponseConst.*;

@Service
@RequiredArgsConstructor
public class EmailService {

    /**
     * initializing dependencies with lombok @RequiredArgsConstructor
     */
    private final UserEntityRepository userEntityRepository;
    private final JavaMailSender javaMailSender;
    private final CustomDateFormatter customDateFormatter;

    /**
     * Sends a reset password email to the specified email address.
     *
     * @param toEmail The email address to which the reset password email will be sent.
     * @return A success message indicating that the password change email was sent.
     * @throws MessagingException        If there is an issue with sending the email.
     * @throws ResourceNotFoundException If the user with the specified email is not found.
     */
    public String sendResetPasswordEmail(String toEmail) throws MessagingException {
        Optional<UserEntity> byEmailOptional = userEntityRepository.findByEmail(toEmail);

        // If the user with the email is not found, throw a ResourceNotFoundException.
        if (byEmailOptional.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        UserEntity user = byEmailOptional.get();

        // Generate a unique reset token and update it in the user entity.
        String rawResetToken = UUID.randomUUID().toString();
        String hashedResetToken = DigestUtils.sha256Hex(rawResetToken);
        user.setResetToken(hashedResetToken);
        userEntityRepository.save(user);

        // Create and send the reset password email.
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, EMAIL_ENCODING);
        helper.setFrom(EMAIL_ORIGIN);
        helper.setTo(toEmail);
        helper.setSubject(EMAIL_FORGOT_PASSWORD_SUBJECT);
        helper.setText(String.format(EMAIL_FORGOT_PASSWORD_HTML_TEMPLATE, user.getUsername(), rawResetToken), true);
        javaMailSender.send(message);

        return PASSWORD_CHANGE_EMAIL_SENT_SUCCESSFULLY;
    }

    /**
     * Sends a two-factor authentication email for login verification.
     *
     * @param loginDTO The login details containing the username.
     * @return A success message indicating that the two-factor authentication email was sent.
     * @throws MessagingException If there is an issue with sending the email.
     */
    public String send2FactorAuthEmail(LoginDTO loginDTO) throws MessagingException {
        UserEntity userEntity = userEntityRepository.findByUsername(loginDTO.getUsername()).get();
        int code;

        // Generate a unique six-digit code for two-factor authentication.
        do {
            code = generateRandomSixDigitCode();
        } while (userEntityRepository.existsByTwoFactorLoginCode(code));
        userEntity.setTwoFactorLoginCode(code);
        userEntityRepository.save(userEntity);

        // Create and send the two-factor authentication email.
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, EMAIL_ENCODING);
        helper.setFrom(EMAIL_ORIGIN);
        helper.setTo(userEntity.getEmail());
        helper.setSubject(TWO_FACTOR_AUTH_SUBJECT_EMAIL);
        helper.setText(String.format(TWO_FACTOR_AUTH_HTML_TEMPLATE_EMAIL, userEntity.getUsername(), code), true);
        javaMailSender.send(message);

        return TWO_FACTOR_CODE_EMAIL_SENT_SUCCESSFULLY;
    }


    /**
     * Generates a random six-digit code for two-factor authentication.
     *
     * @return A randomly generated six-digit code.
     */
    private Integer generateRandomSixDigitCode() {
        Random random = new Random();
        int min = 100000;
        int max = 999999;
        return random.nextInt((max - min) + 1) + min;
    }

    /**
     * Sends an email to the user when a location difference is detected.
     *
     * @param originalLocationDTO The original location information.
     * @param currentLocationDTO  The current location information.
     * @return A success message indicating that the location difference email was sent.
     * @throws MessagingException If there is an issue with sending the email.
     */
    public String sendLocationDifferenceEmail(LocationDTO originalLocationDTO, LocationDTO currentLocationDTO) throws MessagingException {
        UserEntity userEntity = userEntityRepository.findByUsername(originalLocationDTO.getUsername()).get();

        // Create and send an email notifying the user about a location difference.
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, EMAIL_ENCODING);
        helper.setFrom(EMAIL_ORIGIN);
        helper.setTo(userEntity.getEmail());
        helper.setSubject(DIFFERENT_LOCATION_AUTH_SUBJECT_EMAIL);
        helper.setText(String.format(DIFFERENT_LOCATION_HTML_TEMPLATE_EMAIL,
                userEntity.getUsername(),
                currentLocationDTO.getContinent(),
                currentLocationDTO.getCountryFlagURL(),
                currentLocationDTO.getCountry(),
                currentLocationDTO.getCity(),
                currentLocationDTO.getIp(),
                originalLocationDTO.getContinent(),
                originalLocationDTO.getCountryFlagURL(),
                originalLocationDTO.getCountry(),
                originalLocationDTO.getCity(),
                originalLocationDTO.getIp()), true);

        javaMailSender.send(message);

        return LOCATION_DIFFERENCE_EMAIL_SENT_SUCCESSFULLY;
    }


    /**
     * Sends a successful registration email to the user.
     *
     * @param username The username of the registered user.
     * @param email    The email address of the registered user.
     * @throws MessagingException If there is an issue with sending the email.
     */
    public void sendSuccessfulRegistrationEmail(String username, String email) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, EMAIL_ENCODING);
        helper.setFrom(EMAIL_ORIGIN);
        helper.setTo(email);
        helper.setSubject(REGISTRATION_SUCCESS_SUBJECT_EMAIL);
        helper.setText(String.format(REGISTRATION_SUCCESS_HTML_TEMPLATE_EMAIL, username), true);
        javaMailSender.send(message);
    }


    /**
     * Sends a successful payment and subscription email to the user.
     *
     * @param transactionEntity The transaction details.
     * @param username          The username of the user.
     * @throws MessagingException If there is an issue with sending the email.
     */
    public void sendSuccessfulPaymentEmail(TransactionEntity transactionEntity, String username) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, EMAIL_ENCODING);
        helper.setFrom(EMAIL_ORIGIN);
        helper.setTo(transactionEntity.getUserEmail());
        helper.setSubject(SUBSCRIPTION_SUCCESS_SUBJECT_EMAIL);
        helper.setText(String.format(SUBSCRIPTION_SUCCESS_HTML_TEMPLATE_EMAIL,
                username
                , transactionEntity.getBillingDate()
                , transactionEntity.getDuration()
                , transactionEntity.getEndOfBillingDate()
                , transactionEntity.getAmount()
                , transactionEntity.getCard()
                , transactionEntity.getStatus()
                , transactionEntity.getReceipt()
                , transactionEntity.getDescription()
        ), true);
        javaMailSender.send(message);
    }


    /**
     * Sends an inquiry email to the support team.
     *
     * @param inquiryEntity The details of the inquiry.
     * @throws MessagingException If there is an issue with sending the email.
     */
    public void sendInquiryEmail(InquiryEntity inquiryEntity) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, EMAIL_ENCODING);
        helper.setFrom(inquiryEntity.getUserEntity().getEmail());
        helper.setTo(EMAIL_ORIGIN);
        helper.setSubject(inquiryEntity.getCustomID());
        helper.setText(String.format(INQUIRY_EMAIL_HTML_TEMPLATE
                        , inquiryEntity.getTitle()
                        , inquiryEntity.getContent()
                        , inquiryEntity.getDate(), inquiryEntity.getUserEntity().getEmail())
                , true);
        javaMailSender.send(message);
    }


    /**
     * Sends an email to notify a user about being banned.
     *
     * @param bannedUser The user who has been banned.
     * @throws MessagingException If there is an issue with sending the email.
     */
    public void sendBanUserEmail(UserEntity bannedUser) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, EMAIL_ENCODING);
        helper.setFrom(EMAIL_ORIGIN);
        helper.setTo(bannedUser.getEmail());
        helper.setSubject(USER_BAN_SUBJECT_EMAIL);

        helper.setText(String.format(USER_BAN_HTML_TEMPLATE_EMAIL
                        , bannedUser.getUsername()
                        , customDateFormatter.formatLocalDateTimeNowAsString(LocalDateTime.now()))
                , true);

        javaMailSender.send(message);
    }

    /**
     * Sends an email to notify a user about being unbanned.
     *
     * @param bannedUser The user who has been unbanned.
     * @throws MessagingException If there is an issue with sending the email.
     */
    public void sendUnbanUserEmail(UserEntity bannedUser) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, EMAIL_ENCODING);
        helper.setFrom(EMAIL_ORIGIN);
        helper.setTo(bannedUser.getEmail());
        helper.setSubject(USER_UNBAN_SUBJECT_EMAIL);

        helper.setText(String.format(USER_UNBAN_HTML_TEMPLATE_EMAIL
                        , bannedUser.getUsername()
                        , customDateFormatter.formatLocalDateTimeNowAsString(LocalDateTime.now()))
                , true);

        javaMailSender.send(message);
    }


    /**
     * Sends an email notification for a submitted report.
     *
     * @param reportEntity The ReportEntity containing information about the report.
     * @param userName     The username associated with the report.
     * @param email        The email address to which the notification should be sent.
     * @throws MessagingException If an error occurs while sending the email.
     */
    public void sendReportEmail(ReportEntity reportEntity, String userName, String email) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, EMAIL_ENCODING);
        helper.setFrom(EMAIL_ORIGIN);
        helper.setTo(email);
        helper.setSubject(String.format(REPORT_EMAIL_SUBJECT, userName));

        helper.setText(String.format(REPORT_EMAIL_HTML_TEMPLATE
                        , reportEntity.getTitle()
                        , reportEntity.getDate()
                        , userName, reportEntity.getContent()
                        , reportEntity.getImgURL())
                , true);

        javaMailSender.send(message);
    }
}
