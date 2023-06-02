package bachelor.proj.charity.bl.managers.impl;

import bachelor.proj.charity.bl.managers.CharityManager;
import bachelor.proj.charity.bl.managers.PaymentManager;
import bachelor.proj.charity.dal.entities.PaymentDAO;
import bachelor.proj.charity.dal.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentsManagerImpl implements PaymentManager {

    private final PaymentRepository paymentRepository;
    private final CharityManager charityManager;

    @Override
    public PaymentDAO create(PaymentDAO payment) {

        //save payment to db
        final PaymentDAO result = paymentRepository.save(payment);

        //change charity collected value
        charityManager.addMoney(payment.getRelatesTo().getId(), payment.getAmount());

        //return created payment
        return result;
    }

    @Override
    public Page<PaymentDAO> readAllByCharityId(Long charityId, Pageable pageable) {
        return paymentRepository.readPaymentDAOByRelatesTo_Id(charityId, pageable);
    }

    @Override
    public Page<PaymentDAO> readAllByUserId(Long userId, Pageable pageable) {
        return paymentRepository.readPaymentDAOSByPaidBy_Id(userId, pageable);
    }

}
