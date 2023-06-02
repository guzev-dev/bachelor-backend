package bachelor.proj.charity.bl.dto.response;

import bachelor.proj.charity.dal.entities.CharityDAO;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;

@Getter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UserCharityResponseDTO extends CharityResponseDTO<UserResponseDTO> {

    public UserCharityResponseDTO(CharityDAO charityDAO) {
        super(charityDAO);
    }

    @Override
    public void setCreatedBy(UserResponseDTO createdBy) {
        this.individual = true;
        super.setCreatedBy(createdBy);
    }
}
