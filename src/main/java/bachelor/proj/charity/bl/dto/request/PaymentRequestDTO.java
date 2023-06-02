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

@JsonInclude
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentRequestDTO {

    @Setter
    @NotNull(message = "ID of user that paid can't be null.",
            groups = CreateRequest.class)
    @Positive(message = "ID of user that paid be a positive integer.",
            groups = CreateRequest.class)
    private Long paidByUserId;

    @NotNull(message = "ID of charity to which payment relates can't be null.",
            groups = CreateRequest.class)
    @Positive(message = "ID of charity to which payment relates must be a positive integer.",
            groups = CreateRequest.class)
    private Long relatesToCharityId;

    @NotNull(message = "Payment amount can't be null.",
            groups = CreateRequest.class)
    @Positive(message = "Payment amount must be a positive number.",
            groups = CreateRequest.class)
    private BigDecimal amount;

    @NotNull(
            groups = CreateRequest.class)
    private Boolean anonymously;

    /**
     * Create request constructor.
     */
    public PaymentRequestDTO(Long paidByUserId, Long relatesToCharityId, BigDecimal amount, Boolean anonymously) {
        this.paidByUserId = paidByUserId;
        this.relatesToCharityId = relatesToCharityId;
        this.amount = amount;
        this.anonymously = anonymously;
    }
}
