package bachelor.proj.charity.dal.entities.documents;

import bachelor.proj.charity.shared.enums.DocumentType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Data
@NoArgsConstructor
public class DocumentDAO {

    @Column(name = "name", nullable = false, length = 255)
    protected String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 127)
    protected DocumentType type;

    @OneToOne(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            optional = false)
    @JoinColumn(name = "document_body_id", nullable = false)
    protected DocumentBodyDAO body;


    public DocumentDAO(String name, DocumentType type, DocumentBodyDAO body) {
        this.name = name;
        this.type = type;
        this.body = body;
    }
}
