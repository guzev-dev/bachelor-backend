package bachelor.proj.charity.dal.entities.documents;

import bachelor.proj.charity.dal.entities.UserDAO;
import bachelor.proj.charity.shared.enums.DocumentType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {@link UserDAO} profile photo class.
 * <p><i>Database level entity class.</i>
 */

@Entity
@Table(name = "user_profile_photo")
@Data
@NoArgsConstructor
public class UserProfilePhotoDAO extends DocumentDAO {

    /**
     * <b>{@link UserProfilePhotoDAO}</b> identifier (unique user profile photo number).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id = -1L;

    @OneToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE},
            optional = false)
    @MapsId
    private UserDAO user;

    public UserProfilePhotoDAO(DocumentDAO document) {
        this.name = document.name;
        this.type = DocumentType.PROFILE_PHOTO;
        this.body = document.body;
    }

    public UserProfilePhotoDAO(String name, DocumentBodyDAO body, UserDAO user) {
        super(name, DocumentType.PROFILE_PHOTO, body);
        this.user = user;
    }

    public UserProfilePhotoDAO(Long id, String name, DocumentBodyDAO body, UserDAO user) {
        super(name, DocumentType.PROFILE_PHOTO, body);
        this.id = id;
        this.user = user;
    }
}
