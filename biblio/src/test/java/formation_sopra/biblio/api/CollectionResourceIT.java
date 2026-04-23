package formation_sopra.biblio.api;

import static formation_sopra.biblio.model.CollectionAsserts.*;
import static formation_sopra.biblio.api.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import formation_sopra.biblio.model.Collection;
import formation_sopra.biblio.dao.CollectionRepository;
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
 * Integration tests for the {@link CollectionResource} REST controller.
 */
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class CollectionResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/collection";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCollectionMockMvc;

    private Collection collection;

    private Collection insertedCollection;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Collection createEntity() {
        return new Collection().nom(DEFAULT_NOM);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Collection createUpdatedEntity() {
        return new Collection().nom(UPDATED_NOM);
    }

    @BeforeEach
    void initTest() {
        collection = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCollection != null) {
            collectionRepository.delete(insertedCollection);
            insertedCollection = null;
        }
    }

    @Test
    @Transactional
    void createCollection() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Collection
        var returnedCollection = om.readValue(
            restCollectionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(collection)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Collection.class
        );

        // Validate the Collection in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCollectionUpdatableFieldsEquals(returnedCollection, getPersistedCollection(returnedCollection));

        insertedCollection = returnedCollection;
    }

    @Test
    @Transactional
    void createCollectionWithExistingId() throws Exception {
        // Create the Collection with an existing ID
        collection.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(collection)))
            .andExpect(status().isBadRequest());

        // Validate the Collection in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCollections() throws Exception {
        // Initialize the database
        insertedCollection = collectionRepository.saveAndFlush(collection);

        // Get all the collectionList
        restCollectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(collection.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)));
    }

    @Test
    @Transactional
    void getCollection() throws Exception {
        // Initialize the database
        insertedCollection = collectionRepository.saveAndFlush(collection);

        // Get the collection
        restCollectionMockMvc
            .perform(get(ENTITY_API_URL_ID, collection.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(collection.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM));
    }

    @Test
    @Transactional
    void getNonExistingCollection() throws Exception {
        // Get the collection
        restCollectionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCollection() throws Exception {
        // Initialize the database
        insertedCollection = collectionRepository.saveAndFlush(collection);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the collection
        Collection updatedCollection = collectionRepository.findById(collection.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCollection are not directly saved in db
        em.detach(updatedCollection);
        updatedCollection.nom(UPDATED_NOM);

        restCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCollection.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedCollection))
            )
            .andExpect(status().isOk());

        // Validate the Collection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCollectionToMatchAllProperties(updatedCollection);
    }

    @Test
    @Transactional
    void putNonExistingCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        collection.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, collection.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(collection))
            )
            .andExpect(status().isBadRequest());

        // Validate the Collection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        collection.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(collection))
            )
            .andExpect(status().isBadRequest());

        // Validate the Collection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        collection.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(collection)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Collection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCollectionWithPatch() throws Exception {
        // Initialize the database
        insertedCollection = collectionRepository.saveAndFlush(collection);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the collection using partial update
        Collection partialUpdatedCollection = new Collection();
        partialUpdatedCollection.setId(collection.getId());

        restCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCollection.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCollection))
            )
            .andExpect(status().isOk());

        // Validate the Collection in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCollectionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCollection, collection),
            getPersistedCollection(collection)
        );
    }

    @Test
    @Transactional
    void fullUpdateCollectionWithPatch() throws Exception {
        // Initialize the database
        insertedCollection = collectionRepository.saveAndFlush(collection);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the collection using partial update
        Collection partialUpdatedCollection = new Collection();
        partialUpdatedCollection.setId(collection.getId());

        partialUpdatedCollection.nom(UPDATED_NOM);

        restCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCollection.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCollection))
            )
            .andExpect(status().isOk());

        // Validate the Collection in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCollectionUpdatableFieldsEquals(partialUpdatedCollection, getPersistedCollection(partialUpdatedCollection));
    }

    @Test
    @Transactional
    void patchNonExistingCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        collection.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, collection.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(collection))
            )
            .andExpect(status().isBadRequest());

        // Validate the Collection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        collection.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(collection))
            )
            .andExpect(status().isBadRequest());

        // Validate the Collection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        collection.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(collection)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Collection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCollection() throws Exception {
        // Initialize the database
        insertedCollection = collectionRepository.saveAndFlush(collection);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the collection
        restCollectionMockMvc
            .perform(delete(ENTITY_API_URL_ID, collection.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return collectionRepository.count();
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

    protected Collection getPersistedCollection(Collection collection) {
        return collectionRepository.findById(collection.getId()).orElseThrow();
    }

    protected void assertPersistedCollectionToMatchAllProperties(Collection expectedCollection) {
        assertCollectionAllPropertiesEquals(expectedCollection, getPersistedCollection(expectedCollection));
    }

    protected void assertPersistedCollectionToMatchUpdatableProperties(Collection expectedCollection) {
        assertCollectionAllUpdatablePropertiesEquals(expectedCollection, getPersistedCollection(expectedCollection));
    }
}
