package cinema.repository;

import cinema.entity.CinemaChain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CinemaChainRepository extends JpaRepository<CinemaChain, Long> {
}