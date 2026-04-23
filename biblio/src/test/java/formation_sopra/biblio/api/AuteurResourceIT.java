package formation_sopra.biblio.api;

import static formation_sopra.biblio.model.AuteurAsserts.*;
import static formation_sopra.biblio.api.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import formation_sopra.biblio.model.Auteur;
import formation_sopra.biblio.dao.AuteurRepository;
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
 * Integration tests for the {@link AuteurResource} REST controller.
 */
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class AuteurResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM = "BBBBBBBBBB";

    private static final String DEFAULT_NATIONALITE = "AAAAAAAAAA";
    private static final String UPDATED_NATIONALITE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/auteur";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AuteurRepository auteurRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAuteurMockMvc;

    private Auteur auteur;

    private Auteur insertedAuteur;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Auteur createEntity() {
        return new Auteur().nom(DEFAULT_NOM).prenom(DEFAULT_PRENOM).nationalite(DEFAULT_NATIONALITE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Auteur createUpdatedEntity() {
        return new Auteur().nom(UPDATED_NOM).prenom(UPDATED_PRENOM).nationalite(UPDATED_NATIONALITE);
    }

    @BeforeEach
    void initTest() {
        auteur = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAuteur != null) {
            auteurRepository.delete(insertedAuteur);
            insertedAuteur = null;
        }
    }

    @Test
    @Transactional
    void createAuteur() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Auteur
        var returnedAuteur = om.readValue(
            restAuteurMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auteur)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Auteur.class
        );

        // Validate the Auteur in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAuteurUpdatableFieldsEquals(returnedAuteur, getPersistedAuteur(returnedAuteur));

        insertedAuteur = returnedAuteur;
    }

    @Test
    @Transactional
    void createAuteurWithExistingId() throws Exception {
        // Create the Auteur with an existing ID
        auteur.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAuteurMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auteur)))
            .andExpect(status().isBadRequest());

        // Validate the Auteur in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAuteurs() throws Exception {
        // Initialize the database
        insertedAuteur = auteurRepository.saveAndFlush(auteur);

        // Get all the auteurList
        restAuteurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(auteur.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].nationalite").value(hasItem(DEFAULT_NATIONALITE)));
    }

    @Test
    @Transactional
    void getAuteur() throws Exception {
        // Initialize the database
        insertedAuteur = auteurRepository.saveAndFlush(auteur);

        // Get the auteur
        restAuteurMockMvc
            .perform(get(ENTITY_API_URL_ID, auteur.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(auteur.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.prenom").value(DEFAULT_PRENOM))
            .andExpect(jsonPath("$.nationalite").value(DEFAULT_NATIONALITE));
    }

    @Test
    @Transactional
    void getNonExistingAuteur() throws Exception {
        // Get the auteur
        restAuteurMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAuteur() throws Exception {
        // Initialize the database
        insertedAuteur = auteurRepository.saveAndFlush(auteur);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auteur
        Auteur updatedAuteur = auteurRepository.findById(auteur.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAuteur are not directly saved in db
        em.detach(updatedAuteur);
        updatedAuteur.nom(UPDATED_NOM).prenom(UPDATED_PRENOM).nationalite(UPDATED_NATIONALITE);

        restAuteurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAuteur.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedAuteur))
            )
            .andExpect(status().isOk());

        // Validate the Auteur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAuteurToMatchAllProperties(updatedAuteur);
    }

    @Test
    @Transactional
    void putNonExistingAuteur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auteur.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuteurMockMvc
            .perform(put(ENTITY_API_URL_ID, auteur.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auteur)))
            .andExpect(status().isBadRequest());

        // Validate the Auteur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAuteur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auteur.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuteurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(auteur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Auteur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAuteur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auteur.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuteurMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(auteur)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Auteur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAuteurWithPatch() throws Exception {
        // Initialize the database
        insertedAuteur = auteurRepository.saveAndFlush(auteur);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auteur using partial update
        Auteur partialUpdatedAuteur = new Auteur();
        partialUpdatedAuteur.setId(auteur.getId());

        partialUpdatedAuteur.nationalite(UPDATED_NATIONALITE);

        restAuteurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuteur.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAuteur))
            )
            .andExpect(status().isOk());

        // Validate the Auteur in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuteurUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAuteur, auteur), getPersistedAuteur(auteur));
    }

    @Test
    @Transactional
    void fullUpdateAuteurWithPatch() throws Exception {
        // Initialize the database
        insertedAuteur = auteurRepository.saveAndFlush(auteur);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the auteur using partial update
        Auteur partialUpdatedAuteur = new Auteur();
        partialUpdatedAuteur.setId(auteur.getId());

        partialUpdatedAuteur.nom(UPDATED_NOM).prenom(UPDATED_PRENOM).nationalite(UPDATED_NATIONALITE);

        restAuteurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuteur.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAuteur))
            )
            .andExpect(status().isOk());

        // Validate the Auteur in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuteurUpdatableFieldsEquals(partialUpdatedAuteur, getPersistedAuteur(partialUpdatedAuteur));
    }

    @Test
    @Transactional
    void patchNonExistingAuteur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auteur.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuteurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, auteur.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(auteur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Auteur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAuteur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auteur.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuteurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(auteur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Auteur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAuteur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        auteur.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuteurMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(auteur)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Auteur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAuteur() throws Exception {
        // Initialize the database
        insertedAuteur = auteurRepository.saveAndFlush(auteur);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the auteur
        restAuteurMockMvc
            .perform(delete(ENTITY_API_URL_ID, auteur.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return auteurRepository.count();
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

    protected Auteur getPersistedAuteur(Auteur auteur) {
        return auteurRepository.findById(auteur.getId()).orElseThrow();
    }

    protected void assertPersistedAuteurToMatchAllProperties(Auteur expectedAuteur) {
        assertAuteurAllPropertiesEquals(expectedAuteur, getPersistedAuteur(expectedAuteur));
    }

    protected void assertPersistedAuteurToMatchUpdatableProperties(Auteur expectedAuteur) {
        assertAuteurAllUpdatablePropertiesEquals(expectedAuteur, getPersistedAuteur(expectedAuteur));
    }
}
