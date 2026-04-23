package formation_sopra.biblio.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import formation_sopra.biblio.dao.CollectionRepository;
import formation_sopra.biblio.errors.BadRequestAlertException;
import formation_sopra.biblio.model.Collection;

/**
 * REST controller for managing {@link com.formationsopra.biblio.domain.Collection}.
 */
@RestController
@RequestMapping("/api/collections")
@Transactional
public class CollectionResource {

    private static final Logger LOG = LoggerFactory.getLogger(CollectionResource.class);

    private static final String ENTITY_NAME = "collection";

    private final CollectionRepository collectionRepository;

    public CollectionResource(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    /**
     * {@code POST  /collections} : Create a new collection.
     *
     * @param collection the collection to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new collection, or with status {@code 400 (Bad Request)} if the collection has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Collection> createCollection(@RequestBody Collection collection) throws URISyntaxException {
        LOG.debug("REST request to save Collection : {}", collection);
        if (collection.getId() != null) {
            throw new BadRequestAlertException("A new collection cannot already have an ID", ENTITY_NAME, "idexists");
        }
        collection = collectionRepository.save(collection);
        return ResponseEntity.created(new URI("/api/collections/" + collection.getId()))
            .body(collection);
    }

    /**
     * {@code PUT  /collections/:id} : Updates an existing collection.
     *
     * @param id the id of the collection to save.
     * @param collection the collection to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated collection,
     * or with status {@code 400 (Bad Request)} if the collection is not valid,
     * or with status {@code 500 (Internal Server Error)} if the collection couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Collection> updateCollection(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Collection collection
    ) throws URISyntaxException {
        LOG.debug("REST request to update Collection : {}, {}", id, collection);
        if (collection.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, collection.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!collectionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        collection = collectionRepository.save(collection);
        return ResponseEntity.ok()
            .body(collection);
    }

    /**
     * {@code PATCH  /collections/:id} : Partial updates given fields of an existing collection, field will ignore if it is null
     *
     * @param id the id of the collection to save.
     * @param collection the collection to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated collection,
     * or with status {@code 400 (Bad Request)} if the collection is not valid,
     * or with status {@code 404 (Not Found)} if the collection is not found,
     * or with status {@code 500 (Internal Server Error)} if the collection couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Collection> partialUpdateCollection(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Collection collection
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Collection partially : {}, {}", id, collection);
        if (collection.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, collection.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!collectionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Collection> result = collectionRepository
            .findById(collection.getId())
            .map(existingCollection -> {
                updateIfPresent(existingCollection::setNom, collection.getNom());

                return existingCollection;
            })
            .map(collectionRepository::save);

        return result
            .map(updatedCollection -> ResponseEntity.ok().body(updatedCollection))
            .orElseGet(() -> ResponseEntity.notFound().build()
        );
    }

    /**
     * {@code GET  /collections} : get all the Collections.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Collections in body.
     */
    @GetMapping("")
    public List<Collection> getAllCollections() {
        LOG.debug("REST request to get all Collections");
        return collectionRepository.findAll();
    }

    /**
     * {@code GET  /collections/:id} : get the "id" collection.
     *
     * @param id the id of the collection to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the collection, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Collection> getCollection(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Collection : {}", id);
        Optional<Collection> collection = collectionRepository.findById(id);
        return collection.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * {@code DELETE  /collections/:id} : delete the "id" collection.
     *
     * @param id the id of the collection to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollection(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Collection : {}", id);
        collectionRepository.deleteById(id);
        return ResponseEntity.noContent()
            .build();
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
