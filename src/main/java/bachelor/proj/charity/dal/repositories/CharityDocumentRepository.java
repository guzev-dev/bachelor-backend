package bachelor.proj.charity.dal.repositories;

import bachelor.proj.charity.dal.entities.documents.CharityDocumentDAO;
import bachelor.proj.charity.shared.enums.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CharityDocumentRepository extends JpaRepository<CharityDocumentDAO, Long> {

    Optional<CharityDocumentDAO> findCharityDocumentDAOByCharity_IdAndType(Long charityId, DocumentType type);

    Page<CharityDocumentDAO> findCharityDocumentDAOSByCharity_Id(Long charityId, Pageable pageable);

    Page<CharityDocumentDAO> findCharityDocumentDAOSByCharity_IdAndType(Long charityId, DocumentType type, Pageable pageable);

}
