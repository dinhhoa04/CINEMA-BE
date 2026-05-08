// SeatTypeRepository.java
package cinema.repository;
import cinema.entity.SeatType;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SeatTypeRepository extends JpaRepository<SeatType, Byte> {}