package formation_sopra.biblio.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import formation_sopra.biblio.model.Auteur;

/**
 * Spring Data JPA repository for the Auteur entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuteurRepository extends JpaRepository<Auteur, Long> {}
