package bachelor.proj.charity.bl.dto.response;

import bachelor.proj.charity.dal.entities.FundDAO;
import bachelor.proj.charity.shared.enums.CharityCategory;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FundResponseDTO implements CharityResponseDTO.OwnerInfo {

    private Long id;

    private String name;

    private String description;

    private String contactNumber;

    private Set<String> locations;

    private SortedSet<CharityCategory.CharityCategoryResponseDTO> categories;

    private UserResponseDTO moderator;

    @Setter
    private DocumentResponseDTO logo;

    @Setter
    private List<DocumentResponseDTO> documents;

    public FundResponseDTO(FundDAO fundDAO) {
        this.id = fundDAO.getId();
        this.name = fundDAO.getName();
        this.description = fundDAO.getDescription();
        this.contactNumber = fundDAO.getContactNumber();
        this.locations = fundDAO.getLocations();
        this.categories = mapCategories(fundDAO.getCategories());
        this.moderator = new UserResponseDTO(fundDAO.getModerator());
    }

    @JsonSetter("categories")
    public void setCategories(Set<CharityCategory> categories) {
        this.categories = mapCategories(categories);
    }

    @Override
    public Long getOwnerId() {
        return id;
    }

    private TreeSet<CharityCategory.CharityCategoryResponseDTO> mapCategories(Set<CharityCategory> categories) {
        return categories.stream()
                .map(CharityCategory::getResponse)
                .collect(Collectors.toCollection(
                        () -> new TreeSet<>(Comparator.comparing(CharityCategory.CharityCategoryResponseDTO::getName))
                ));
    }
}
