package bachelor.proj.charity.bl.dto.request.documents;

import bachelor.proj.charity.bl.dto.validation.CreateRequest;
import bachelor.proj.charity.shared.enums.DocumentType;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CharityDocumentRequestDTO extends DocumentRequestDTO {

    @Setter
    @NotNull(message = "Charity ID can't be null.",
            groups = CreateRequest.class)
    @Positive(message = "Charity ID be a positive integer.",
            groups = CreateRequest.class)
    private Long charityId;

    /**
     * Create request constructor.
     * */
    public CharityDocumentRequestDTO(String name, DocumentType type, String extension, byte[] content, Long charityId) {
        super(name, type, extension, content);
        this.charityId = charityId;
    }
}
