package bachelor.proj.charity.pl.controllers;

import bachelor.proj.charity.bl.dto.request.CharityRequestDTO;
import bachelor.proj.charity.bl.dto.request.FundCharityRequestDTO;
import bachelor.proj.charity.bl.dto.request.UserCharityRequestDTO;
import bachelor.proj.charity.bl.dto.request.documents.CharityDocumentRequestDTO;
import bachelor.proj.charity.bl.dto.response.CharityResponseDTO;
import bachelor.proj.charity.bl.services.CharityService;
import bachelor.proj.charity.bl.services.FundService;
import bachelor.proj.charity.pl.authorization.jwt.JWTokenProvider;
import bachelor.proj.charity.shared.enums.CharityCategory;
import bachelor.proj.charity.shared.enums.CharityStatus;
import bachelor.proj.charity.shared.enums.sort.CharitySortedBy;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/charity/api/charity")
@CrossOrigin(
        origins = "http://localhost:4200",
        allowedHeaders = {"Authorization", "Content-Type"},
        methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE}
)
@RequiredArgsConstructor
public class CharityController {

    private final CharityService charityService;

    private final FundService fundService;

    private final JWTokenProvider tokenProvider;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/fund")
    public ResponseEntity<?> createFundCharity(@RequestBody FundCharityRequestDTO charityDTO,
                                               HttpServletRequest request) {

        final Long authenticatedUserId = tokenProvider.getUserId(request),
                fundId = fundService.readByModeratorId(authenticatedUserId, false, false).getId();

        charityDTO.setFundId(fundId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(charityService.addCharity(charityDTO));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/user")
    public ResponseEntity<?> createUserCharity(@RequestBody UserCharityRequestDTO charityDTO,
                                               HttpServletRequest request) {

        final Long authenticatedUserId = tokenProvider.getUserId(request);

        charityDTO.setUserId(authenticatedUserId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(charityService.addCharity(charityDTO));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{charityId}/docs")
    public ResponseEntity<?> addCharityDocument(@PathVariable(name = "charityId") Long charityId,
                                                @RequestBody CharityDocumentRequestDTO documentDTO,
                                                HttpServletRequest request) {

        final Long authenticatedUserId = tokenProvider.getUserId(request);
        final CharityResponseDTO<?> charity = charityService.readById(charityId, false, false);

        if (charity.getCreatedBy().equals(authenticatedUserId)) {
            documentDTO.setCharityId(charityId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(charityService.addDocument(documentDTO));
        } else
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Авторизований користувач не є власником благодійного збору.");
    }

    @GetMapping("/{charityId}")
    public ResponseEntity<?> readCharity(@PathVariable(name = "charityId") Long charityId,
                                         @RequestParam(value = "initializePhoto", defaultValue = "false") Boolean initializePhoto,
                                         @RequestParam(value = "initializeDocs", defaultValue = "false") Boolean initializeDocs) {

        return ResponseEntity.ok(
                charityService.readById(charityId, initializePhoto, initializeDocs)
        );
    }

    @GetMapping
    public ResponseEntity<?> readCharities(@RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "status", required = false) String status,
                                           @RequestParam(value = "category", required = false) String category,
                                           @RequestParam(value = "initializePhotos", defaultValue = "false") Boolean initializePhotos,
                                           @RequestParam(value = "page", defaultValue = "0") Integer page,
                                           @RequestParam(value = "size", defaultValue = "5") Integer size,
                                           @RequestParam(value = "sortBy", required = false) String sortBy) {
        final CharityStatus charityStatus;
        if (status != null)
            charityStatus = CharityStatus.getByName(status);
        else
            charityStatus = null;

        final CharityCategory charityCategory;
        if (category != null)
            charityCategory = CharityCategory.getByName(category);
        else
            charityCategory = null;

        final CharitySortedBy sortedBy = getCharitySortedBy(sortBy);


        return ResponseEntity.ok(
                charityService.readAllByParams(name, charityStatus, charityCategory, page, size, sortedBy.getSort(), initializePhotos)
        );
    }

    @GetMapping("/fund/{fundId}")
    public ResponseEntity<?> readFundCharities(@PathVariable(name = "fundId") Long fundId,
                                               @RequestParam(value = "initializePhotos", defaultValue = "false") Boolean initializePhotos,
                                               @RequestParam(value = "page", defaultValue = "0") Integer page,
                                               @RequestParam(value = "size", defaultValue = "5") Integer size,
                                               @RequestParam(value = "sortBy", required = false) String sortBy) {

        final CharitySortedBy sortedBy = getCharitySortedBy(sortBy);

        return ResponseEntity.ok(
                charityService.readAllByFundId(fundId, page, size, sortedBy.getSort(), initializePhotos)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> readUserCharities(@PathVariable(name = "userId") Long userId,
                                               @RequestParam(value = "initializePhotos", defaultValue = "false") Boolean initializePhotos,
                                               @RequestParam(value = "page", defaultValue = "0") Integer page,
                                               @RequestParam(value = "size", defaultValue = "5") Integer size,
                                               @RequestParam(value = "sortBy", required = false) String sortBy) {

        final CharitySortedBy sortedBy = getCharitySortedBy(sortBy);

        return ResponseEntity.ok(
                charityService.readAllByUserId(userId, page, size, sortedBy.getSort(), initializePhotos)
        );
    }

    @GetMapping("/documents/{documentId}")
    public ResponseEntity<?> readDocument(@PathVariable(name = "documentId") Long documentId) {
        return ResponseEntity.ok(
                charityService.readDocument(documentId)
        );
    }

    @GetMapping("/orders")
    public ResponseEntity<?> readCharityOrders() {
        return ResponseEntity.ok(
                Arrays.stream(CharitySortedBy.values())
                        .map(CharitySortedBy::getName)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/statuses")
    public ResponseEntity<?> readCharityStatuses() {
        return ResponseEntity.ok(
                Arrays.stream(CharityStatus.values())
                        .map(CharityStatus::getResponse)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/categories")
    public ResponseEntity<?> readCharityCategories() {
        return ResponseEntity.ok(
                Arrays.stream(CharityCategory.values())
                        .map(CharityCategory::getResponse)
                        .collect(Collectors.toList())
        );
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{charityId}/vote")
    public ResponseEntity<?> voteForCharity(@PathVariable(name = "charityId") Long charityId,
                                            HttpServletRequest request) {

        final Long authenticatedUserId = tokenProvider.getUserId(request);

        return ResponseEntity.ok(
                Map.of("accepted", charityService.upvoteForCharity(charityId, authenticatedUserId))
        );
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{charityId}/status")
    public ResponseEntity<?> changeStatus(@PathVariable(name = "charityId") Long charityId,
                                          @RequestParam(name = "status") String status,
                                          HttpServletRequest request) {

        final Long authenticatedUserId = tokenProvider.getUserId(request);
        final CharityResponseDTO<?> charity = charityService.readById(charityId, false, false);

        if (charity.getCreatedBy().equals(authenticatedUserId)) {

            return ResponseEntity.ok(
                    Map.of("accepted", charityService.changeStatus(charityId, CharityStatus.getByName(status)))
            );
        } else
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Авторизований користувач не є власником благодійного збору.");
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{charityId}")
    public ResponseEntity<?> updateCharity(@RequestBody CharityRequestDTO charityDTO,
                                           @PathVariable(name = "charityId") Long charityId,
                                           @RequestParam(value = "initializePhoto", defaultValue = "false") Boolean initializePhoto,
                                           @RequestParam(value = "initializeDocs", defaultValue = "false") Boolean initializeDocs,
                                           HttpServletRequest request) {

        final Long authenticatedUserId = tokenProvider.getUserId(request);
        final CharityResponseDTO<?> charity = charityService.readById(charityId, false, false);

        if (charity.getCreatedBy().equals(authenticatedUserId)) {

            return ResponseEntity.ok(
                    charityService.update(charityDTO, initializePhoto, initializeDocs)
            );
        } else
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Авторизований користувач не є власником благодійного збору.");
    }

    private CharitySortedBy getCharitySortedBy(String sortBy) {
        final CharitySortedBy sortedBy;

        if (sortBy != null)
            sortedBy = CharitySortedBy.getByName(sortBy);
        else
            sortedBy = CharitySortedBy.values()[0];
        return sortedBy;
    }
}
