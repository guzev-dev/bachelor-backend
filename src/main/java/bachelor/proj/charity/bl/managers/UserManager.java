package bachelor.proj.charity.bl.managers;

import bachelor.proj.charity.dal.entities.UserDAO;
import bachelor.proj.charity.dal.entities.documents.UserProfilePhotoDAO;
import bachelor.proj.charity.shared.enums.UserRole;
import org.springframework.stereotype.Service;

@Service
public interface UserManager {

    UserProfilePhotoDAO createUserProfilePhoto(UserProfilePhotoDAO document, Long userId);

    UserDAO readById(Long userId);

    UserDAO readByEmail(String email);

    UserProfilePhotoDAO readPhoto(Long userId);

    UserDAO update(UserDAO user);

    UserDAO changeUserRole(UserRole role, Long userId);

    UserDAO changeUpvoteDate(Long userId);

    void deletePhoto(Long photoId);
}
