package formation_sopra.biblio.api;

import static formation_sopra.biblio.model.LivreAsserts.*;
import static formation_sopra.biblio.api.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import formation_sopra.biblio.model.Livre;
import formation_sopra.biblio.dao.LivreRepository;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link LivreResource} REST controller.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)

@WithMockUser
class LivreResourceIT {

    private static final String DEFAULT_TITRE = "AAAAAAAAAA";
    private static final String UPDATED_TITRE = "BBBBBBBBBB";

    private static final String DEFAULT_RESUME = "AAAAAAAAAA";
    private static final String UPDATED_RESUME = "BBBBBBBBBB";

    private static final String DEFAULT_ANNEE = "AAAAAAAAAA";
    private static final String UPDATED_ANNEE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/livre";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LivreRepository livreRepository;

    @Mock
    private LivreRepository livreRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLivreMockMvc;

    private Livre livre;

    private Livre insertedLivre;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Livre createEntity() {
        return new Livre().titre(DEFAULT_TITRE).resume(DEFAULT_RESUME).annee(DEFAULT_ANNEE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Livre createUpdatedEntity() {
        return new Livre().titre(UPDATED_TITRE).resume(UPDATED_RESUME).annee(UPDATED_ANNEE);
    }

    @BeforeEach
    void initTest() {
        livre = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedLivre != null) {
            livreRepository.delete(insertedLivre);
            insertedLivre = null;
        }
    }

    @Test
    @Transactional
    void createLivre() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Livre
        var returnedLivre = om.readValue(
            restLivreMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(livre)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Livre.class
        );

        // Validate the Livre in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertLivreUpdatableFieldsEquals(returnedLivre, getPersistedLivre(returnedLivre));

        insertedLivre = returnedLivre;
    }

    @Test
    @Transactional
    void createLivreWithExistingId() throws Exception {
        // Create the Livre with an existing ID
        livre.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLivreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(livre)))
            .andExpect(status().isBadRequest());

        // Validate the Livre in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllLivres() throws Exception {
        // Initialize the database
        insertedLivre = livreRepository.saveAndFlush(livre);

        // Get all the livreList
        restLivreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(livre.getId().intValue())))
            .andExpect(jsonPath("$.[*].titre").value(hasItem(DEFAULT_TITRE)))
            .andExpect(jsonPath("$.[*].resume").value(hasItem(DEFAULT_RESUME)))
            .andExpect(jsonPath("$.[*].annee").value(hasItem(DEFAULT_ANNEE)));
    }


    @Test
    @Transactional
    void getLivre() throws Exception {
        // Initialize the database
        insertedLivre = livreRepository.saveAndFlush(livre);

        // Get the livre
        restLivreMockMvc
            .perform(get(ENTITY_API_URL_ID, livre.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(livre.getId().intValue()))
            .andExpect(jsonPath("$.titre").value(DEFAULT_TITRE))
            .andExpect(jsonPath("$.resume").value(DEFAULT_RESUME))
            .andExpect(jsonPath("$.annee").value(DEFAULT_ANNEE));
    }

    @Test
    @Transactional
    void getNonExistingLivre() throws Exception {
        // Get the livre
        restLivreMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLivre() throws Exception {
        // Initialize the database
        insertedLivre = livreRepository.saveAndFlush(livre);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the livre
        Livre updatedLivre = livreRepository.findById(livre.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLivre are not directly saved in db
        em.detach(updatedLivre);
        updatedLivre.titre(UPDATED_TITRE).resume(UPDATED_RESUME).annee(UPDATED_ANNEE);

        restLivreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLivre.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedLivre))
            )
            .andExpect(status().isOk());

        // Validate the Livre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLivreToMatchAllProperties(updatedLivre);
    }

    @Test
    @Transactional
    void putNonExistingLivre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        livre.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLivreMockMvc
            .perform(put(ENTITY_API_URL_ID, livre.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(livre)))
            .andExpect(status().isBadRequest());

        // Validate the Livre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLivre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        livre.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLivreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(livre))
            )
            .andExpect(status().isBadRequest());

        // Validate the Livre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLivre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        livre.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLivreMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(livre)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Livre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLivreWithPatch() throws Exception {
        // Initialize the database
        insertedLivre = livreRepository.saveAndFlush(livre);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the livre using partial update
        Livre partialUpdatedLivre = new Livre();
        partialUpdatedLivre.setId(livre.getId());

        partialUpdatedLivre.resume(UPDATED_RESUME);

        restLivreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLivre.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLivre))
            )
            .andExpect(status().isOk());

        // Validate the Livre in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLivreUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedLivre, livre), getPersistedLivre(livre));
    }

    @Test
    @Transactional
    void fullUpdateLivreWithPatch() throws Exception {
        // Initialize the database
        insertedLivre = livreRepository.saveAndFlush(livre);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the livre using partial update
        Livre partialUpdatedLivre = new Livre();
        partialUpdatedLivre.setId(livre.getId());

        partialUpdatedLivre.titre(UPDATED_TITRE).resume(UPDATED_RESUME).annee(UPDATED_ANNEE);

        restLivreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLivre.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLivre))
            )
            .andExpect(status().isOk());

        // Validate the Livre in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLivreUpdatableFieldsEquals(partialUpdatedLivre, getPersistedLivre(partialUpdatedLivre));
    }

    @Test
    @Transactional
    void patchNonExistingLivre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        livre.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLivreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, livre.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(livre))
            )
            .andExpect(status().isBadRequest());

        // Validate the Livre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLivre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        livre.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLivreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(livre))
            )
            .andExpect(status().isBadRequest());

        // Validate the Livre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLivre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        livre.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLivreMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(livre)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Livre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLivre() throws Exception {
        // Initialize the database
        insertedLivre = livreRepository.saveAndFlush(livre);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the livre
        restLivreMockMvc
            .perform(delete(ENTITY_API_URL_ID, livre.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return livreRepository.count();
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

    protected Livre getPersistedLivre(Livre livre) {
        return livreRepository.findById(livre.getId()).orElseThrow();
    }

    protected void assertPersistedLivreToMatchAllProperties(Livre expectedLivre) {
        assertLivreAllPropertiesEquals(expectedLivre, getPersistedLivre(expectedLivre));
    }

    protected void assertPersistedLivreToMatchUpdatableProperties(Livre expectedLivre) {
        assertLivreAllUpdatablePropertiesEquals(expectedLivre, getPersistedLivre(expectedLivre));
    }
}
