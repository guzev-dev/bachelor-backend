package bachelor.proj.charity.bl.dto.response;

import bachelor.proj.charity.dal.entities.UserDAO;
import bachelor.proj.charity.shared.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;

@Getter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UserWithCredentialsResponseDTO {

    private Long id;

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private UserRole role;

    public UserWithCredentialsResponseDTO(UserDAO userDAO) {
        this.id = userDAO.getId();
        this.email = userDAO.getEmail();
        this.password = userDAO.getPassword();
        this.firstName = userDAO.getFirstName();
        this.lastName = userDAO.getLastName();
        this.role = userDAO.getRole();

    }

}
