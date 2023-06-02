package bachelor.proj.charity.dal.repositories;

import bachelor.proj.charity.dal.entities.documents.FundDocumentDAO;
import bachelor.proj.charity.shared.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FundDocumentRepository extends JpaRepository<FundDocumentDAO, Long> {

    Optional<FundDocumentDAO> findFundDocumentDAOByFund_IdAndType(Long fundId, DocumentType type);

    List<FundDocumentDAO> findFundDocumentDAOSByFund_Id(Long fundId);

    List<FundDocumentDAO> findFundDocumentDAOSByFund_IdAndType(Long fundId, DocumentType type);

}
