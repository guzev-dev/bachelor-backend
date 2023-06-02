package bachelor.proj.charity.bl.dto.response;

import bachelor.proj.charity.dal.entities.CharityDAO;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;

@Getter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FundCharityResponseDTO extends CharityResponseDTO<FundResponseDTO>{

    public FundCharityResponseDTO(CharityDAO charityDAO) {
        super(charityDAO);
    }

    @Override
    public void setCreatedBy(FundResponseDTO createdBy) {
        this.individual = false;
        super.setCreatedBy(createdBy);
    }
}
