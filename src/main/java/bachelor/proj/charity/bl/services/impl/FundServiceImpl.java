package bachelor.proj.charity.bl.services.impl;

import bachelor.proj.charity.bl.dto.request.FundRequestDTO;
import bachelor.proj.charity.bl.dto.request.documents.FundDocumentRequestDTO;
import bachelor.proj.charity.bl.dto.response.DocumentResponseDTO;
import bachelor.proj.charity.bl.dto.response.FundResponseDTO;
import bachelor.proj.charity.bl.dto.validation.CreateRequest;
import bachelor.proj.charity.bl.dto.validation.UpdateRequest;
import bachelor.proj.charity.bl.exceptions.WrongDataExceptionFactory;
import bachelor.proj.charity.bl.managers.FundManager;
import bachelor.proj.charity.bl.services.FundService;
import bachelor.proj.charity.dal.entities.FundDAO;
import bachelor.proj.charity.dal.entities.UserDAO;
import bachelor.proj.charity.dal.entities.documents.DocumentBodyDAO;
import bachelor.proj.charity.dal.entities.documents.FundDocumentDAO;
import bachelor.proj.charity.shared.enums.DocumentType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FundServiceImpl implements FundService {

    private final FundManager fundManager;
    private final WrongDataExceptionFactory exceptionFactory;

    @Override
    public FundResponseDTO addFund(FundRequestDTO fundDTO) {

        exceptionFactory.checkIfValid(fundDTO, CreateRequest.class, "Unable to add fund.");

        UserDAO userDAO = new UserDAO();
        userDAO.setId(fundDTO.getModeratorId());

        FundDAO dao = new FundDAO();
        dao.setName(fundDTO.getName());
        dao.setDescription(fundDTO.getDescription());
        dao.setContactNumber(fundDTO.getContactNumber());
        dao.setLocations(fundDTO.getLocations());
        dao.setCategories(fundDTO.getCategories());
        dao.setModerator(userDAO);

        return new FundResponseDTO(fundManager.create(dao));
    }

    @Override
    public DocumentResponseDTO addDocument(FundDocumentRequestDTO documentDTO) {

        exceptionFactory.checkIfValid(documentDTO, CreateRequest.class, "Unable to add document.");

        DocumentBodyDAO documentBodyDAO = new DocumentBodyDAO();
        documentBodyDAO.setExtension(documentDTO.getExtension());
        documentBodyDAO.setContent(documentDTO.getContent());

        FundDocumentDAO dao = new FundDocumentDAO();
        dao.setBody(documentBodyDAO);
        dao.setName(documentDTO.getName());
        dao.setType(documentDTO.getType());

        return new DocumentResponseDTO(fundManager.createDocument(dao, documentDTO.getFundId()));
    }

    @Override
    public FundResponseDTO readById(Long id, boolean initializeLogo, boolean initializeDocs) {

        return this.readFund(fundManager.readById(id), initializeLogo, initializeDocs);
    }

    @Override
    public FundResponseDTO readByModeratorId(Long moderatorId, boolean initializeLogo, boolean initializeDocs) {

        return this.readFund(fundManager.readByModerator(moderatorId), initializeLogo, initializeDocs);
    }

    @Override
    public Page<FundResponseDTO> readAll(Integer page, Integer size, Sort sortBy, boolean initializeLogos) {
        Page<FundResponseDTO> response = fundManager.readAll(PageRequest.of(page, size, sortBy))
                .map(FundResponseDTO::new);

        if (initializeLogos)
            return response
                    .map(fund -> {
                        fund.setLogo(
                                new DocumentResponseDTO(
                                        fundManager.readFundLogo(fund.getId())
                                )
                        );
                        return fund;
                    });
        else
            return response;
    }

    @Override
    public Page<FundResponseDTO> readAllByParams(String name, String location, Integer page, Integer size, Sort sortBy, boolean initializeLogos) {

        final Pageable pageRequest = PageRequest.of(page, size, sortBy);
        Page<FundDAO> result;

        if (name == null && location == null) {
            result = fundManager.readAll(pageRequest);
        } else if (name != null && location == null) {
            result = fundManager.readAllByName(name, pageRequest);
        } else if (name == null && location != null) {
            result = fundManager.readAllByLocation(location, pageRequest);
        } else {
            result = fundManager.readAllByNameAndLocation(name, location, pageRequest);
        }

        Page<FundResponseDTO> response = result.map(FundResponseDTO::new);

        if (initializeLogos)
            return response
                    .map(fund -> {
                        fund.setLogo(
                                new DocumentResponseDTO(
                                        fundManager.readFundLogo(fund.getId())
                                )
                        );
                        return fund;
                    });
        else
            return response;
    }

    @Override
    public DocumentResponseDTO readDocument(Long documentId) {
        return new DocumentResponseDTO(fundManager.readDocumentWithBody(documentId));
    }

    @Override
    public List<String> readAvailableLocations() {
        return fundManager.readAvailableLocations();
    }

    @Override
    public FundResponseDTO update(FundRequestDTO fundDTO) {

        exceptionFactory.checkIfValid(fundDTO, UpdateRequest.class, "Unable to update fund.");

        FundDAO dao = new FundDAO();
        dao.setId(fundDTO.getId());
        dao.setName(fundDTO.getName());
        dao.setDescription(fundDTO.getDescription());
        dao.setContactNumber(fundDTO.getContactNumber());
        dao.setLocations(fundDTO.getLocations());
        dao.setCategories(fundDTO.getCategories());

        return new FundResponseDTO(fundManager.update(dao));
    }

    @Override
    public FundResponseDTO changeModerator(Long fundId, Long newModeratorId) {

        return new FundResponseDTO(fundManager.changeModerator(fundId, newModeratorId));
    }

    @Override
    public void delete(Long fundId) {

        fundManager.delete(fundId);
    }

    @Override
    public void deleteDocument(Long documentId) {

        fundManager.deleteDocument(documentId);
    }

    private FundResponseDTO readFund(FundDAO dao, boolean initializeLogo, boolean initializeDocs) {
        final FundResponseDTO response = new FundResponseDTO(dao);

        if (initializeLogo)
            response.setLogo(
                    new DocumentResponseDTO(
                            fundManager.readFundLogo(dao.getId())
                    )
            );

        if (initializeDocs)
            response.setDocuments(
                    fundManager.readFundDocumentsByType(dao.getId(), DocumentType.FUND_DOC)
                            .stream().map(DocumentResponseDTO::new)
                            .collect(Collectors.toList())
            );

        return response;
    }
}
