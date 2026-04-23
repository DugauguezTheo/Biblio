package formation_sopra.biblio.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import formation_sopra.biblio.model.Livre;

public interface LivreRepositoryWithBagRelationships {
    Optional<Livre> fetchBagRelationships(Optional<Livre> livre);

    List<Livre> fetchBagRelationships(List<Livre> livres);

    Page<Livre> fetchBagRelationships(Page<Livre> livres);
}
