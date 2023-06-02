package bachelor.proj.charity.bl.services;

import bachelor.proj.charity.bl.dto.request.CharityRequestDTO;
import bachelor.proj.charity.bl.dto.request.FundCharityRequestDTO;
import bachelor.proj.charity.bl.dto.request.UserCharityRequestDTO;
import bachelor.proj.charity.bl.dto.request.documents.CharityDocumentRequestDTO;
import bachelor.proj.charity.bl.dto.response.CharityResponseDTO;
import bachelor.proj.charity.bl.dto.response.DocumentResponseDTO;
import bachelor.proj.charity.shared.enums.CharityCategory;
import bachelor.proj.charity.shared.enums.CharityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@SuppressWarnings("rawtypes")

@Service
public interface CharityService {

    CharityResponseDTO addCharity(FundCharityRequestDTO fundCharityDTO);

    CharityResponseDTO addCharity(UserCharityRequestDTO userCharityDTO);

    DocumentResponseDTO addDocument(CharityDocumentRequestDTO documentDTO);

    CharityResponseDTO readById(Long id, boolean initializePhoto, boolean initializeDocs);

    Page<CharityResponseDTO> readAll(Integer page, Integer size, Sort sortBy, boolean initializePhotos);

    Page<CharityResponseDTO> readAllByParams(String name, CharityStatus status, CharityCategory category,
                                             Integer page, Integer size, Sort sortBy,
                                             boolean initializePhotos);

    Page<CharityResponseDTO> readAllByFundId(Long fundId, Integer page, Integer size, Sort sortBy, boolean initializePhotos);

    Page<CharityResponseDTO> readAllByUserId(Long userId, Integer page, Integer size, Sort sortBy, boolean initializePhotos);

    DocumentResponseDTO readDocument(Long documentId);

    boolean upvoteForCharity(Long charityId, Long userId);

    boolean changeStatus(Long charityId, CharityStatus status);

    CharityResponseDTO update(CharityRequestDTO charityDTO, boolean initializePhoto, boolean initializeDocs);
}
