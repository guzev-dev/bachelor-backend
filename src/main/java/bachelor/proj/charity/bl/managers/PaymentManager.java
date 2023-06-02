package bachelor.proj.charity.bl.managers;

import bachelor.proj.charity.dal.entities.PaymentDAO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface PaymentManager {

    PaymentDAO create(PaymentDAO payment);

    Page<PaymentDAO> readAllByCharityId(Long charityId, Pageable pageable);

    Page<PaymentDAO> readAllByUserId(Long userId, Pageable pageable);

}
