package bachelor.proj.charity.bl.dto.request;

import bachelor.proj.charity.bl.dto.validation.CreateRequest;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonInclude
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCharityRequestDTO extends CharityRequestDTO {

    @Setter
    @NotNull(message = "User ID can't be null.",
            groups = CreateRequest.class)
    @Positive(message = "User ID be a positive integer.",
            groups = CreateRequest.class)
    private Long userId;

    /**
     * Create request constructor.
     * */
    public UserCharityRequestDTO(String name, String description, String charityCategory, BigDecimal needToCollect, LocalDate endDate, Long userId) {
        super(name, description, charityCategory, needToCollect, endDate);
        this.userId = userId;
    }

    /**
     * Update request constructor.
     * */
    public UserCharityRequestDTO(Long id, String name, String description, String charityCategory) {
        super(id, name, description, charityCategory);
    }
}
