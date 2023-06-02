package bachelor.proj.charity.dal.repositories;

import bachelor.proj.charity.dal.entities.UserDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserDAO, Long> {

    Optional<UserDAO> findUserDAOByEmail(String email);

}
