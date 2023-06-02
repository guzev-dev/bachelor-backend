package bachelor.proj.charity.dal.entities;

import bachelor.proj.charity.dal.entities.documents.FundDocumentDAO;
import bachelor.proj.charity.shared.enums.CharityCategory;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Fund class contains all information about the charity organization in the application,
 * such as name, fund moderator, list of fund members, etc.
 * <p><i>Database level entity class.</i>
 */

@Entity
@Table(name = "fund")
@Data
@NoArgsConstructor
public class FundDAO {

    /**
     * <b>{@link FundDAO}</b> identifier (unique fund number).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id = -1L;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    /**
     * Information about the goals and purpose of the <b>{@link FundDAO}</b>.
     */
    @Column(name = "description", nullable = false, length = 8191)
    private String description;

    /**
     * Number to contact with representatives of the <b>{@link FundDAO}</b>.
     */
    @Column(name="contact_number", nullable = false, length = 127)
    private String contactNumber;

    /**
     * Locations, where the <b>{@link FundDAO}</b> has volunteers.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "fund_locations",
            joinColumns = @JoinColumn(name = "fund_id", nullable = false)
    )
    @Column(name = "location", nullable = false, length = 255)
    private Set<String> locations;

    /**
     * Charity categories in which the <b>{@link FundDAO}</b> works.
     * */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "fund_categories",
            joinColumns = @JoinColumn(name = "fund_id", nullable = false)
    )
    @Column(name = "category", nullable = false, length = 127)
    @Enumerated(EnumType.STRING)
    private Set<CharityCategory> categories;

    /**
     * User that moderate <b>{@link FundDAO}</b> info.*/
    @OneToOne(optional = false)
    @JoinColumn(name = "moderated_by", nullable = false)
    private UserDAO moderator;

    @ToString.Exclude
    @OneToMany(cascade = {CascadeType.REMOVE},
            mappedBy = "fund")
    private List<FundDocumentDAO> documents = new ArrayList<>();
}