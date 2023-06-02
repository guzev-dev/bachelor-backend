package bachelor.proj.charity.bl.managers.impl;

import bachelor.proj.charity.bl.managers.FundManager;
import bachelor.proj.charity.bl.managers.UserManager;
import bachelor.proj.charity.dal.entities.FundDAO;
import bachelor.proj.charity.dal.entities.UserDAO;
import bachelor.proj.charity.dal.entities.documents.FundDocumentDAO;
import bachelor.proj.charity.dal.repositories.FundCharityRepository;
import bachelor.proj.charity.dal.repositories.FundDocumentRepository;
import bachelor.proj.charity.dal.repositories.FundRepository;
import bachelor.proj.charity.shared.enums.DocumentType;
import bachelor.proj.charity.shared.enums.UserRole;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Service
public class FundManagerImpl implements FundManager {

    private final FundRepository fundRepository;
    private final FundDocumentRepository fundDocumentRepository;
    private final FundCharityRepository fundCharityRepository;
    private final UserManager userManager;
    private final EntityManager entityManager;

    private final Supplier<? extends RuntimeException> cannotFindFundException =
            () -> new EntityNotFoundException("Can't find a fund with such ID.");
    private Map<String, Object> forceDocumentInitializationProperties;

    @Override
    public FundDAO create(FundDAO fund) {

        //set fund moderator and save to db
        UserDAO userDAO = new UserDAO();
        userDAO.setId(fund.getModerator().getId());

        //set moderator and return created fund
        return setModerator(fund, userDAO);
    }

    @Override
    public FundDocumentDAO createDocument(FundDocumentDAO document, Long fundId) {

        if (document.getType() == DocumentType.FUND_LOGO) {
            //check if logo already exists
            final Optional<FundDocumentDAO> previousLogo =
                    fundDocumentRepository.findFundDocumentDAOByFund_IdAndType(fundId, DocumentType.FUND_LOGO);

            //if exists -> delete previous logo
            previousLogo.ifPresent(documentDAO -> deleteDocument(documentDAO.getId()));
        }
        //set fund id and save
        FundDAO fundDAO = new FundDAO();
        fundDAO.setId(fundId);
        document.setFund(fundDAO);

        //return created document
        return fundDocumentRepository.save(document);
    }

    @Override
    public FundDAO readById(Long id) {
        return fundRepository.findById(id)
                .orElseThrow(cannotFindFundException);
    }

    @Override
    public FundDAO readByModerator(Long userId) {
        return fundRepository.findFundDAOByModerator_Id(userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find a fund with such moderator."));
    }

    @Override
    public Page<FundDAO> readAll(Pageable pageable) {
        return fundRepository.findAll(pageable);
    }


    @Override
    public Page<FundDAO> readAllByName(String name, Pageable pageable) {
        return fundRepository.findFundDAOSByNameLike(surroundWithPercents(name), pageable);
    }

    @Override
    public Page<FundDAO> readAllByLocation(String location, Pageable pageable) {
        return fundRepository.findFundDAOSByLocationsContains(location, pageable);
    }

    @Override
    public Page<FundDAO> readAllByNameAndLocation(String name, String location, Pageable pageable) {
        return fundRepository.findFundDAOSByNameLikeAndLocationsContains(surroundWithPercents(name), location, pageable);
    }

    @Override
    public List<FundDocumentDAO> readFundDocuments(Long fundId) {
        return fundDocumentRepository.findFundDocumentDAOSByFund_Id(fundId);
    }

    @Override
    public List<FundDocumentDAO> readFundDocumentsByType(Long fundId, DocumentType type) {
        return fundDocumentRepository.findFundDocumentDAOSByFund_IdAndType(fundId, type);
    }

    @Override
    public FundDocumentDAO readFundLogo(Long fundId) {
        //find fund logo id
        Optional<FundDocumentDAO> logo =
                fundDocumentRepository.findFundDocumentDAOByFund_IdAndType(fundId, DocumentType.FUND_LOGO);

        if (logo.isPresent()) {
            //initialize body and return
            return readDocumentWithBody(logo.get().getId());
        } else
            return null;
    }

    @Override
    public FundDocumentDAO readDocumentWithBody(Long documentId) {

        return entityManager.find(FundDocumentDAO.class, documentId, forceDocumentInitializationProperties);
    }

    @Override
    public List<String> readAvailableLocations() {
        return fundRepository.findFundLocations();
    }

    @Override
    public FundDAO update(FundDAO fund) {
        //find fund to update
        FundDAO fundToUpdate = fundRepository
                .findById(fund.getId())
                .orElseThrow(cannotFindFundException);
        boolean isChanged = false;

        //compare fund data and if it differs then change it
        if (!fundToUpdate.getName().equals(fund.getName())) {
            fundToUpdate.setName(fund.getName());
            isChanged = true;
        }
        if (!fundToUpdate.getDescription().equals(fund.getDescription())) {
            fundToUpdate.setDescription(fund.getDescription());
            isChanged = true;
        }
        if (!fundToUpdate.getContactNumber().equals(fund.getContactNumber())) {
            fundToUpdate.setContactNumber(fund.getContactNumber());
            isChanged = true;
        }
        if (!fundToUpdate.getLocations().equals(fund.getLocations())) {
            fundToUpdate.setLocations(fund.getLocations());
            isChanged = true;
        }
        if (!fundToUpdate.getCategories().equals(fund.getCategories())) {
            fundToUpdate.setCategories(fund.getCategories());
            isChanged = true;
        }

        //if there are any changes -> save them and return changed fund, otherwise -> just return unchanged fund
        if (isChanged) {
            fundToUpdate = fundRepository.save(fundToUpdate);
        }

        return fundToUpdate;
    }

    @Override
    public FundDAO changeModerator(Long fundId, Long userId) {
        //find fund to update
        FundDAO fundToUpdate = fundRepository
                .findById(fundId)
                .orElseThrow(cannotFindFundException);
        Long previousModeratorId = fundToUpdate.getModerator().getId();

        //set moderator and save new fund
        UserDAO moderatorDAO = new UserDAO();
        moderatorDAO.setId(userId);

        final FundDAO result = setModerator(fundToUpdate, moderatorDAO);

        //if new moderator settled successfully then change previous moderator role to USER
        userManager.changeUserRole(UserRole.USER, previousModeratorId);

        //return changed fund
        return result;
    }

    @Override
    public void delete(Long id) {

        //detach all fund charities from fund before delete
        fundCharityRepository.findFundCharityDAOSByOwner_Id(id, null)
                .map(fundCharityDAO -> {
                    fundCharityDAO.setOwner(null);
                    fundCharityRepository.save(fundCharityDAO);

                    return null;
                });

        //delete fund
        fundRepository.deleteById(id);
    }

    @Override
    public void deleteDocument(Long documentId) {
        fundDocumentRepository.deleteById(documentId);
    }

    private FundDAO setModerator(FundDAO fundDAO, UserDAO userDAO) {

        //if user don't moderate any fund -> set new moderator and save changes, otherwise - throw exception
        final UserDAO user = userManager.readById(userDAO.getId());

        if (user.getRole() != UserRole.FUND_MODERATOR) {

            fundDAO.setModerator(userDAO);
            final FundDAO result = fundRepository.save(fundDAO);

            //change user role to FUND_MODERATOR
            userManager.changeUserRole(UserRole.FUND_MODERATOR, userDAO.getId());

            //return result
            return result;
        } else
            throw new IllegalArgumentException("Користувач вже є модератором іншого фонду.");

    }

    private String surroundWithPercents(String input) {
        return "%" + input + "%";
    }

    @PostConstruct
    private void setForceDocumentInitializationProperties() {
        EntityGraph<FundDocumentDAO> entityGraph = entityManager.createEntityGraph(FundDocumentDAO.class);
        entityGraph.addAttributeNodes("body");
        Map<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.fetchgraph", entityGraph);
        this.forceDocumentInitializationProperties = properties;
    }
}
