package bachelor.proj.charity.dal.repositories;

import bachelor.proj.charity.dal.entities.documents.UserProfilePhotoDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfilePhotoRepository extends JpaRepository<UserProfilePhotoDAO, Long> {
}
