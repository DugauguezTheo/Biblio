package formation_sopra.commande_service.repository;

import formation_sopra.commande_service.model.CommandeDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandeDetailsRepository extends JpaRepository<CommandeDetails, Integer> {
}
