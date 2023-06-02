package bachelor.proj.charity.dal.entities;

import bachelor.proj.charity.dal.entities.documents.CharityDocumentDAO;
import bachelor.proj.charity.shared.enums.CharityCategory;
import bachelor.proj.charity.shared.enums.CharityStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Charity collection (charity) abstract class.
 * <p> Class contains info about the charity collection name, category, status,
 * amount of money needed, end date, upvotes.
 * <p><i>Database level entity class.</i>
 */

@Entity
@Table(name = "charity")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "charity_class", discriminatorType = DiscriminatorType.STRING)
@Data
@NoArgsConstructor
public abstract class CharityDAO<O> {

    /**
     * <b>{@link CharityDAO}</b> identifier (unique charity number).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected Long id = -1L;

    @Column(name = "name", nullable = false, length = 255)
    protected String name;

    /**
     * Additional information about the charity collection.
     */
    @Column(name = "description", nullable = false, length = 8191)
    protected String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 127)
    protected CharityCategory category;

    /**
     * The amount of money that <i> need to be collected</i> to complete the charity collection.
     */
    @Column(name = "money_to_collect", nullable = false, precision = 9, scale = 2)
    protected BigDecimal needToCollect;

    /**
     * Date of the charity collection ending.
     */
    @Column(name = "end_date", nullable = false)
    protected LocalDate endDate;

    @Column(name = "collected", nullable = false, precision = 9, scale = 2)
    protected BigDecimal collected = new BigDecimal(0);

    /**
     * The number of user upvotes for the promotion of the charity collection.
     * <p><i>User can upvote for only one charity collection per day.</i>
     */
    @Column(name = "upvotes", nullable = false)
    protected Long upvotes = 0L;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 63)
    protected CharityStatus status = CharityStatus.ACTIVE;

    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.REMOVE},
            mappedBy = "charity")
    private List<CharityDocumentDAO> documents = new ArrayList<>();

    public CharityDAO(String name,
                      String description,
                      CharityCategory category,
                      BigDecimal needToCollect,
                      LocalDate endDate) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.needToCollect = needToCollect;
        this.endDate = endDate;
    }

    /**
     * <b>< T ></b> - the type of the charity collection owner ({@link UserDAO} or {@link FundDAO}).
     */
    public abstract O getOwner();

    public abstract void setOwner(O owner);
}