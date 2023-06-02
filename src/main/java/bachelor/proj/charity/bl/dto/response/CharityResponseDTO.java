package bachelor.proj.charity.bl.dto.response;

import bachelor.proj.charity.dal.entities.CharityDAO;
import bachelor.proj.charity.shared.enums.CharityCategory;
import bachelor.proj.charity.shared.enums.CharityStatus;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CharityResponseDTO<T extends CharityResponseDTO.OwnerInfo> {

    protected Long id;

    protected String name;

    protected String description;

    protected CharityCategory.CharityCategoryResponseDTO category;

    protected BigDecimal needToCollect;

    protected LocalDate endDate;

    protected BigDecimal collected;

    protected Long upvotes;

    protected CharityStatus.CharityStatusResponseDTO status;

    @Setter
    protected DocumentResponseDTO photo;

    @Setter
    protected List<DocumentResponseDTO> documents;

    @Setter
    protected boolean individual;

    @Setter
    protected T createdBy;

    public CharityResponseDTO(CharityDAO charityDAO) {
        this.id = charityDAO.getId();
        this.name = charityDAO.getName();
        this.description = charityDAO.getDescription();
        this.category = charityDAO.getCategory().getResponse();
        this.needToCollect = charityDAO.getNeedToCollect();
        this.endDate = charityDAO.getEndDate();
        this.collected = charityDAO.getCollected();
        this.upvotes = charityDAO.getUpvotes();
        this.status = charityDAO.getStatus().getResponse();
    }

    @JsonSetter("category")
    public void setCategory(String category) {
        this.category = CharityCategory.getByName(category).getResponse();
    }

    @JsonSetter("status")
    public void setStatus(String status) {
        this.status = CharityStatus.getByName(status).getResponse();
    }

    public Long getCreatedBy() {
        return createdBy.getOwnerId();
    }

    public interface OwnerInfo {
        Long getOwnerId();
    }
}
