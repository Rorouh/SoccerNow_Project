package pt.ul.fc.css.soccernow.repository;

import pt.ul.fc.css.soccernow.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
