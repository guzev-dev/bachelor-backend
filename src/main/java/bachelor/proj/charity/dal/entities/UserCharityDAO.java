package bachelor.proj.charity.dal.entities;

import bachelor.proj.charity.shared.enums.CharityCategory;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * {@link CharityDAO} abstract class implementation for charity collections created by user ({@link UserDAO}).
 * <p><i>Database level entity class.</i>
 */

@Entity
@DiscriminatorValue("User Charity")
@Data
@NoArgsConstructor
public class UserCharityDAO extends CharityDAO<UserDAO> {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDAO owner;

    public UserCharityDAO(String name,
                          String description,
                          CharityCategory category,
                          BigDecimal needToCollect,
                          LocalDate endDate,
                          UserDAO owner) {
        super(name, description, category, needToCollect, endDate);
        this.owner = owner;
    }

    @Override
    public UserDAO getOwner() {
        return this.owner;
    }

    @Override
    public void setOwner(UserDAO owner) {
        this.owner = owner;
    }
}
