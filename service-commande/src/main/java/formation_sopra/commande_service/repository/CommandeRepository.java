package formation_sopra.commande_service.repository;

import formation_sopra.commande_service.model.Commande;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import feign.Param;

public interface CommandeRepository extends JpaRepository<Commande, Integer> {

    @Query("Select c from Commande c where c.clientId = :id")
    public List<Commande> findAllByClientId(@Param("id") Integer idClient);
}
