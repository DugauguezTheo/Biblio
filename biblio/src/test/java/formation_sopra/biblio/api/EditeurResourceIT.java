package formation_sopra.biblio.api;

import static formation_sopra.biblio.model.EditeurAsserts.*;
import static formation_sopra.biblio.api.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import formation_sopra.biblio.model.Editeur;
import formation_sopra.biblio.dao.EditeurRepository;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EditeurResource} REST controller.
 */
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class EditeurResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_PAYS = "AAAAAAAAAA";
    private static final String UPDATED_PAYS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/editeur";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EditeurRepository editeurRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEditeurMockMvc;

    private Editeur editeur;

    private Editeur insertedEditeur;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Editeur createEntity() {
        return new Editeur().nom(DEFAULT_NOM).pays(DEFAULT_PAYS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Editeur createUpdatedEntity() {
        return new Editeur().nom(UPDATED_NOM).pays(UPDATED_PAYS);
    }

    @BeforeEach
    void initTest() {
        editeur = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedEditeur != null) {
            editeurRepository.delete(insertedEditeur);
            insertedEditeur = null;
        }
    }

    @Test
    @Transactional
    void createEditeur() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Editeur
        var returnedEditeur = om.readValue(
            restEditeurMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(editeur)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Editeur.class
        );

        // Validate the Editeur in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertEditeurUpdatableFieldsEquals(returnedEditeur, getPersistedEditeur(returnedEditeur));

        insertedEditeur = returnedEditeur;
    }

    @Test
    @Transactional
    void createEditeurWithExistingId() throws Exception {
        // Create the Editeur with an existing ID
        editeur.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEditeurMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(editeur)))
            .andExpect(status().isBadRequest());

        // Validate the Editeur in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllEditeurs() throws Exception {
        // Initialize the database
        insertedEditeur = editeurRepository.saveAndFlush(editeur);

        // Get all the editeurList
        restEditeurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(editeur.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].pays").value(hasItem(DEFAULT_PAYS)));
    }

    @Test
    @Transactional
    void getEditeur() throws Exception {
        // Initialize the database
        insertedEditeur = editeurRepository.saveAndFlush(editeur);

        // Get the editeur
        restEditeurMockMvc
            .perform(get(ENTITY_API_URL_ID, editeur.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(editeur.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.pays").value(DEFAULT_PAYS));
    }

    @Test
    @Transactional
    void getNonExistingEditeur() throws Exception {
        // Get the editeur
        restEditeurMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEditeur() throws Exception {
        // Initialize the database
        insertedEditeur = editeurRepository.saveAndFlush(editeur);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the editeur
        Editeur updatedEditeur = editeurRepository.findById(editeur.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEditeur are not directly saved in db
        em.detach(updatedEditeur);
        updatedEditeur.nom(UPDATED_NOM).pays(UPDATED_PAYS);

        restEditeurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEditeur.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedEditeur))
            )
            .andExpect(status().isOk());

        // Validate the Editeur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEditeurToMatchAllProperties(updatedEditeur);
    }

    @Test
    @Transactional
    void putNonExistingEditeur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        editeur.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEditeurMockMvc
            .perform(put(ENTITY_API_URL_ID, editeur.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(editeur)))
            .andExpect(status().isBadRequest());

        // Validate the Editeur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEditeur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        editeur.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEditeurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(editeur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Editeur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEditeur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        editeur.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEditeurMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(editeur)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Editeur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEditeurWithPatch() throws Exception {
        // Initialize the database
        insertedEditeur = editeurRepository.saveAndFlush(editeur);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the editeur using partial update
        Editeur partialUpdatedEditeur = new Editeur();
        partialUpdatedEditeur.setId(editeur.getId());

        partialUpdatedEditeur.pays(UPDATED_PAYS);

        restEditeurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEditeur.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEditeur))
            )
            .andExpect(status().isOk());

        // Validate the Editeur in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEditeurUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedEditeur, editeur), getPersistedEditeur(editeur));
    }

    @Test
    @Transactional
    void fullUpdateEditeurWithPatch() throws Exception {
        // Initialize the database
        insertedEditeur = editeurRepository.saveAndFlush(editeur);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the editeur using partial update
        Editeur partialUpdatedEditeur = new Editeur();
        partialUpdatedEditeur.setId(editeur.getId());

        partialUpdatedEditeur.nom(UPDATED_NOM).pays(UPDATED_PAYS);

        restEditeurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEditeur.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEditeur))
            )
            .andExpect(status().isOk());

        // Validate the Editeur in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEditeurUpdatableFieldsEquals(partialUpdatedEditeur, getPersistedEditeur(partialUpdatedEditeur));
    }

    @Test
    @Transactional
    void patchNonExistingEditeur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        editeur.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEditeurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, editeur.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(editeur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Editeur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEditeur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        editeur.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEditeurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(editeur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Editeur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEditeur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        editeur.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEditeurMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(editeur)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Editeur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEditeur() throws Exception {
        // Initialize the database
        insertedEditeur = editeurRepository.saveAndFlush(editeur);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the editeur
        restEditeurMockMvc
            .perform(delete(ENTITY_API_URL_ID, editeur.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return editeurRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Editeur getPersistedEditeur(Editeur editeur) {
        return editeurRepository.findById(editeur.getId()).orElseThrow();
    }

    protected void assertPersistedEditeurToMatchAllProperties(Editeur expectedEditeur) {
        assertEditeurAllPropertiesEquals(expectedEditeur, getPersistedEditeur(expectedEditeur));
    }

    protected void assertPersistedEditeurToMatchUpdatableProperties(Editeur expectedEditeur) {
        assertEditeurAllUpdatablePropertiesEquals(expectedEditeur, getPersistedEditeur(expectedEditeur));
    }
}
