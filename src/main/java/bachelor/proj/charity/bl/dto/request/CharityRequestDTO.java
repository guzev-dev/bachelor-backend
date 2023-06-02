package bachelor.proj.charity.bl.dto.request;

import bachelor.proj.charity.bl.dto.validation.CreateRequest;
import bachelor.proj.charity.bl.dto.validation.UpdateRequest;
import bachelor.proj.charity.shared.enums.CharityCategory;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CharityRequestDTO {

    @NotNull(message = "Charity ID can't be null.",
            groups = UpdateRequest.class)
    @Positive(message = "Charity ID be a positive integer.",
            groups = UpdateRequest.class)
    protected Long id;

    @NotNull(message = "Charity name can't be null.",
            groups = CreateRequest.class)
    @Size(message = "Max length of charity name is 255 characters.",
            min = 1, max = 255,
            groups = {CreateRequest.class, UpdateRequest.class})
    protected String name;

    @NotNull(message = "Charity description can't be null.",
            groups = CreateRequest.class)
    @Size(message = "Max length of charity description is 8191 characters.",
            min = 0, max = 8191,
            groups = {CreateRequest.class, UpdateRequest.class})
    protected String description;

    @NotNull(message = "Charity category can't be null.",
            groups = {CreateRequest.class, UpdateRequest.class})
    protected CharityCategory category;

    @NotNull(message = "Amount of money need to be collected can't be null.",
            groups = CreateRequest.class)
    @Positive(message = "Amount of money need to be collected must be a positive number.",
            groups = CreateRequest.class)
    protected BigDecimal needToCollect;

    @NotNull(message = "Charity end date can't be null.",
            groups = CreateRequest.class)
    @Future(message = "Charity end date must be a date in future.")
    protected LocalDate endDate;

    public CharityRequestDTO(String name, String description, String category, BigDecimal needToCollect, LocalDate endDate) {
        this.name = name;
        this.description = description;
        this.category = CharityCategory.getByName(category);
        this.needToCollect = needToCollect;
        this.endDate = endDate;
    }

    public CharityRequestDTO(Long id, String name, String description, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = CharityCategory.getByName(category);
    }

    @JsonSetter("category")
    public void setCategory(String category) {
        this.category = CharityCategory.getByName(category);
    }
}
