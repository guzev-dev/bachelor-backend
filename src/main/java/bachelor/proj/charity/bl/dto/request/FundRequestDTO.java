package bachelor.proj.charity.bl.dto.request;

import bachelor.proj.charity.bl.dto.validation.CreateRequest;
import bachelor.proj.charity.bl.dto.validation.UpdateRequest;
import bachelor.proj.charity.shared.enums.CharityCategory;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@JsonInclude
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FundRequestDTO {

    @NotNull(message = "Fund ID can't be null.",
            groups = UpdateRequest.class)
    @Positive(message = "Fund ID must be a positive integer.",
            groups = UpdateRequest.class)
    private Long id;

    @NotNull(message = "Fund name can't be null.",
            groups = CreateRequest.class)
    @Size(message = "Max length of fund name is 255 characters.",
            min = 1, max = 255,
            groups = {CreateRequest.class, UpdateRequest.class})
    private String name;

    @NotNull(message = "Fund description can't be null.",
            groups = CreateRequest.class)
    @Size(message = "Max length of fund description is 8191 characters.",
            min = 0, max = 8191,
            groups = {CreateRequest.class, UpdateRequest.class})
    private String description;

    @NotNull(message = "Fund contact number can't be null.",
            groups = CreateRequest.class)
    @Size(message = "Max length of contact number is 127 characters.",
            min = 1, max = 127,
            groups = {CreateRequest.class, UpdateRequest.class})
    private String contactNumber;

    @NotNull(message = "Fund locations can't be null.",
            groups = CreateRequest.class)
    @NotEmpty(message = "Fund locations must contain at least one location.",
            groups = CreateRequest.class)
    private Set<String> locations;

    @NotNull(message = "Fund categories can't be null.",
            groups = CreateRequest.class)
    @NotEmpty(message = "Fund categories must contain at least one category.",
            groups = CreateRequest.class)
    private Set<CharityCategory> categories;

    @Setter
    @NotNull(message = "Fund moderator ID can't be null.",
            groups = CreateRequest.class)
    @Positive(message = "Fund moderator ID must be a positive integer.",
            groups = CreateRequest.class)
    private Long moderatorId;

    /**
     * Create request constructor.
     */
    public FundRequestDTO(String name, String description, String contactNumber, Set<String> locations, Set<String> categories, Long moderatorId) {
        this.name = name;
        this.description = description;
        this.contactNumber = contactNumber;
        this.locations = locations;
        this.categories = categories.stream().map(CharityCategory::getByName).collect(Collectors.toSet());
        this.moderatorId = moderatorId;
    }

    /**
     * Update request constructor
     */
    public FundRequestDTO(Long id, String name, String description, String contactNumber, Set<String> locations, Set<String> categories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.contactNumber = contactNumber;
        this.locations = locations;
        this.categories = categories.stream().map(CharityCategory::getByName).collect(Collectors.toSet());
    }
}
