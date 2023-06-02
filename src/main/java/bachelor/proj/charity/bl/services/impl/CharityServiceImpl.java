package bachelor.proj.charity.bl.services.impl;

import bachelor.proj.charity.bl.dto.request.CharityRequestDTO;
import bachelor.proj.charity.bl.dto.request.FundCharityRequestDTO;
import bachelor.proj.charity.bl.dto.request.UserCharityRequestDTO;
import bachelor.proj.charity.bl.dto.request.documents.CharityDocumentRequestDTO;
import bachelor.proj.charity.bl.dto.response.CharityResponseDTO;
import bachelor.proj.charity.bl.dto.response.DocumentResponseDTO;
import bachelor.proj.charity.bl.dto.response.FundResponseDTO;
import bachelor.proj.charity.bl.dto.response.UserResponseDTO;
import bachelor.proj.charity.bl.dto.validation.CreateRequest;
import bachelor.proj.charity.bl.dto.validation.UpdateRequest;
import bachelor.proj.charity.bl.exceptions.WrongDataExceptionFactory;
import bachelor.proj.charity.bl.managers.CharityManager;
import bachelor.proj.charity.bl.managers.FundManager;
import bachelor.proj.charity.bl.managers.UserManager;
import bachelor.proj.charity.bl.services.CharityService;
import bachelor.proj.charity.dal.entities.*;
import bachelor.proj.charity.dal.entities.documents.CharityDocumentDAO;
import bachelor.proj.charity.dal.entities.documents.DocumentBodyDAO;
import bachelor.proj.charity.shared.enums.CharityCategory;
import bachelor.proj.charity.shared.enums.CharityStatus;
import bachelor.proj.charity.shared.enums.DocumentType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")

@Service
@RequiredArgsConstructor
public class CharityServiceImpl implements CharityService {

    private final CharityManager charityManager;
    private final UserManager userManager;
    private final FundManager fundManager;
    private final WrongDataExceptionFactory exceptionFactory;

    private Function<CharityResponseDTO, CharityResponseDTO> initializePhotosFunction;

    @Override
    public CharityResponseDTO addCharity(FundCharityRequestDTO fundCharityDTO) {

        exceptionFactory.checkIfValid(fundCharityDTO, CreateRequest.class, "Unable to add charity.");

        FundDAO fundDAO = new FundDAO();
        fundDAO.setId(fundCharityDTO.getFundId());
        CharityDAO dao = addCharity(new FundCharityDAO(), fundCharityDTO, fundDAO);

        final CharityDAO result = charityManager.create(dao);
        final CharityResponseDTO<FundResponseDTO> response = new CharityResponseDTO<>(result);
        response.setCreatedBy(new FundResponseDTO(fundManager.readById(fundCharityDTO.getFundId())));

        return response;
    }

    @Override
    public CharityResponseDTO addCharity(UserCharityRequestDTO userCharityDTO) {

        exceptionFactory.checkIfValid(userCharityDTO, CreateRequest.class, "Unable to add charity.");

        UserDAO userDAO = new UserDAO();
        userDAO.setId(userCharityDTO.getUserId());
        CharityDAO dao = addCharity(new UserCharityDAO(), userCharityDTO, userDAO);

        final CharityDAO result = charityManager.create(dao);
        final CharityResponseDTO<UserResponseDTO> response = new CharityResponseDTO<>(result);
        response.setCreatedBy(new UserResponseDTO(userManager.readById(userCharityDTO.getUserId())));

        return response;
    }

    @Override
    public DocumentResponseDTO addDocument(CharityDocumentRequestDTO documentDTO) {

        exceptionFactory.checkIfValid(documentDTO, CreateRequest.class, "Unable to add document.");
        DocumentBodyDAO documentBodyDAO = new DocumentBodyDAO();
        documentBodyDAO.setExtension(documentDTO.getExtension());
        documentBodyDAO.setContent(documentDTO.getContent());

        CharityDocumentDAO dao = new CharityDocumentDAO();
        dao.setBody(documentBodyDAO);
        dao.setName(documentDTO.getName());
        dao.setType(documentDTO.getType());

        return new DocumentResponseDTO(charityManager.createDocument(dao, documentDTO.getCharityId()));
    }

    @Override
    public CharityResponseDTO readById(Long id, boolean initializePhoto, boolean initializeDocs) {

        CharityDAO charityDAO = charityManager.readById(id);
        final CharityResponseDTO response = daoToDTO(charityDAO);

        if (initializePhoto) {
            response.setPhoto(
                    new DocumentResponseDTO(
                            charityManager.readCharityPhoto(id)
                    )
            );
        }

        if (initializeDocs) {
            response.setDocuments(
                    charityManager.readCharityDocumentsByType(charityDAO.getId(), DocumentType.CHARITY_DOC)
                            .stream().map(DocumentResponseDTO::new)
                            .collect(Collectors.toList())
            );
        }

        return response;
    }

    @Override
    public Page<CharityResponseDTO> readAll(Integer page, Integer size, Sort sortBy, boolean initializePhotos) {
        Page<CharityResponseDTO> response = charityManager.readAll(PageRequest.of(page, size, sortBy))
                .map(this::daoToDTO);

        if (initializePhotos)
            return response
                    .map(initializePhotosFunction);
        else
            return response;
    }

    @Override
    public Page<CharityResponseDTO> readAllByParams(String name, CharityStatus status, CharityCategory category, Integer page, Integer size, Sort sortBy, boolean initializePhotos) {
        Page<CharityResponseDTO> response = dispatch(name, status, category, PageRequest.of(page, size, sortBy))
                .map(this::daoToDTO);

        if (initializePhotos)
            return response
                    .map(initializePhotosFunction);
        else
            return response;
    }

    @Override
    public Page<CharityResponseDTO> readAllByFundId(Long fundId, Integer page, Integer size, Sort sortBy, boolean initializePhotos) {
        Page<CharityResponseDTO> response = charityManager.readAllByFundId(fundId, PageRequest.of(page, size, sortBy))
                .map(this::daoToDTO);

        if (initializePhotos)
            return response
                    .map(initializePhotosFunction);
        else
            return response;
    }

    @Override
    public Page<CharityResponseDTO> readAllByUserId(Long userId, Integer page, Integer size, Sort sortBy, boolean initializePhotos) {
        Page<CharityResponseDTO> response = charityManager.readAllByUserId(userId, PageRequest.of(page, size, sortBy))
                .map(this::daoToDTO);

        if (initializePhotos)
            return response
                    .map(initializePhotosFunction);
        else
            return response;
    }

    @Override
    public DocumentResponseDTO readDocument(Long documentId) {
        return new DocumentResponseDTO(charityManager.readDocumentWithBody(documentId));
    }

    @Override
    public boolean upvoteForCharity(Long charityId, Long userId) {

        charityManager.upvoteForCharity(charityId, userId);
        return true;
    }

    @Override
    public boolean changeStatus(Long charityId, CharityStatus status) {

        charityManager.changeStatus(charityId, status);
        return true;
    }

    @Override
    public CharityResponseDTO update(CharityRequestDTO charityDTO, boolean initializePhoto, boolean initializeDocs) {

        exceptionFactory.checkIfValid(charityDTO, UpdateRequest.class, "Unable to update charity");

        CharityDAO dao = new CharityDAO() {
            @Override
            public Object getOwner() {
                return null;
            }

            @Override
            public void setOwner(Object owner) {

            }
        };
        dao.setId(charityDTO.getId());
        dao.setName(charityDTO.getName());
        dao.setDescription(charityDTO.getDescription());
        dao.setCategory(charityDTO.getCategory());

        final CharityResponseDTO response = daoToDTO(charityManager.update(dao));

        if (initializePhoto) {
            response.setPhoto(
                    new DocumentResponseDTO(
                            charityManager.readCharityPhoto(charityDTO.getId())
                    )
            );
        }

        if (initializeDocs) {
            response.setDocuments(
                    charityManager.readCharityDocumentsByType(charityDTO.getId(), DocumentType.CHARITY_DOC)
                            .stream().map(DocumentResponseDTO::new)
                            .collect(Collectors.toList())
            );
        }

        return response;
    }

    private CharityDAO addCharity(CharityDAO charityDAO, CharityRequestDTO charityDTO, Object owner) {

        charityDAO.setName(charityDTO.getName());
        charityDAO.setDescription(charityDTO.getDescription());
        charityDAO.setCategory(charityDTO.getCategory());
        charityDAO.setNeedToCollect(charityDTO.getNeedToCollect());
        charityDAO.setEndDate(charityDTO.getEndDate());
        charityDAO.setOwner(owner);

        return charityDAO;
    }

    private CharityResponseDTO daoToDTO(CharityDAO charityDAO) {

        final CharityResponseDTO response = new CharityResponseDTO<>(charityDAO);

        if (charityDAO instanceof FundCharityDAO) {

            response.setIndividual(false);
            response.setCreatedBy(new FundResponseDTO(((FundCharityDAO) charityDAO).getOwner()));


        } else if (charityDAO instanceof UserCharityDAO) {

            response.setIndividual(true);
            response.setCreatedBy(new UserResponseDTO(((UserCharityDAO) charityDAO).getOwner()));
        }

        return response;
    }

    private Page<CharityDAO> dispatch(String name, CharityStatus status, CharityCategory category, Pageable pageable) {

        if (name == null && status == null && category != null) {
            return charityManager.readAllByCategory(category, pageable);
        } else if (name == null && status != null && category == null) {
            return charityManager.readAllByStatus(status, pageable);
        } else if (name != null && status == null && category == null) {
            return charityManager.readAllByName(name, pageable);
        } else if (name != null && status != null && category == null) {
            return charityManager.readAllByStatusAndName(status, name, pageable);
        } else if (name != null && status == null) {
            return charityManager.readAllByCategoryAndName(category,name,pageable);
        } else if (name == null && status != null) {
            return charityManager.readAllByCategoryAndStatus(category,status,pageable);
        } else if (name != null) {
            return charityManager.readAllByCategoryAndStatusAndName(category, status, name, pageable);
        } else {
            return charityManager.readAll(pageable);
        }

    }

    @PostConstruct
    private void setInitializePhotosFunction() {
        this.initializePhotosFunction = (charity) -> {
            charity.setPhoto(
                    new DocumentResponseDTO(
                            charityManager.readCharityPhoto(charity.getId())
                    )
            );
            return charity;
        };
    }
}
