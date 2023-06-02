package bachelor.proj.charity.bl.managers.impl;

import bachelor.proj.charity.bl.managers.CharityManager;
import bachelor.proj.charity.bl.managers.UserManager;
import bachelor.proj.charity.dal.entities.*;
import bachelor.proj.charity.dal.entities.documents.CharityDocumentDAO;
import bachelor.proj.charity.dal.repositories.*;
import bachelor.proj.charity.shared.enums.CharityCategory;
import bachelor.proj.charity.shared.enums.CharityStatus;
import bachelor.proj.charity.shared.enums.DocumentType;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("rawtypes")

@Service
@RequiredArgsConstructor
public class CharityManagerImpl implements CharityManager {

    private final CharityRepository charityRepository;
    private final CharityDocumentRepository documentRepository;
    private final FundCharityRepository fundCharityRepository;
    private final UserCharityRepository userCharityRepository;
    private final UserManager userManager;
    private final EntityManager entityManager;

    private final Supplier<? extends RuntimeException> cannotFindCharityException =
            () -> new EntityNotFoundException("Can't find a charity with such ID.");
    private Map<String, Object> forceDocumentInitializationProperties;

    @Override
    public CharityDAO create(CharityDAO charity) {
        //save charity and return created charity
        return charityRepository.save(charity);
    }

    @Override
    public CharityDocumentDAO createDocument(CharityDocumentDAO document, Long charityId) {

        if (document.getType() == DocumentType.CHARITY_PHOTO) {
            //check if photo already exists
            final Optional<CharityDocumentDAO> previousPhoto =
                    documentRepository.findCharityDocumentDAOByCharity_IdAndType(charityId, DocumentType.CHARITY_PHOTO);

            //if exists -> delete previous photo
            previousPhoto.ifPresent(documentDAO -> deleteDocument(documentDAO.getId()));
        }

        //set charity id
        CharityDAO charityDAO = new CharityDAO() {
            @Override
            public Object getOwner() {
                return null;
            }

            @Override
            public void setOwner(Object owner) {

            }
        };
        charityDAO.setId(charityId);
        document.setCharity(charityDAO);

        //save document and return created document
        return documentRepository.save(document);
    }

    @Override
    public CharityDAO readById(Long id) {
        return charityRepository.findById(id)
                .orElseThrow(cannotFindCharityException);
    }

    @Override
    public Page<CharityDAO> readAll(Pageable pageable) {
        return charityRepository.findAll(pageable);
    }

    @Override
    public Page<CharityDAO> readAllByName(String name, Pageable pageable) {
        return charityRepository.findCharityDAOSByNameLike(surroundWithPercents(name), pageable);
    }

    @Override
    public Page<CharityDAO> readAllByStatus(CharityStatus status, Pageable pageable) {
        return charityRepository.findCharityDAOSByStatus(status, pageable);
    }

    @Override
    public Page<CharityDAO> readAllByCategory(CharityCategory category, Pageable pageable) {
        return charityRepository.findCharityDAOSByCategory(category, pageable);

    }

    @Override
    public Page<CharityDAO> readAllByCategoryAndStatus(CharityCategory category, CharityStatus status, Pageable pageable) {
        return charityRepository.findCharityDAOSByCategoryAndStatus(category, status, pageable);
    }

    @Override
    public Page<CharityDAO> readAllByCategoryAndName(CharityCategory category, String name, Pageable pageable) {
        return charityRepository.findCharityDAOSByCategoryAndNameLike(category, surroundWithPercents(name), pageable);
    }

    @Override
    public Page<CharityDAO> readAllByStatusAndName(CharityStatus status, String name, Pageable pageable) {
        return charityRepository.findCharityDAOSByStatusAndNameLike(status, surroundWithPercents(name), pageable);
    }

    @Override
    public Page<CharityDAO> readAllByCategoryAndStatusAndName(CharityCategory category, CharityStatus status, String name, Pageable pageable) {
        return charityRepository.findCharityDAOSByCategoryAndStatusAndNameLike(category, status, surroundWithPercents(name), pageable);
    }

    @Override
    public Page<CharityDAO> readAllByFundId(Long fundId) {
        return fundCharityRepository.findFundCharityDAOSByOwner_Id(fundId, null)
                .map(Function.identity());
    }

    @Override
    public Page<CharityDAO> readAllByFundId(Long fundId, Pageable pageable) {
        return fundCharityRepository.findFundCharityDAOSByOwner_Id(fundId, pageable)
                .map(Function.identity());
    }

    @Override
    public Page<CharityDAO> readAllByUserId(Long userId) {
        return userCharityRepository.findUserCharityDAOSByOwner_Id(userId, null)
                .map(Function.identity());
    }

    @Override
    public Page<CharityDAO> readAllByUserId(Long userId, Pageable pageable) {
        return userCharityRepository.findUserCharityDAOSByOwner_Id(userId, pageable)
                .map(Function.identity());
    }

    @Override
    public List<CharityDocumentDAO> readCharityDocuments(Long charityId) {
        return documentRepository.findCharityDocumentDAOSByCharity_Id(charityId, null)
                .getContent();
    }

    @Override
    public List<CharityDocumentDAO> readCharityDocumentsByType(Long charityId, DocumentType type) {
        return documentRepository.findCharityDocumentDAOSByCharity_IdAndType(charityId, type, null)
                .getContent();
    }

    @Override
    public CharityDocumentDAO readCharityPhoto(Long charityId) {
        //find charity photo
        Optional<CharityDocumentDAO> photo =
                documentRepository.findCharityDocumentDAOByCharity_IdAndType(charityId, DocumentType.CHARITY_PHOTO);

        if (photo.isPresent()) {
            //initialize body and return
            return readDocumentWithBody(photo.get().getId());
        } else
            return null;
    }

    @Override
    public CharityDocumentDAO readDocumentWithBody(Long documentId) {
        return entityManager.find(CharityDocumentDAO.class, documentId, forceDocumentInitializationProperties);
    }

    @Override
    public CharityDAO upvoteForCharity(Long charityId, Long userId) {
        //find charity to upvote for
        CharityDAO charityDAO = charityRepository
                .findById(charityId)
                .orElseThrow(cannotFindCharityException);

        //if user not upvoted today -> increment upvotes and save changes
        UserDAO user = userManager.readById(userId);

        if (user.getUpvoteDate().isBefore(LocalDate.now())) {
            final Long upvotes = Long.valueOf(charityDAO.getUpvotes()) + 1;
            charityDAO.setUpvotes(upvotes);
            final CharityDAO result = charityRepository.save(charityDAO);

            //change users upvote date
            userManager.changeUpvoteDate(userId);

            //return result
            return result;
        } else
            throw new RuntimeException("User has already voted today.");
    }

    @Override
    public CharityDAO changeStatus(Long charityId, CharityStatus status) {
        //find charity to update
        CharityDAO charityDAO = charityRepository
                .findById(charityId)
                .orElseThrow(cannotFindCharityException);

        //change status and save changes
        charityDAO.setStatus(status);
        return charityRepository.save(charityDAO);
    }

    @Override
    public CharityDAO addMoney(Long charityId, BigDecimal amount) {
        //find charity to add money to
        CharityDAO charityDAO = charityRepository
                .findById(charityId)
                .orElseThrow(cannotFindCharityException);

        //if amount is null -> throw exception
        if (amount != null) {
            //increment collected money and save changes
            final BigDecimal collected = new BigDecimal(charityDAO.getCollected().toString())
                    .add(amount);
            charityDAO.setCollected(collected);
            return charityRepository.save(charityDAO);
        } else
            throw new NullPointerException("Amount of money added can't be null.");
    }

    @Override
    public CharityDAO update(CharityDAO charity) {
        //find charity to update
        CharityDAO charityToUpdate = charityRepository
                .findById(charity.getId())
                .orElseThrow(cannotFindCharityException);
        boolean isChanged = false;

        //compare charity data if it differs -> change
        if (!charityToUpdate.getName().equals(charity.getName())) {
            charityToUpdate.setName(charity.getName());
            isChanged = true;
        }
        if (!charityToUpdate.getDescription().equals(charity.getDescription())) {
            charityToUpdate.setDescription(charity.getDescription());
            isChanged = true;
        }
        if (!charityToUpdate.getCategory().equals(charity.getCategory())) {
            charityToUpdate.setCategory(charity.getCategory());
            isChanged = true;
        }

        //if there are changes -> save them and return changed charity, otherwise -> just return unchanged charity
        if (isChanged) {
            charityToUpdate = charityRepository.save(charityToUpdate);
        }

        return charityToUpdate;
    }

    @Override
    public void deleteDocument(Long documentId) {
        this.documentRepository.deleteById(documentId);
    }

    private String surroundWithPercents(String input) {
        return "%" + input + "%";
    }

    @PostConstruct
    private void setForceDocumentInitializationProperties() {
        EntityGraph<CharityDocumentDAO> entityGraph = entityManager.createEntityGraph(CharityDocumentDAO.class);
        entityGraph.addAttributeNodes("body");
        Map<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.fetchgraph", entityGraph);
        this.forceDocumentInitializationProperties = properties;
    }

}
