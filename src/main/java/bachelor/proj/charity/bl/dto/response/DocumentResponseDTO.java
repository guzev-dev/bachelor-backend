package bachelor.proj.charity.bl.dto.response;

import bachelor.proj.charity.dal.entities.documents.CharityDocumentDAO;
import bachelor.proj.charity.dal.entities.documents.FundDocumentDAO;
import bachelor.proj.charity.dal.entities.documents.UserProfilePhotoDAO;
import bachelor.proj.charity.shared.enums.DocumentType;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import org.hibernate.LazyInitializationException;

@Getter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DocumentResponseDTO {

    private Long id;

    private String name;

    private DocumentType documentType;

    private boolean isInitialized;

    private String extension;

    private byte[] content;

    public DocumentResponseDTO(UserProfilePhotoDAO photoDAO) {
        if (photoDAO == null) {
            return;
        }

        this.id = photoDAO.getId();
        this.name = photoDAO.getName();
        this.documentType = photoDAO.getType();

        try {
            this.extension = photoDAO.getBody().getExtension();
            this.content = photoDAO.getBody().getContent();
            this.isInitialized = true;
        } catch (LazyInitializationException exception) {
            this.isInitialized = false;
        }
    }

    public DocumentResponseDTO(FundDocumentDAO documentDAO) {
        if (documentDAO == null) {
            return;
        }

        this.id = documentDAO.getId();
        this.name = documentDAO.getName();
        this.documentType = documentDAO.getType();

        try {
            this.extension = documentDAO.getBody().getExtension();
            this.content = documentDAO.getBody().getContent();
            this.isInitialized = true;
        } catch (LazyInitializationException exception) {
            this.isInitialized = false;
        }
    }

    public DocumentResponseDTO(CharityDocumentDAO documentDAO) {
        if (documentDAO == null) {
            return;
        }

        this.id = documentDAO.getId();
        this.name = documentDAO.getName();
        this.documentType = documentDAO.getType();

        try {
            this.extension = documentDAO.getBody().getExtension();
            this.content = documentDAO.getBody().getContent();
            this.isInitialized = true;
        } catch (LazyInitializationException exception) {
            this.isInitialized = false;
        }
    }
}
