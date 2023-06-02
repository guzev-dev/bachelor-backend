package bachelor.proj.charity.bl.managers;

import bachelor.proj.charity.dal.entities.CharityDAO;
import bachelor.proj.charity.dal.entities.documents.CharityDocumentDAO;
import bachelor.proj.charity.shared.enums.CharityCategory;
import bachelor.proj.charity.shared.enums.CharityStatus;
import bachelor.proj.charity.shared.enums.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@SuppressWarnings("rawtypes")

@Service
public interface CharityManager {

    CharityDAO create(CharityDAO charity);

    CharityDocumentDAO createDocument(CharityDocumentDAO document, Long charityId);

    CharityDAO readById(Long id);

    Page<CharityDAO> readAll(Pageable pageable);

    Page<CharityDAO> readAllByName(String name, Pageable pageable);

    Page<CharityDAO> readAllByStatus(CharityStatus status, Pageable pageable);

    Page<CharityDAO> readAllByCategory(CharityCategory category, Pageable pageable);

    Page<CharityDAO> readAllByCategoryAndStatus(CharityCategory category, CharityStatus status, Pageable pageable);

    Page<CharityDAO> readAllByCategoryAndName(CharityCategory category, String name, Pageable pageable);

    Page<CharityDAO> readAllByStatusAndName(CharityStatus status, String name, Pageable pageable);

    Page<CharityDAO> readAllByCategoryAndStatusAndName(CharityCategory category, CharityStatus status, String name, Pageable pageable);

    Page<CharityDAO> readAllByFundId(Long fundId);

    Page<CharityDAO> readAllByFundId(Long fundId, Pageable pageable);

    Page<CharityDAO> readAllByUserId(Long userId);

    Page<CharityDAO> readAllByUserId(Long userId, Pageable pageable);

    List<CharityDocumentDAO> readCharityDocuments(Long charityId);

    List<CharityDocumentDAO> readCharityDocumentsByType(Long charityId, DocumentType type);

    CharityDocumentDAO readCharityPhoto(Long charityId);

    CharityDocumentDAO readDocumentWithBody(Long documentId);

    CharityDAO upvoteForCharity(Long charityId, Long userId);

    CharityDAO changeStatus(Long charityId, CharityStatus status);

    CharityDAO addMoney(Long charityId, BigDecimal amount);

    CharityDAO update(CharityDAO charity);

    void deleteDocument(Long documentId);
}
