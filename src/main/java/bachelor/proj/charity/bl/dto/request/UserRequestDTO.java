package bachelor.proj.charity.bl.dto.request;

import bachelor.proj.charity.bl.dto.validation.CreateRequest;
import bachelor.proj.charity.bl.dto.validation.UpdateRequest;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRequestDTO {

    @Positive(message = "User ID must be a positive integer.",
            groups = UpdateRequest.class)
    private Long id;

    @NotNull(message = "User email can't be null.",
            groups = CreateRequest.class)
    @Size(message = "Max length of user email is 127 characters.",
            min = 1, max = 127,
            groups = {CreateRequest.class, UpdateRequest.class})
    private String email;

    @NotNull(message = "User password can't be null.",
            groups = CreateRequest.class)
    @Size(message = "Password must be at least 8 characters long and not exceed 255 characters.",
            min = 8, max = 255,
            groups = CreateRequest.class)
    private String password;

    @NotNull(message = "User first name can't be null.",
            groups = CreateRequest.class)
    @Size(message = "Max length of user first name is 127 characters.",
            min = 1, max = 127,
            groups = {CreateRequest.class, UpdateRequest.class})
    private String firstName;

    @NotNull(message = "User last name can't be null.",
            groups = CreateRequest.class)
    @Size(message = "Max length of user last name is 127 characters.",
            min = 1, max = 127,
            groups = {CreateRequest.class, UpdateRequest.class})
    private String lastName;

    /**
     * Create request constructor.
     */
    public UserRequestDTO(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Update request constructor.
     */
    public UserRequestDTO(Long id, String email, String firstName, String lastName) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
