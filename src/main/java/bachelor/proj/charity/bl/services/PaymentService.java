package bachelor.proj.charity.bl.services;

import bachelor.proj.charity.bl.dto.request.PaymentRequestDTO;
import bachelor.proj.charity.bl.dto.response.PaymentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {

    PaymentResponseDTO addPayment(PaymentRequestDTO paymentDTO);

    Page<PaymentResponseDTO> readCharityPayments(Long charityId, Integer page, Integer size);

    Page<PaymentResponseDTO> readUserPayments(Long userId, Integer page, Integer size);

}
