package inhagdsc.mamasteps.user.repository;


import inhagdsc.mamasteps.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u JOIN FETCH u.walkPreferences WHERE u.id = :userId")
    Optional<User> findUserWithWalkPreferences(@Param("userId") Long userId);


}
