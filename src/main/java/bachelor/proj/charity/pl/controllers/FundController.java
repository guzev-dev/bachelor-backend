package bachelor.proj.charity.pl.controllers;

import bachelor.proj.charity.bl.dto.request.FundRequestDTO;
import bachelor.proj.charity.bl.dto.request.documents.FundDocumentRequestDTO;
import bachelor.proj.charity.bl.dto.response.DocumentResponseDTO;
import bachelor.proj.charity.bl.dto.response.FundResponseDTO;
import bachelor.proj.charity.bl.services.FundService;
import bachelor.proj.charity.pl.authorization.jwt.JWTokenProvider;
import bachelor.proj.charity.shared.enums.sort.FundSortedBy;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.text.Collator;

@RestController
@RequestMapping("/charity/api/funds")
@CrossOrigin(
        origins = "http://localhost:4200",
        allowedHeaders = {"Authorization", "Content-Type"},
        methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE}
)
@RequiredArgsConstructor
public class FundController {

    private final FundService fundService;

    private final JWTokenProvider tokenProvider;

    private final Collator localeCollator = Collator.getInstance(new Locale("uk","UA"));

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<?> createFund(FundRequestDTO fundDTO,
                                        HttpServletRequest request) {

        final Long authenticatedUserId = tokenProvider.getUserId(request);

        fundDTO.setModeratorId(authenticatedUserId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fundService.addFund(fundDTO));

    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/docs")
    public ResponseEntity<?> addFundDocument(FundDocumentRequestDTO documentDTO,
                                             HttpServletRequest request) {

        final Long authenticatedUserId = tokenProvider.getUserId(request);
        documentDTO.setFundId(
                fundService.readByModeratorId(authenticatedUserId, false, false).getId()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fundService.addDocument(documentDTO));
    }

    @GetMapping("/{fundId}")
    public ResponseEntity<?> readFund(@PathVariable(name = "fundId") Long fundId,
                                      @RequestParam(value = "initializeLogo", defaultValue = "false") Boolean initializeLogo,
                                      @RequestParam(value = "initializeDocs", defaultValue = "false") Boolean initializeDocs) {

        return ResponseEntity.ok(
                fundService.readById(fundId, initializeLogo, initializeDocs)
        );
    }

    @GetMapping
    public ResponseEntity<?> readFunds(@RequestParam(value = "name", required = false) String name,
                                       @RequestParam(value = "location", required = false) String location,
                                       @RequestParam(value = "initializeLogos", defaultValue = "false") Boolean initializeLogos,
                                       @RequestParam(value = "page", defaultValue = "0") Integer page,
                                       @RequestParam(value = "size", defaultValue = "5") Integer size,
                                       @RequestParam(value = "sortBy", required = false) String sortBy) {

        final FundSortedBy sortedBy;

        if (sortBy != null && !sortBy.isEmpty())
            sortedBy = FundSortedBy.getByName(sortBy);
        else
            sortedBy = FundSortedBy.values()[0];


        return ResponseEntity.ok(
                fundService.readAllByParams(name, location, page, size, sortedBy.getSort(), initializeLogos)
        );

    }

    @GetMapping("/order")
    public ResponseEntity<?> readFundsOrder() {
        return ResponseEntity.ok(
                Arrays.stream(FundSortedBy.values())
                        .map(FundSortedBy::getName)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/locations")
    public ResponseEntity<?> readFundsLocations() {
        return ResponseEntity.ok(
                fundService.readAvailableLocations()
                        .stream().sorted(localeCollator)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/documents/{documentId}")
    public ResponseEntity<?> readDocument(@PathVariable(name = "documentId") Long documentId) {
        return ResponseEntity.ok(
                fundService.readDocument(documentId)
        );
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping
    public ResponseEntity<?> updateFund(FundRequestDTO fundDTO,
                                        HttpServletRequest request) {

        final Long authenticatedUserId = tokenProvider.getUserId(request);

        try {
            final Long fundToUpdateId = fundService.readByModeratorId(authenticatedUserId, false, false).getId();

            if (fundToUpdateId.equals(fundDTO.getId()))
                return ResponseEntity.ok(
                        fundService.update(fundDTO)
                );
            else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Користувач не є модератором оновлюваного фонду.");
        } catch (EntityNotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не вдається знайти фонд з вказаним ідентифікатором.");
        }

    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{fundId}")
    public ResponseEntity<?> deleteFund(@PathVariable(name = "fundId") Long fundId,
                                        HttpServletRequest request) {

        final Long authenticatedUserId = tokenProvider.getUserId(request);

        try {
            final Long moderatedFundId = fundService.readByModeratorId(authenticatedUserId, false, false).getId();

            if (fundId.equals(moderatedFundId)) {
                fundService.delete(fundId);
                return ResponseEntity.ok(
                        Map.of("deleted", true)
                );
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Користувач не є модератором оновлюваного фонду.");
        } catch (EntityNotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не вдається знайти фонд з вказаним ідентифікатором.");
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/documents/{documentId}")
    public ResponseEntity<?> deleteDocument(@PathVariable(name = "documentId") Long documentId,
                                            HttpServletRequest request) {

        final Long authenticatedUserId = tokenProvider.getUserId(request);


        final FundResponseDTO moderatedFund =
                fundService.readByModeratorId(authenticatedUserId, true, true);
        if (moderatedFund.getLogo().getId().equals(documentId)
                || moderatedFund.getDocuments().stream().map(DocumentResponseDTO::getId).anyMatch(id -> id.equals(documentId))) {
            fundService.deleteDocument(documentId);
            return ResponseEntity.ok(
                    Map.of("deleted", true)
            );
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не вдається знайти документ з вказаним ідентифікатором серед документів фонду.");
    }
}
