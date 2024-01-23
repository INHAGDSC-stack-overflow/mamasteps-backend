package inhagdsc.mamasteps.user.repository;


import inhagdsc.mamasteps.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {


}
