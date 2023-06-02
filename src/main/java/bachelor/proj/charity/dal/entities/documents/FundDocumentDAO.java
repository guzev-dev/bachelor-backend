package bachelor.proj.charity.dal.entities.documents;

import bachelor.proj.charity.dal.entities.FundDAO;
import bachelor.proj.charity.shared.enums.DocumentType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A document that contains some multimedia content related to fund ({@link FundDAO}).
 * <p><i>Database level entity class.</i>
 */

@Entity
@Table(name = "fund_document")
@Data
@NoArgsConstructor
public class FundDocumentDAO extends DocumentDAO {

    /**
     * <b>{@link FundDocumentDAO}</b> identifier (unique fund document number).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id = -1L;

    @ManyToOne(cascade = {},
            optional = false)
    @JoinColumn(name = "fund_id", nullable = false)
    private FundDAO fund;

    public FundDocumentDAO(DocumentDAO document) {
        this.name = document.name;
        this.type = document.type;
        this.body = document.body;
    }

    public FundDocumentDAO(String name, DocumentType type, DocumentBodyDAO body, FundDAO fund) {
        super(name, type, body);
        this.fund = fund;
    }

    public FundDocumentDAO(Long id, String name, DocumentType type, DocumentBodyDAO body, FundDAO fund) {
        super(name, type, body);
        this.id = id;
        this.fund = fund;
    }

}
