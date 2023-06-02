package bachelor.proj.charity.bl.managers;

import bachelor.proj.charity.dal.entities.UserDAO;
import org.springframework.stereotype.Service;

@Service
public interface SecuredOperationsUserManager {

    boolean requestToCreate(UserDAO user);

    UserDAO validate(String email, String secretCode);

    boolean changePassword(Long userId, String oldPassword, String newPassword);

    boolean requestToDelete(Long userId);

    boolean delete(String email, String secretCode);
}
