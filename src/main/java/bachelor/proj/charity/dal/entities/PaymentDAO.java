package bachelor.proj.charity.dal.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

/**
 * A class contains information about {@link UserDAO} donations:
 * the amount of transaction,
 * info about the user who paid the transaction,
 * the charity collection to which the payment relates.
 * <p><i>Database level entity class.</i>
 */

@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
public class PaymentDAO {

    /**
     * <b>{@link PaymentDAO}</b> identifier (unique payment number).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id = -1L;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDAO paidBy;

    @ManyToOne
    @JoinColumn(name = "charity_id")
    private CharityDAO relatesTo;

    /**
     * Sum of payment.
     */
    @Column(name = "amount", nullable = false, precision = 9, scale = 2, updatable = false)
    private BigDecimal amount;

    /**
     * If <b><i>anonymously</i></b> = true, then app won't show {@link UserDAO} info (lastName, firstName).
     * <p>If <b><i>anonymously</i></b> = false, then app can show {@link UserDAO} info (lastName, firstName).
     */
    @NonNull
    @Column(name = "anonymously", nullable = false)
    private boolean anonymously;
}
