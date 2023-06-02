package bachelor.proj.charity.bl.authentication;

import bachelor.proj.charity.bl.managers.UserManager;
import bachelor.proj.charity.dal.entities.UserDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserManager userManager;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        final UserDAO dao = userManager.readByEmail(username);

        if (dao != null)
            return new UserDetailsImpl(dao.getEmail(), dao.getPassword(), dao.getRole());
        else
            throw new UsernameNotFoundException("Користувача з вказаною електронною адресою не знайдено.");
    }

}
