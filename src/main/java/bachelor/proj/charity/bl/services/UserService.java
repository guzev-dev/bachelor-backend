package bachelor.proj.charity.bl.services;

import bachelor.proj.charity.bl.dto.request.UserRequestDTO;
import bachelor.proj.charity.bl.dto.request.documents.UserPhotoRequestDTO;
import bachelor.proj.charity.bl.dto.response.DocumentResponseDTO;
import bachelor.proj.charity.bl.dto.response.UserResponseDTO;
import bachelor.proj.charity.bl.dto.response.UserWithCredentialsResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    boolean requestToCreate(UserRequestDTO requestDTO);

    UserWithCredentialsResponseDTO confirmUserCreation(String email, String secretCode);

    DocumentResponseDTO addProfilePhoto(UserPhotoRequestDTO photoRequestDTO);

    UserResponseDTO readById(Long userId, boolean fullInitialization);

    default UserResponseDTO readByEmail(String email) {
        return readByEmail(email, false);
    }

    UserResponseDTO readByEmail(String email, boolean fullInitialization);

    boolean checkIfVoteAvailable(Long userId);

    UserResponseDTO update(UserRequestDTO requestDTO);

    boolean changePassword(Long userId, String oldPassword, String newPassword);

    boolean requestToDelete(Long userId);

    boolean confirmUserDelete(String email, String secretCode);

    void deleteProfilePhoto(Long photoId);

}
