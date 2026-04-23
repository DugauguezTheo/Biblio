package formation_sopra.biblio.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import formation_sopra.biblio.model.Editeur;

/**
 * Spring Data JPA repository for the Editeur entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EditeurRepository extends JpaRepository<Editeur, Long> {}
