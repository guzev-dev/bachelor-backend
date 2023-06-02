package bachelor.proj.charity.dal.entities;

import bachelor.proj.charity.shared.enums.CharityCategory;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * {@link CharityDAO} abstract class implementation for charity collections created by fund ({@link FundDAO}).
 * <p><i>Database level entity class.</i>
 */

@Entity
@DiscriminatorValue("Fund Charity")
@Data
@NoArgsConstructor
public class FundCharityDAO extends CharityDAO<FundDAO> {

    @ManyToOne
    @JoinColumn(name = "fund_id")
    private FundDAO owner;

    public FundCharityDAO(String name,
                          String description,
                          CharityCategory category,
                          BigDecimal needToCollect,
                          LocalDate endDate,
                          FundDAO owner) {
        super(name, description, category, needToCollect, endDate);
        this.owner = owner;
    }

    @Override
    public FundDAO getOwner() {
        return this.owner;
    }

    @Override
    public void setOwner(FundDAO owner) {
        this.owner = owner;
    }

}
