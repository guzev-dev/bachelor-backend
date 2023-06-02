package bachelor.proj.charity.pl.controllers;

import bachelor.proj.charity.bl.dto.request.PaymentRequestDTO;
import bachelor.proj.charity.bl.services.PaymentService;
import bachelor.proj.charity.pl.authorization.jwt.JWTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/charity/api/payments")
@CrossOrigin(
        origins = "http://localhost:4200",
        allowedHeaders = {"Authorization", "Content-Type"},
        methods = {RequestMethod.POST, RequestMethod.GET}
)
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    private final JWTokenProvider tokenProvider;

    @PostMapping("/create")
    public ResponseEntity<?> create(PaymentRequestDTO paymentDTO,
                                    HttpServletRequest request) {

        final Long authenticatedUserId = tokenProvider.getUserId(request);

        paymentDTO.setPaidByUserId(authenticatedUserId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.addPayment(paymentDTO));
    }

    @GetMapping("/charity/{charityId}")
    public ResponseEntity<?> readCharityPayments(@PathVariable("charityId") Long charityId,
                                                 @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                 @RequestParam(value = "size", defaultValue = "5") Integer size) {

        return ResponseEntity.ok(paymentService.readCharityPayments(charityId, page, size));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user")
    public ResponseEntity<?> readUserPayments(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                              @RequestParam(value = "size", defaultValue = "5") Integer size,
                                              HttpServletRequest request) {

        final Long authenticatedUserId = tokenProvider.getUserId(request);

        return ResponseEntity.ok(paymentService.readUserPayments(authenticatedUserId, page, size));
    }

}
