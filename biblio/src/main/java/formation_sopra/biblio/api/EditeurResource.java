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

import formation_sopra.biblio.dao.EditeurRepository;
import formation_sopra.biblio.errors.BadRequestAlertException;
import formation_sopra.biblio.model.Editeur;

/**
 * REST controller for managing {@link com.formationsopra.biblio.domain.Editeur}.
 */
@RestController
@RequestMapping("/api/editeur")
@Transactional
public class EditeurResource {

    private static final Logger LOG = LoggerFactory.getLogger(EditeurResource.class);

    private static final String ENTITY_NAME = "editeur";

    private final EditeurRepository editeurRepository;

    public EditeurResource(EditeurRepository editeurRepository) {
        this.editeurRepository = editeurRepository;
    }

    /**
     * {@code POST  /editeur} : Create a new editeur.
     *
     * @param editeur the editeur to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new editeur, or with status {@code 400 (Bad Request)} if the editeur has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Editeur> createEditeur(@RequestBody Editeur editeur) throws URISyntaxException {
        LOG.debug("REST request to save Editeur : {}", editeur);
        if (editeur.getId() != null) {
            throw new BadRequestAlertException("A new editeur cannot already have an ID", ENTITY_NAME, "idexists");
        }
        editeur = editeurRepository.save(editeur);
        return ResponseEntity.created(new URI("/api/editeur/" + editeur.getId()))
            .body(editeur);
    }

    /**
     * {@code PUT  /editeur/:id} : Updates an existing editeur.
     *
     * @param id the id of the editeur to save.
     * @param editeur the editeur to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated editeur,
     * or with status {@code 400 (Bad Request)} if the editeur is not valid,
     * or with status {@code 500 (Internal Server Error)} if the editeur couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Editeur> updateEditeur(@PathVariable(value = "id", required = false) final Long id, @RequestBody Editeur editeur)
        throws URISyntaxException {
        LOG.debug("REST request to update Editeur : {}, {}", id, editeur);
        if (editeur.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, editeur.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!editeurRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        editeur = editeurRepository.save(editeur);
        return ResponseEntity.ok()
            .body(editeur);
    }

    /**
     * {@code PATCH  /editeur/:id} : Partial updates given fields of an existing editeur, field will ignore if it is null
     *
     * @param id the id of the editeur to save.
     * @param editeur the editeur to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated editeur,
     * or with status {@code 400 (Bad Request)} if the editeur is not valid,
     * or with status {@code 404 (Not Found)} if the editeur is not found,
     * or with status {@code 500 (Internal Server Error)} if the editeur couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Editeur> partialUpdateEditeur(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Editeur editeur
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Editeur partially : {}, {}", id, editeur);
        if (editeur.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, editeur.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!editeurRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Editeur> result = editeurRepository
            .findById(editeur.getId())
            .map(existingEditeur -> {
                updateIfPresent(existingEditeur::setNom, editeur.getNom());
                updateIfPresent(existingEditeur::setPays, editeur.getPays());

                return existingEditeur;
            })
            .map(editeurRepository::save);

        return result
            .map(updatedEditeur -> ResponseEntity.ok().body(updatedEditeur))
            .orElseGet(() -> ResponseEntity.notFound().build() 
        );
    }

    /**
     * {@code GET  /editeur} : get all the Editeurs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Editeurs in body.
     */
    @GetMapping("")
    public List<Editeur> getAllEditeurs() {
        LOG.debug("REST request to get all Editeurs");
        return editeurRepository.findAll();
    }

    /**
     * {@code GET  /editeur/:id} : get the "id" editeur.
     *
     * @param id the id of the editeur to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the editeur, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Editeur> getEditeur(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Editeur : {}", id);
        Optional<Editeur> editeur = editeurRepository.findById(id);
        return editeur.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * {@code DELETE  /editeur/:id} : delete the "id" editeur.
     *
     * @param id the id of the editeur to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEditeur(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Editeur : {}", id);
        editeurRepository.deleteById(id);
        return ResponseEntity.noContent()
            .build();
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
