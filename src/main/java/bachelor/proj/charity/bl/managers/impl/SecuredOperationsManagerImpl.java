package bachelor.proj.charity.bl.managers.impl;

import bachelor.proj.charity.bl.managers.FundManager;
import bachelor.proj.charity.bl.managers.SecuredOperationsUserManager;
import bachelor.proj.charity.dal.entities.UserDAO;
import bachelor.proj.charity.dal.repositories.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SecuredOperationsManagerImpl implements SecuredOperationsUserManager {

    private final UserRepository userRepository;
    private final UserProfilePhotoRepository profilePhotoRepository;
    private final UserCharityRepository userCharityRepository;
    private final PaymentRepository paymentRepository;

    private final FundManager fundManager;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Value("${auth.secret-code.length}")
    private Integer secretCodeLength;

    private final Map<UserDAO, String> createRequests = new HashMap<>();
    private final Map<String, String> deleteRequests = new HashMap<>();

    @Override
    public boolean requestToCreate(UserDAO user) {

        //generate secret code
        final String secretCode = RandomStringUtils.random(secretCodeLength, true, true);

        //send secretCode to user email
        sendCreateRequestMessage(user.getEmail(), secretCode);

        //save user creation request to createRequests map
        createRequests.put(user, secretCode);

        return true;
    }

    @Override
    public UserDAO validate(String email, String secretCode) {

        UserDAO userToCreate = null;

        //find user creation request
        for (UserDAO user : createRequests.keySet()) {
            if (user.getEmail().equals(email)) {
                userToCreate = user;
                break;
            }
        }

        //if entered secretCode matches with secretCode stored in createRequests -> save user
        if (userToCreate != null && secretCode.equals(createRequests.get(userToCreate))) {

            //create new user entity with encrypted password
            UserDAO dao = new UserDAO();
            dao.setEmail(userToCreate.getEmail());
            dao.setPassword(passwordEncoder.encode(userToCreate.getPassword()));
            dao.setFirstName(userToCreate.getFirstName());
            dao.setLastName(userToCreate.getLastName());

            //save new user
            dao = userRepository.save(dao);

            //remove creation request from map
            createRequests.remove(userToCreate);

            //set needed info and return
            userToCreate.setId(dao.getId());
            userToCreate.setRole(dao.getRole());
            return userToCreate ;
        }

        //throw error if user creation request not found or user entered invalid secret code
        if (userToCreate == null)
            throw new EntityNotFoundException("Can't find a user with such email.");
        else
            throw new IllegalArgumentException("Invalid secret Code");
    }

    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {

        //find user to update
        final UserDAO userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find a user with such ID."));

        //if entered old password matches with stored in db -> change password
        if (passwordEncoder.matches(oldPassword, userToUpdate.getPassword())) {
            userToUpdate.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(userToUpdate);
            return true;
        }

        return false;
    }

    @Override
    public boolean requestToDelete(Long userId) {

        //find email of user to delete
        final String email = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find a user with such ID."))
                .getEmail();

        //generate secret code
        final String secretCode = RandomStringUtils.random(secretCodeLength, true, true);

        System.out.println(secretCode);

        //send secretCode to user email
        sendDeleteRequestMessage(email, secretCode);

        //save user delete request to deleteRequests map
        this.deleteRequests.put(email, secretCode);

        return true;
    }

    @Override
    public boolean delete(String email, String secretCode) {

        UserDAO userToDelete = null;

        //find user delete request
        if (deleteRequests.containsKey(email) && deleteRequests.get(email).equals(secretCode)) {
            //if request founded and entered secretCode is valid -> fetch user from db
            userToDelete = userRepository.findUserDAOByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("Can't find a user with such email."));
        }

        if (userToDelete != null) {
            //detach all payments from user before delete
            paymentRepository.readPaymentDAOSByPaidBy_Id(userToDelete.getId(), null)
                    .map(payment -> {
                        payment.setPaidBy(null);
                        paymentRepository.save(payment);

                        return null;
                    });

            try {
                fundManager.delete(
                        fundManager.readByModerator(userToDelete.getId()).getId()
                );
            } catch (EntityNotFoundException exception) {
                //exception means that user don't moderate any fund
            }

            //detach all user charities from fund before delete
            userCharityRepository.findUserCharityDAOSByOwner_Id(userToDelete.getId(),null)
                    .map(charity -> {
                        charity.setOwner(null);
                        userCharityRepository.save(charity);

                        return null;
                    });

            //delete user profile photo and then delete user
            profilePhotoRepository.deleteById(userToDelete.getId());
            userRepository.deleteById(userToDelete.getId());
            return true;
        }

        return false;
    }

    private void sendCreateRequestMessage(String email, String secretCode) {

        //create, fill and send user creation request email
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            helper.setFrom("Charity_application");
            helper.setSubject("Створення облікового запису");
            helper.setTo(email);
            helper.setText(RequestText.getCreateText(secretCode), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void sendDeleteRequestMessage(String email, String secretCode) {

        //create, fill and send user delete request email
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            helper.setFrom("Charity_application");
            helper.setSubject("Видалення облікового запису");
            helper.setTo(email);
            helper.setText(RequestText.getDeleteText(secretCode), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    //class for convenient creation of emails
    private static class RequestText {

        private static String getCreateText(String secretCode) {
            return String.format("<div style=\"font-family: Helvetica;\" > <p style=\"font-size: 2rem;\"> Ваш код активації облікового запису на сайті гуманітарної допомоги: </p>" +
                    "<p style=\"text-align: center; font-size: 5rem; letter-spacing: 1rem\"> <b> <a> %s </a> </b> </p> <hr> </div>", secretCode);
        }

        private static String getDeleteText(String secretCode) {

            return String.format("<div style=\"font-family: Helvetica;\" > <p style=\"font-size: 2rem;\"> Код для видалення облікового запису: </p>" +
                    "<p style=\"text-align: center; font-size: 5rem; letter-spacing: 1rem\"> <b> <a> %s </a> </b> </p>" +
                    "<p style=\"text-align: center; font-size: 1rem; color: red;\"> Увага! Цю дію не можна скасувати у майбутньому. Обліковий запис зникне з системи. </p> <hr> </div>", secretCode);
        }

    }

}
