package bachelor.proj.charity.bl.dto.request.documents;

import bachelor.proj.charity.bl.dto.validation.CreateRequest;
import bachelor.proj.charity.shared.enums.DocumentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class DocumentRequestDTO {

    @NotNull(message = "Document name can't be null.",
            groups = CreateRequest.class)
    @Size(message = "Max length of document name is 255 characters.",
            min = 0, max = 255,
            groups = CreateRequest.class)
    protected String name;

    @NotNull(message = "Document type can't be null.",
            groups = CreateRequest.class)
    protected DocumentType type;

    @NotNull(message = "Document extension can't be null.",
            groups = CreateRequest.class)
    @Size(message = "Max length of document extension is 31 characters.",
            min = 0, max = 31,
            groups = CreateRequest.class)
    protected String extension;

    @NotNull(message = "Document content can't be null.",
            groups = CreateRequest.class)
    protected byte[] content;

    public DocumentRequestDTO(String name, DocumentType type, String extension, byte[] content) {
        this.name = name;
        this.type = type;
        this.extension = extension;
        this.content = content;
    }
}
