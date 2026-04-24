package formation_sopra.biblio.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import formation_sopra.biblio.model.Livre;

/**
 * Spring Data JPA repository for the Livre entity.
 *
 * When extending this class, extend LivreRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface LivreRepository extends JpaRepository<Livre, Long> {

}
