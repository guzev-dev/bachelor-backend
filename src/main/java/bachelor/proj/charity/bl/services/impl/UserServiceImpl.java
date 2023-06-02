package bachelor.proj.charity.bl.services.impl;

import bachelor.proj.charity.bl.dto.request.UserRequestDTO;
import bachelor.proj.charity.bl.dto.request.documents.UserPhotoRequestDTO;
import bachelor.proj.charity.bl.dto.response.DocumentResponseDTO;
import bachelor.proj.charity.bl.dto.response.UserResponseDTO;
import bachelor.proj.charity.bl.dto.response.UserWithCredentialsResponseDTO;
import bachelor.proj.charity.bl.dto.validation.CreateRequest;
import bachelor.proj.charity.bl.dto.validation.UpdateRequest;
import bachelor.proj.charity.bl.exceptions.WrongDataExceptionFactory;
import bachelor.proj.charity.bl.managers.SecuredOperationsUserManager;
import bachelor.proj.charity.bl.managers.UserManager;
import bachelor.proj.charity.bl.services.UserService;
import bachelor.proj.charity.dal.entities.UserDAO;
import bachelor.proj.charity.dal.entities.documents.DocumentBodyDAO;
import bachelor.proj.charity.dal.entities.documents.UserProfilePhotoDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserManager userManager;
    private final SecuredOperationsUserManager securedOperationsManager;
    private final WrongDataExceptionFactory exceptionFactory;

    @Override
    public boolean requestToCreate(UserRequestDTO requestDTO) {

        //check if data valid
        exceptionFactory.checkIfValid(requestDTO, CreateRequest.class, "Unable to add user.");

        UserDAO dao = new UserDAO();
        dao.setFirstName(requestDTO.getFirstName());
        dao.setLastName(requestDTO.getLastName());
        dao.setEmail(requestDTO.getEmail());
        dao.setPassword(requestDTO.getPassword());

        return securedOperationsManager.requestToCreate(dao);
    }

    @Override
    public UserWithCredentialsResponseDTO confirmUserCreation(String email, String secretCode) {

        return new UserWithCredentialsResponseDTO(
                securedOperationsManager.validate(email, secretCode)
        );
    }

    @Override
    public DocumentResponseDTO addProfilePhoto(UserPhotoRequestDTO photoRequestDTO) {

        //check if data valid
        exceptionFactory.checkIfValid(photoRequestDTO, CreateRequest.class, "Unable to add photo.");

        //create dao from dto and save
        DocumentBodyDAO documentBodyDAO = new DocumentBodyDAO();
        documentBodyDAO.setExtension(photoRequestDTO.getExtension());
        documentBodyDAO.setContent(photoRequestDTO.getContent());

        UserProfilePhotoDAO photoDAO = new UserProfilePhotoDAO();
        photoDAO.setBody(documentBodyDAO);
        photoDAO.setName(photoRequestDTO.getName());
        photoDAO.setType(photoRequestDTO.getType());

        return new DocumentResponseDTO(
                userManager.createUserProfilePhoto(photoDAO, photoRequestDTO.getUserId())
        );
    }

    @Override
    public UserResponseDTO readById(Long userId, boolean fullInitialization) {

        return this.readUser(userManager.readById(userId), fullInitialization);
    }

    @Override
    public UserResponseDTO readByEmail(String email, boolean fullInitialization) {

        return this.readUser(userManager.readByEmail(email), fullInitialization);
    }

    @Override
    public boolean checkIfVoteAvailable(Long userId) {

        return userManager.readById(userId)
                .getUpvoteDate()
                .isBefore(LocalDate.now());
    }

    @Override
    public UserResponseDTO update(UserRequestDTO requestDTO) {

        exceptionFactory.checkIfValid(requestDTO, UpdateRequest.class, "Unable to update user.");

        UserDAO userDAO = new UserDAO();
        userDAO.setId(requestDTO.getId());
        userDAO.setFirstName(requestDTO.getFirstName());
        userDAO.setLastName(requestDTO.getLastName());

        return new UserResponseDTO(userManager.update(userDAO));
    }

    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {

        return securedOperationsManager.changePassword(userId, oldPassword, newPassword);
    }

    @Override
    public boolean requestToDelete(Long userId) {

        return securedOperationsManager.requestToDelete(userId);
    }

    @Override
    public boolean confirmUserDelete(String email, String secretCode) {

        return securedOperationsManager.delete(email, secretCode);
    }

    @Override
    public void deleteProfilePhoto(Long photoId) {

        userManager.deletePhoto(photoId);
    }

    private UserResponseDTO readUser(UserDAO dao, boolean fullInitialization) {
        final UserResponseDTO response = new UserResponseDTO(dao);

        if (fullInitialization)
            response.setProfilePhoto(
                    new DocumentResponseDTO(userManager.readPhoto(dao.getId()))
            );

        return response;
    }
}
