package bachelor.proj.charity.bl.managers.impl;

import bachelor.proj.charity.bl.managers.UserManager;
import bachelor.proj.charity.dal.entities.UserDAO;
import bachelor.proj.charity.dal.entities.documents.UserProfilePhotoDAO;
import bachelor.proj.charity.dal.repositories.UserProfilePhotoRepository;
import bachelor.proj.charity.dal.repositories.UserRepository;
import bachelor.proj.charity.shared.enums.UserRole;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class UserManagerImpl implements UserManager {

    private final UserRepository userRepository;
    private final UserProfilePhotoRepository profilePhotoRepository;
    private final EntityManager entityManager;

    private final Supplier<? extends RuntimeException> cannotFindUserException =
            () -> new EntityNotFoundException("Can't find a user with such ID.");
    private Map<String, Object> forcePhotoInitializationProperties;

    @Override
    public UserProfilePhotoDAO createUserProfilePhoto(UserProfilePhotoDAO document, Long userId) {

        //check if user already has profile photo
        if (profilePhotoRepository.existsById(userId)) {
            //delete old photo if exists
            deletePhoto(userId);
        }

        //set user id and save
        document.setUser(
                userRepository.findById(userId).get()
        );
        //return created photo
        return profilePhotoRepository.save(document);
    }

    @Override
    public UserDAO readById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(cannotFindUserException);
    }

    @Override
    @Cacheable(value="user-details_cache", cacheManager = "userCacheManager")
    public UserDAO readByEmail(String email) {
        return userRepository.findUserDAOByEmail(email)
                        .orElseThrow(cannotFindUserException);
    }

    @Override
    public UserProfilePhotoDAO readPhoto(Long userId) {
        return entityManager.find(UserProfilePhotoDAO.class, userId, this.forcePhotoInitializationProperties);
    }

    @Override
    public UserDAO update(UserDAO user) {
        //find user to update
        UserDAO userToUpdate = userRepository
                .findById(user.getId())
                .orElseThrow(cannotFindUserException);
        boolean isChanged = false;

        //compare user data and if it differs -> change
        if (!userToUpdate.getFirstName().equals(user.getFirstName())) {
            userToUpdate.setFirstName(user.getFirstName());
            isChanged = true;
        }
        if (!userToUpdate.getLastName().equals(user.getLastName())) {
            userToUpdate.setLastName(user.getLastName());
            isChanged = true;
        }

        //if there are changes -> save them and return changed user, otherwise -> just return unchanged user
        if (isChanged) {
            userToUpdate = userRepository.save(userToUpdate);
        }

        return userToUpdate;
    }

    @Override
    public UserDAO changeUserRole(UserRole role, Long userId) {
        //find user to update
        UserDAO userDAO = userRepository
                .findById(userId)
                .orElseThrow(cannotFindUserException);

        //change role and save
        userDAO.setRole(role);
        //return changed user
        return userRepository.save(userDAO);
    }

    @Override
    public UserDAO changeUpvoteDate(Long userId) {
        //find user to update
        UserDAO userDAO = userRepository
                .findById(userId)
                .orElseThrow(cannotFindUserException);

        //change upvote date
        userDAO.setUpvoteDate(LocalDate.now());
        //return changed user
        return userRepository.save(userDAO);
    }

    @Override
    public void deletePhoto(Long photoId) {
        profilePhotoRepository.deleteById(photoId);
    }

    @PostConstruct
    private void setForcePhotoInitializationProperties() {
        EntityGraph<UserProfilePhotoDAO> entityGraph = entityManager.createEntityGraph(UserProfilePhotoDAO.class);
        entityGraph.addAttributeNodes("body");
        Map<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.fetchgraph", entityGraph);
        this.forcePhotoInitializationProperties = properties;
    }

}
