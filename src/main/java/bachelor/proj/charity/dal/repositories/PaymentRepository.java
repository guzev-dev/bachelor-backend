package bachelor.proj.charity.dal.repositories;

import bachelor.proj.charity.dal.entities.PaymentDAO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentDAO, Long> {

    Page<PaymentDAO> readPaymentDAOSByPaidBy_Id(Long userId, Pageable pageable);

    Page<PaymentDAO> readPaymentDAOByRelatesTo_Id(Long charityId, Pageable pageable);

}
