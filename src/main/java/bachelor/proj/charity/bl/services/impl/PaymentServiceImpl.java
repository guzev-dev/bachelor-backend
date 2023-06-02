package bachelor.proj.charity.bl.services.impl;

import bachelor.proj.charity.bl.dto.request.PaymentRequestDTO;
import bachelor.proj.charity.bl.dto.response.PaymentResponseDTO;
import bachelor.proj.charity.bl.dto.validation.CreateRequest;
import bachelor.proj.charity.bl.exceptions.WrongDataExceptionFactory;
import bachelor.proj.charity.bl.managers.PaymentManager;
import bachelor.proj.charity.bl.services.PaymentService;
import bachelor.proj.charity.dal.entities.CharityDAO;
import bachelor.proj.charity.dal.entities.PaymentDAO;
import bachelor.proj.charity.dal.entities.UserDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentManager paymentManager;
    private final WrongDataExceptionFactory exceptionFactory;

    @Override
    public PaymentResponseDTO addPayment(PaymentRequestDTO paymentDTO) {

        exceptionFactory.checkIfValid(paymentDTO, CreateRequest.class, "Unable to add payment.");

        UserDAO userDAO = new UserDAO();
        userDAO.setId(paymentDTO.getPaidByUserId());

        CharityDAO charityDAO = new CharityDAO() {
            @Override
            public Object getOwner() {
                return null;
            }

            @Override
            public void setOwner(Object owner) {

            }
        };
        charityDAO.setId(paymentDTO.getRelatesToCharityId());

        PaymentDAO dao = new PaymentDAO();
        dao.setPaidBy(userDAO);
        dao.setRelatesTo(charityDAO);
        dao.setAmount(paymentDTO.getAmount());
        dao.setAnonymously(paymentDTO.getAnonymously());

        return new PaymentResponseDTO(paymentManager.create(dao));
    }

    @Override
    public Page<PaymentResponseDTO> readCharityPayments(Long charityId, Integer page, Integer size) {
        return paymentManager.readAllByCharityId(charityId, PageRequest.of(page,size,Sort.by("id").descending()))
                .map(PaymentResponseDTO::new);
    }

    @Override
    public Page<PaymentResponseDTO> readUserPayments(Long userId, Integer page, Integer size) {
        return paymentManager.readAllByUserId(userId, PageRequest.of(page,size,Sort.by("id").descending()))
                .map(PaymentResponseDTO::new);
    }
}
