package formation_sopra.biblio.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import formation_sopra.biblio.model.Avis;

/**
 * Spring Data JPA repository for the Avis entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AvisRepository extends JpaRepository<Avis, Long> {}
