package bachelor.proj.charity.dal.repositories;

import bachelor.proj.charity.dal.entities.CharityDAO;
import bachelor.proj.charity.shared.enums.CharityCategory;
import bachelor.proj.charity.shared.enums.CharityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CharityRepository extends JpaRepository<CharityDAO, Long> {

    Page<CharityDAO> findCharityDAOSByNameLike(String name, Pageable pageable);

    Page<CharityDAO> findCharityDAOSByCategory(CharityCategory category, Pageable pageable);

    Page<CharityDAO> findCharityDAOSByStatus(CharityStatus status, Pageable pageable);

    Page<CharityDAO> findCharityDAOSByCategoryAndStatus(CharityCategory category, CharityStatus status, Pageable pageable);

    Page<CharityDAO> findCharityDAOSByCategoryAndNameLike(CharityCategory category, String name, Pageable pageable);

    Page<CharityDAO> findCharityDAOSByStatusAndNameLike(CharityStatus status, String name, Pageable pageable);

    Page<CharityDAO> findCharityDAOSByCategoryAndStatusAndNameLike(CharityCategory category, CharityStatus status, String name, Pageable pageable);

}
