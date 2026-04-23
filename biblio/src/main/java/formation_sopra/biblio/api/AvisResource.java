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

import formation_sopra.biblio.dao.AvisRepository;
import formation_sopra.biblio.errors.BadRequestAlertException;
import formation_sopra.biblio.model.Avis;

/**
 * REST controller for managing {@link com.formationsopra.biblio.domain.Avis}.
 */
@RestController
@RequestMapping("/api/avis")
@Transactional
public class AvisResource {

    private static final Logger LOG = LoggerFactory.getLogger(AvisResource.class);

    private static final String ENTITY_NAME = "avis";

    private final AvisRepository avisRepository;

    public AvisResource(AvisRepository avisRepository) {
        this.avisRepository = avisRepository;
    }

    /**
     * {@code POST  /avis} : Create a new avis.
     *
     * @param avis the avis to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new avis, or with status {@code 400 (Bad Request)} if the avis has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Avis> createAvis(@RequestBody Avis avis) throws URISyntaxException {
        LOG.debug("REST request to save Avis : {}", avis);
        if (avis.getId() != null) {
            throw new BadRequestAlertException("A new avis cannot already have an ID", ENTITY_NAME, "idexists");
        }
        avis = avisRepository.save(avis);
        return ResponseEntity.created(new URI("/api/avis/" + avis.getId()))
            .body(avis);
    }

    /**
     * {@code PUT  /avis/:id} : Updates an existing avis.
     *
     * @param id the id of the avis to save.
     * @param avis the avis to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated avis,
     * or with status {@code 400 (Bad Request)} if the avis is not valid,
     * or with status {@code 500 (Internal Server Error)} if the avis couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Avis> updateAvis(@PathVariable(value = "id", required = false) final Long id, @RequestBody Avis avis)
        throws URISyntaxException {
        LOG.debug("REST request to update Avis : {}, {}", id, avis);
        if (avis.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, avis.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!avisRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        avis = avisRepository.save(avis);
        return ResponseEntity.ok()
            .body(avis);
    }

    /**
     * {@code PATCH  /avis/:id} : Partial updates given fields of an existing avis, field will ignore if it is null
     *
     * @param id the id of the avis to save.
     * @param avis the avis to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated avis,
     * or with status {@code 400 (Bad Request)} if the avis is not valid,
     * or with status {@code 404 (Not Found)} if the avis is not found,
     * or with status {@code 500 (Internal Server Error)} if the avis couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Avis> partialUpdateAvis(@PathVariable(value = "id", required = false) final Long id, @RequestBody Avis avis)
        throws URISyntaxException {
        LOG.debug("REST request to partial update Avis partially : {}, {}", id, avis);
        if (avis.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, avis.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!avisRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Avis> result = avisRepository
            .findById(avis.getId())
            .map(existingAvis -> {
                updateIfPresent(existingAvis::setNote, avis.getNote());
                updateIfPresent(existingAvis::setCommentaires, avis.getCommentaires());
                updateIfPresent(existingAvis::setDate, avis.getDate());

                return existingAvis;
            })
            .map(avisRepository::save);

        return result
            .map(updatedAvis -> ResponseEntity.ok().body(updatedAvis))
            .orElseGet(() -> ResponseEntity.notFound().build()
        );
    }

    /**
     * {@code GET  /avis} : get all the Avis.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Avis in body.
     */
    @GetMapping("")
    public List<Avis> getAllAvises() {
        LOG.debug("REST request to get all Avises");
        return avisRepository.findAll();
    }

    /**
     * {@code GET  /avis/:id} : get the "id" avis.
     *
     * @param id the id of the avis to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the avis, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Avis> getAvis(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Avis : {}", id);
        Optional<Avis> avis = avisRepository.findById(id);
        return avis.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * {@code DELETE  /avis/:id} : delete the "id" avis.
     *
     * @param id the id of the avis to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvis(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Avis : {}", id);
        avisRepository.deleteById(id);
        return ResponseEntity.noContent()
            .build();
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
