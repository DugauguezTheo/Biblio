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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import formation_sopra.biblio.dao.LivreRepository;
import formation_sopra.biblio.errors.BadRequestAlertException;
import formation_sopra.biblio.model.Livre;

/**
 * REST controller for managing {@link com.formationsopra.biblio.domain.Livre}.
 */
@RestController
@RequestMapping("/api/livre")
@Transactional
public class LivreResource {

    private static final Logger LOG = LoggerFactory.getLogger(LivreResource.class);

    private static final String ENTITY_NAME = "livre";

    private final LivreRepository livreRepository;

    public LivreResource(LivreRepository livreRepository) {
        this.livreRepository = livreRepository;
    }

    /**
     * {@code POST  /livre} : Create a new livre.
     *
     * @param livre the livre to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new livre, or with status {@code 400 (Bad Request)} if the livre has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Livre> createLivre(@RequestBody Livre livre) throws URISyntaxException {
        LOG.debug("REST request to save Livre : {}", livre);
        if (livre.getId() != null) {
            throw new BadRequestAlertException("A new livre cannot already have an ID", ENTITY_NAME, "idexists");
        }
        livre = livreRepository.save(livre);
        return ResponseEntity.created(new URI("/api/livre/" + livre.getId()))
            .body(livre);
    }

    /**
     * {@code PUT  /livre/:id} : Updates an existing livre.
     *
     * @param id the id of the livre to save.
     * @param livre the livre to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated livre,
     * or with status {@code 400 (Bad Request)} if the livre is not valid,
     * or with status {@code 500 (Internal Server Error)} if the livre couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Livre> updateLivre(@PathVariable(value = "id", required = false) final Long id, @RequestBody Livre livre)
        throws URISyntaxException {
        LOG.debug("REST request to update Livre : {}, {}", id, livre);
        if (livre.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, livre.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!livreRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        livre = livreRepository.save(livre);
        return ResponseEntity.ok()
            .body(livre);
    }

    /**
     * {@code PATCH  /livre/:id} : Partial updates given fields of an existing livre, field will ignore if it is null
     *
     * @param id the id of the livre to save.
     * @param livre the livre to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated livre,
     * or with status {@code 400 (Bad Request)} if the livre is not valid,
     * or with status {@code 404 (Not Found)} if the livre is not found,
     * or with status {@code 500 (Internal Server Error)} if the livre couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Livre> partialUpdateLivre(@PathVariable(value = "id", required = false) final Long id, @RequestBody Livre livre)
        throws URISyntaxException {
        LOG.debug("REST request to partial update Livre partially : {}, {}", id, livre);
        if (livre.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, livre.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!livreRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Livre> result = livreRepository
            .findById(livre.getId())
            .map(existingLivre -> {
                updateIfPresent(existingLivre::setTitre, livre.getTitre());
                updateIfPresent(existingLivre::setResume, livre.getResume());
                updateIfPresent(existingLivre::setAnnee, livre.getAnnee());

                return existingLivre;
            })
            .map(livreRepository::save);

        return result
            .map(updatedLivre -> ResponseEntity.ok().body(updatedLivre))
            .orElseGet(() -> ResponseEntity.notFound().build()
        );
    }

    /**
     * {@code GET  /livre} : get all the Livres.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Livres in body.
     */
    @GetMapping("")
    public List<Livre> getAllLivres(@RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        LOG.debug("REST request to get all Livres");
        if (eagerload) {
            return livreRepository.findAllWithEagerRelationships();
        } else {
            return livreRepository.findAll();
        }
    }

    /**
     * {@code GET  /livre/:id} : get the "id" livre.
     *
     * @param id the id of the livre to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the livre, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Livre> getLivre(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Livre : {}", id);
        Optional<Livre> livre = livreRepository.findOneWithEagerRelationships(id);
        return livre.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * {@code DELETE  /livre/:id} : delete the "id" livre.
     *
     * @param id the id of the livre to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLivre(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Livre : {}", id);
        livreRepository.deleteById(id);
        return ResponseEntity.noContent()
            .build();
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
