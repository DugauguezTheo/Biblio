package formation_sopra.biblio.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import formation_sopra.biblio.model.Livre;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class LivreRepositoryWithBagRelationshipsImpl implements LivreRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String LIVRES_PARAMETER = "livres";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Livre> fetchBagRelationships(Optional<Livre> livre) {
        return livre.map(this::fetchGenreses);
    }

    @Override
    public Page<Livre> fetchBagRelationships(Page<Livre> livres) {
        return new PageImpl<>(fetchBagRelationships(livres.getContent()), livres.getPageable(), livres.getTotalElements());
    }

    @Override
    public List<Livre> fetchBagRelationships(List<Livre> livres) {
        return Optional.of(livres).map(this::fetchGenreses).orElse(Collections.emptyList());
    }

    Livre fetchGenreses(Livre result) {
        return entityManager
            .createQuery("select livre from Livre livre left join fetch livre.genreses where livre.id = :id", Livre.class)
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Livre> fetchGenreses(List<Livre> livres) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, livres.size()).forEach(index -> order.put(livres.get(index).getId(), index));
        List<Livre> result = entityManager
            .createQuery("select livre from Livre livre left join fetch livre.genreses where livre in :livres", Livre.class)
            .setParameter(LIVRES_PARAMETER, livres)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
