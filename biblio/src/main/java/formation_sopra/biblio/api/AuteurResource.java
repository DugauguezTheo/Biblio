package formation_sopra.biblio.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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

import formation_sopra.biblio.dao.AuteurRepository;
import formation_sopra.biblio.errors.BadRequestAlertException;
import formation_sopra.biblio.model.Auteur;

/**
 * REST controller for managing {@link com.formationsopra.biblio.domain.Auteur}.
 */
@RestController
@RequestMapping("/api/auteur")
@Transactional
public class AuteurResource {

    private static final Logger LOG = LoggerFactory.getLogger(AuteurResource.class);

    private static final String ENTITY_NAME = "auteur";

    private final AuteurRepository auteurRepository;

    public AuteurResource(AuteurRepository auteurRepository) {
        this.auteurRepository = auteurRepository;
    }

    /**
     * {@code POST  /auteur} : Create a new auteur.
     *
     * @param auteur the auteur to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new auteur, or with status {@code 400 (Bad Request)} if the auteur has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Auteur> createAuteur(@RequestBody Auteur auteur) throws URISyntaxException {
        LOG.debug("REST request to save Auteur : {}", auteur);
        if (auteur.getId() != null) {
            throw new BadRequestAlertException("A new auteur cannot already have an ID", ENTITY_NAME, "idexists");
        }
        auteur = auteurRepository.save(auteur);
        return ResponseEntity.created(new URI("/api/auteur/" + auteur.getId()))
            .body(auteur);
    }

    /**
     * {@code PUT  /auteur/:id} : Updates an existing auteur.
     *
     * @param id the id of the auteur to save.
     * @param auteur the auteur to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auteur,
     * or with status {@code 400 (Bad Request)} if the auteur is not valid,
     * or with status {@code 500 (Internal Server Error)} if the auteur couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Auteur> updateAuteur(@PathVariable(value = "id", required = false) final Long id, @RequestBody Auteur auteur)
        throws URISyntaxException {
        LOG.debug("REST request to update Auteur : {}, {}", id, auteur);
        if (auteur.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auteur.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!auteurRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        auteur = auteurRepository.save(auteur);
        return ResponseEntity.ok()
            .body(auteur);
    }

    /**
     * {@code PATCH  /auteur/:id} : Partial updates given fields of an existing auteur, field will ignore if it is null
     *
     * @param id the id of the auteur to save.
     * @param auteur the auteur to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auteur,
     * or with status {@code 400 (Bad Request)} if the auteur is not valid,
     * or with status {@code 404 (Not Found)} if the auteur is not found,
     * or with status {@code 500 (Internal Server Error)} if the auteur couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Auteur> partialUpdateAuteur(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Auteur auteur
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Auteur partially : {}, {}", id, auteur);
        if (auteur.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auteur.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!auteurRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Auteur> result = auteurRepository
            .findById(auteur.getId())
            .map(existingAuteur -> {
                updateIfPresent(existingAuteur::setNom, auteur.getNom());
                updateIfPresent(existingAuteur::setPrenom, auteur.getPrenom());
                updateIfPresent(existingAuteur::setNationalite, auteur.getNationalite());

                return existingAuteur;
            })
            .map(auteurRepository::save);

        return result
            .map(updatedAuteur -> ResponseEntity.ok().body(updatedAuteur))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * {@code GET  /auteur} : get all the Auteurs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Auteurs in body.
     */
    @GetMapping("")
    public List<Auteur> getAllAuteurs() {
        LOG.debug("REST request to get all Auteurs");
        return auteurRepository.findAll();
    }

    /**
     * {@code GET  /auteur/:id} : get the "id" auteur.
     *
     * @param id the id of the auteur to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the auteur, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Auteur> getAuteur(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Auteur : {}", id);
        Optional<Auteur> auteur = auteurRepository.findById(id);
        return auteur.map(response -> ResponseEntity.ok().body(response))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * {@code DELETE  /auteur/:id} : delete the "id" auteur.
     *
     * @param id the id of the auteur to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuteur(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Auteur : {}", id);
        auteurRepository.deleteById(id);
        return ResponseEntity.noContent()
            .build();
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
