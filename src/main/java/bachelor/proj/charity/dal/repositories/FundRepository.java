package bachelor.proj.charity.dal.repositories;

import bachelor.proj.charity.dal.entities.FundDAO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FundRepository extends JpaRepository<FundDAO, Long> {

    Optional<FundDAO> findFundDAOByModerator_Id(Long moderatorId);

    @Query("SELECT DISTINCT f.locations FROM FundDAO f")
    List<String> findFundLocations();

    Page<FundDAO> findFundDAOSByNameLike(String name, Pageable pageable);

    Page<FundDAO> findFundDAOSByLocationsContains(String name, Pageable pageable);

    Page<FundDAO> findFundDAOSByNameLikeAndLocationsContains(String name, String locations, Pageable pageable);
}
