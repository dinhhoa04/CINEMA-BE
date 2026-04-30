package cinema.repository;

import cinema.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Thêm dòng này để Spring Security dùng khi Login
    Optional<User> findByEmail(String email);
}