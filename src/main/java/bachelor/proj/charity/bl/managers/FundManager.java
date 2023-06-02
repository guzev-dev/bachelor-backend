package bachelor.proj.charity.bl.managers;

import bachelor.proj.charity.dal.entities.FundDAO;
import bachelor.proj.charity.dal.entities.documents.FundDocumentDAO;
import bachelor.proj.charity.shared.enums.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FundManager {

    FundDAO create(FundDAO fund);

    FundDocumentDAO createDocument(FundDocumentDAO document, Long fundId);

    FundDAO readById(Long id);

    FundDAO readByModerator(Long userId);

    Page<FundDAO> readAll(Pageable pageable);

    Page<FundDAO> readAllByName(String name, Pageable pageable);

    Page<FundDAO> readAllByLocation(String location, Pageable pageable);

    Page<FundDAO> readAllByNameAndLocation(String name, String location, Pageable pageable);

    List<FundDocumentDAO> readFundDocuments(Long fundId);

    List<FundDocumentDAO> readFundDocumentsByType(Long fundId, DocumentType type);

    FundDocumentDAO readFundLogo(Long fundId);

    FundDocumentDAO readDocumentWithBody(Long documentId);

    List<String> readAvailableLocations();

    FundDAO update(FundDAO fund);

    FundDAO changeModerator(Long fundId, Long userId);

    void delete(Long id);

    void deleteDocument(Long documentId);
}
