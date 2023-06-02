package bachelor.proj.charity.dal.repositories;

import bachelor.proj.charity.dal.entities.FundCharityDAO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FundCharityRepository extends JpaRepository<FundCharityDAO, Long> {

    Page<FundCharityDAO> findFundCharityDAOSByOwner_Id(Long fundId, Pageable pageable);

}
