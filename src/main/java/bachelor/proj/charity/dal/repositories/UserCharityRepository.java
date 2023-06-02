package bachelor.proj.charity.dal.repositories;

import bachelor.proj.charity.dal.entities.UserCharityDAO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCharityRepository extends JpaRepository<UserCharityDAO, Long> {

    Page<UserCharityDAO> findUserCharityDAOSByOwner_Id(Long userId, Pageable pageable);

}
