package formation_sopra.commande_service.repository;

import formation_sopra.commande_service.model.Commande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandeRepository extends JpaRepository<Commande, Integer> {
}
