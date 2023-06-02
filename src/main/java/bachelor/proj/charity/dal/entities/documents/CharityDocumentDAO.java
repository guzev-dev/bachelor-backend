package bachelor.proj.charity.dal.entities.documents;

import bachelor.proj.charity.dal.entities.CharityDAO;
import bachelor.proj.charity.shared.enums.DocumentType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A document that contains some multimedia content for charity collections ({@link CharityDAO}).
 * <p><i>Database level entity class.</i>
 */

@Entity
@Table(name = "charity_document")
@Data
@NoArgsConstructor
public class CharityDocumentDAO extends DocumentDAO {

    /**
     * <b>{@link CharityDocumentDAO}</b> identifier (unique charity document number).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id = -1L;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
            optional = false)
    @JoinColumn(name = "charity_id", nullable = false)
    private CharityDAO charity;

    public CharityDocumentDAO(DocumentDAO document) {
        this.name = document.name;
        this.type = document.type;
        this.body = document.body;
    }

    public CharityDocumentDAO(String name, DocumentType type, DocumentBodyDAO body, CharityDAO charity) {
        super(name, type, body);
        this.charity = charity;
    }

    public CharityDocumentDAO(Long id, String name, DocumentType type, DocumentBodyDAO body, CharityDAO charity) {
        super(name, type, body);
        this.id = id;
        this.charity = charity;
    }
}
