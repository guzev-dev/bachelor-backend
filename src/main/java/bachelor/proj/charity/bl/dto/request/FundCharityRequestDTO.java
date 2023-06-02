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
public class FundCharityRequestDTO extends CharityRequestDTO {

    @Setter
    @NotNull(message = "Fund ID can't be null.",
            groups = CreateRequest.class)
    @Positive(message = "Fund ID be a positive integer.",
            groups = CreateRequest.class)
    private Long fundId;

    /**
     * Create request constructor.
     * */
    public FundCharityRequestDTO(String name, String description, String charityCategory, BigDecimal needToCollect, LocalDate endDate, Long fundId) {
        super(name, description, charityCategory, needToCollect, endDate);
        this.fundId = fundId;
    }

    /**
     * Update request constructor.
     * */
    public FundCharityRequestDTO(Long id, String name, String description, String charityCategory) {
        super(id, name, description, charityCategory);
    }
}
