package bachelor.proj.charity.bl.dto.response;

import bachelor.proj.charity.dal.entities.CharityDAO;
import bachelor.proj.charity.dal.entities.PaymentDAO;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PaymentResponseDTO {

    private CharityShortResponseDTO refersTo;

    private UserResponseDTO paidBy;

    private BigDecimal amount;

    private boolean anonymously;

    public PaymentResponseDTO(PaymentDAO paymentDAO) {
        this.amount = paymentDAO.getAmount();
        this.anonymously = paymentDAO.isAnonymously();

        if (paymentDAO.getRelatesTo() != null) {
            this.refersTo = new CharityShortResponseDTO(paymentDAO.getRelatesTo());
        }

        if (paymentDAO.getPaidBy() == null) {
            this.anonymously = true;
        }

        if (!this.anonymously) {
            this.paidBy = new UserResponseDTO(paymentDAO.getPaidBy());
        }
    }

    @Getter
    private static class CharityShortResponseDTO {

        private Long id;

        private String name;    

        private CharityShortResponseDTO(CharityDAO charityDAO) {
            this.id = charityDAO.getId();
            this.name = charityDAO.getName();
        }
    }

}
