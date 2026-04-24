package formation_sopra.biblio.api;

import static formation_sopra.biblio.model.AvisAsserts.*;
import static formation_sopra.biblio.api.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import formation_sopra.biblio.model.Avis;
import formation_sopra.biblio.dao.AvisRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

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
 * Integration tests for the {@link AvisResource} REST controller.
 */
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class AvisResourceIT {

    private static final Double DEFAULT_NOTE = 1D;
    private static final Double UPDATED_NOTE = 2D;

    private static final String DEFAULT_COMMENTAIRES = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTAIRES = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE = Instant.ofEpochMilli(0L)
            .atZone(ZoneId.systemDefault())
            .toLocalDate();

    private static final LocalDate UPDATED_DATE = Instant.now()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();

    private static final String ENTITY_API_URL = "/api/avis";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AvisRepository avisRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAvisMockMvc;

    private Avis avis;

    private Avis insertedAvis;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Avis createEntity() {
        return new Avis().note(DEFAULT_NOTE).commentaire(DEFAULT_COMMENTAIRES).date(DEFAULT_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Avis createUpdatedEntity() {
        return new Avis().note(UPDATED_NOTE).commentaire(UPDATED_COMMENTAIRES).date(UPDATED_DATE);
    }

    @BeforeEach
    void initTest() {
        avis = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAvis != null) {
            avisRepository.delete(insertedAvis);
            insertedAvis = null;
        }
    }

    @Test
    @Transactional
    void createAvis() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Avis
        var returnedAvis = om.readValue(
            restAvisMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(avis)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Avis.class
        );

        // Validate the Avis in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAvisUpdatableFieldsEquals(returnedAvis, getPersistedAvis(returnedAvis));

        insertedAvis = returnedAvis;
    }

    @Test
    @Transactional
    void createAvisWithExistingId() throws Exception {
        // Create the Avis with an existing ID
        avis.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAvisMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(avis)))
            .andExpect(status().isBadRequest());

        // Validate the Avis in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAvises() throws Exception {
        // Initialize the database
        insertedAvis = avisRepository.saveAndFlush(avis);

        // Get all the avisList
        restAvisMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(avis.getId().intValue())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].commentaire").value(hasItem(DEFAULT_COMMENTAIRES)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    @Transactional
    void getAvis() throws Exception {
        // Initialize the database
        insertedAvis = avisRepository.saveAndFlush(avis);

        // Get the avis
        restAvisMockMvc
            .perform(get(ENTITY_API_URL_ID, avis.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(avis.getId().intValue()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE))
            .andExpect(jsonPath("$.commentaire").value(DEFAULT_COMMENTAIRES))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingAvis() throws Exception {
        // Get the avis
        restAvisMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAvis() throws Exception {
        // Initialize the database
        insertedAvis = avisRepository.saveAndFlush(avis);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the avis
        Avis updatedAvis = avisRepository.findById(avis.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAvis are not directly saved in db
        em.detach(updatedAvis);
        updatedAvis.note(UPDATED_NOTE).commentaire(UPDATED_COMMENTAIRES).date(UPDATED_DATE);

        restAvisMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAvis.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedAvis))
            )
            .andExpect(status().isOk());

        // Validate the Avis in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAvisToMatchAllProperties(updatedAvis);
    }

    @Test
    @Transactional
    void putNonExistingAvis() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        avis.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAvisMockMvc
            .perform(put(ENTITY_API_URL_ID, avis.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(avis)))
            .andExpect(status().isBadRequest());

        // Validate the Avis in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAvis() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        avis.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAvisMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(avis))
            )
            .andExpect(status().isBadRequest());

        // Validate the Avis in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAvis() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        avis.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAvisMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(avis)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Avis in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAvisWithPatch() throws Exception {
        // Initialize the database
        insertedAvis = avisRepository.saveAndFlush(avis);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the avis using partial update
        Avis partialUpdatedAvis = new Avis();
        partialUpdatedAvis.setId(avis.getId());

        restAvisMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAvis.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAvis))
            )
            .andExpect(status().isOk());

        // Validate the Avis in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAvisUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAvis, avis), getPersistedAvis(avis));
    }

    @Test
    @Transactional
    void fullUpdateAvisWithPatch() throws Exception {
        // Initialize the database
        insertedAvis = avisRepository.saveAndFlush(avis);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the avis using partial update
        Avis partialUpdatedAvis = new Avis();
        partialUpdatedAvis.setId(avis.getId());

        partialUpdatedAvis.note(UPDATED_NOTE).commentaire(UPDATED_COMMENTAIRES).date(UPDATED_DATE);

        restAvisMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAvis.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAvis))
            )
            .andExpect(status().isOk());

        // Validate the Avis in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAvisUpdatableFieldsEquals(partialUpdatedAvis, getPersistedAvis(partialUpdatedAvis));
    }

    @Test
    @Transactional
    void patchNonExistingAvis() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        avis.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAvisMockMvc
            .perform(patch(ENTITY_API_URL_ID, avis.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(avis)))
            .andExpect(status().isBadRequest());

        // Validate the Avis in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAvis() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        avis.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAvisMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(avis))
            )
            .andExpect(status().isBadRequest());

        // Validate the Avis in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAvis() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        avis.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAvisMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(avis)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Avis in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAvis() throws Exception {
        // Initialize the database
        insertedAvis = avisRepository.saveAndFlush(avis);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the avis
        restAvisMockMvc
            .perform(delete(ENTITY_API_URL_ID, avis.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return avisRepository.count();
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

    protected Avis getPersistedAvis(Avis avis) {
        return avisRepository.findById(avis.getId()).orElseThrow();
    }

    protected void assertPersistedAvisToMatchAllProperties(Avis expectedAvis) {
        assertAvisAllPropertiesEquals(expectedAvis, getPersistedAvis(expectedAvis));
    }

    protected void assertPersistedAvisToMatchUpdatableProperties(Avis expectedAvis) {
        assertAvisAllUpdatablePropertiesEquals(expectedAvis, getPersistedAvis(expectedAvis));
    }
}
