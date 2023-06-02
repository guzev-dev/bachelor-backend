package bachelor.proj.charity.dal.entities;

import bachelor.proj.charity.shared.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * An application user class that contains user's personal data, such as firstName and lastName,
 * login credentials, role, last upvote date.
 * <p><i>Database level entity class.</i>
 */

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
public class UserDAO {

    /**
     * User identifier (unique user number).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id = -1L;

    @Column(name = "email", nullable = false, length = 127, unique = true)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "first_name", nullable = false, length = 127)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 127)
    private String lastName;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "user_role", nullable = false, length = 127)
    private UserRole role = UserRole.USER;

    /**
     * Last date the user upvote for {@link CharityDAO}.
     * <p><i>User can upvote once a day.</i>
     */
    @Column(name = "upvote_date", nullable = false)
    private LocalDate upvoteDate = LocalDate.now();

}
