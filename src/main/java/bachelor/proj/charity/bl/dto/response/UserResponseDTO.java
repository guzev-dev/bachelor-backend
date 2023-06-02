package bachelor.proj.charity.bl.dto.response;

import bachelor.proj.charity.dal.entities.UserDAO;
import bachelor.proj.charity.shared.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UserResponseDTO implements CharityResponseDTO.OwnerInfo {

    private Long id;

    private String email;

    private String firstName;

    private String lastName;

    private UserRole role;

    private LocalDate upvoteDate;

    @Setter
    private DocumentResponseDTO profilePhoto;

    public UserResponseDTO(UserDAO userDAO) {
        this.id = userDAO.getId();
        this.email = userDAO.getEmail();
        this.firstName = userDAO.getFirstName();
        this.lastName = userDAO.getLastName();
        this.role = userDAO.getRole();
        this.upvoteDate = userDAO.getUpvoteDate();
    }

    @Override
    public Long getOwnerId() {
        return id;
    }
}

