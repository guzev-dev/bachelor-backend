package bachelor.proj.charity.bl.services;

import bachelor.proj.charity.bl.dto.request.FundRequestDTO;
import bachelor.proj.charity.bl.dto.request.documents.FundDocumentRequestDTO;
import bachelor.proj.charity.bl.dto.response.DocumentResponseDTO;
import bachelor.proj.charity.bl.dto.response.FundResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FundService {

    FundResponseDTO addFund(FundRequestDTO fundDTO);

    DocumentResponseDTO addDocument(FundDocumentRequestDTO documentDTO);

    FundResponseDTO readById(Long id, boolean initializeLogo, boolean initializeDocs);

    FundResponseDTO readByModeratorId(Long moderatorId, boolean initializeLogo, boolean initializeDocs);

    default Page<FundResponseDTO> readAll(Integer page, Integer size, Sort sortBy) {
        return this.readAll(page, size, sortBy, false);
    }

    Page<FundResponseDTO> readAll(Integer page, Integer size, Sort sortBy, boolean initializeLogos);

    default Page<FundResponseDTO> readAllByParams(String name, String location, Integer page,Integer size, Sort sortBy) {
        return this.readAllByParams(name, location, page, size, sortBy, false);
    }

    Page<FundResponseDTO> readAllByParams(String name, String location, Integer page,Integer size, Sort sortBy, boolean initializeLogos);

    DocumentResponseDTO readDocument(Long documentId);

    List<String> readAvailableLocations();

    FundResponseDTO update(FundRequestDTO fundDTO);

    FundResponseDTO changeModerator(Long fundId, Long newModeratorId);

    void delete(Long fundId);

    void deleteDocument(Long documentId);

}
