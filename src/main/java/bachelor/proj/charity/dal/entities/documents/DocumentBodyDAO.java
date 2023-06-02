package bachelor.proj.charity.dal.entities.documents;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A class for lazy loading app documents: photos, reports, logos, etc.
 * <p><i>Database level entity class.</i>
 */

@Entity
@Table(name = "document_body")
@Data
@NoArgsConstructor
public class DocumentBodyDAO {

    /**
     * <b>{@link DocumentBodyDAO}</b> identifier (unique document body number).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id = 1L;

    @Column(name="extension", nullable = false, length = 31)
    private String extension;

    /**
     * {@code content (binary large object)} - array of binary data that represents images, audio or other multimedia content.
     */
    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "MEDIUMBLOB")
    private byte[] content;
}
