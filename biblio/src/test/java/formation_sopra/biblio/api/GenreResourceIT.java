// package formation_sopra.biblio.api;

// import static formation_sopra.biblio.model.GenreAsserts.*;
// import static formation_sopra.biblio.api.TestUtil.createUpdateProxyForBean;
// import static org.assertj.core.api.Assertions.assertThat;
// import static org.hamcrest.Matchers.hasItem;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import formation_sopra.biblio.model.Genre;
// import formation_sopra.biblio.dao.GenreRepository;
// import jakarta.persistence.EntityManager;
// import java.util.Random;
// import java.util.concurrent.atomic.AtomicLong;
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.transaction.annotation.Transactional;

// /**
//  * Integration tests for the {@link GenreResource} REST controller.
//  */
// @SpringBootTest
// @AutoConfigureMockMvc
// @WithMockUser
// class GenreResourceIT {

//     private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
//     private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

//     private static final String ENTITY_API_URL = "/api/genre";
//     private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

//     private static Random random = new Random();
//     private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

//     @Autowired
//     private ObjectMapper om;

//     @Autowired
//     private GenreRepository genreRepository;

//     @Autowired
//     private EntityManager em;

//     @Autowired
//     private MockMvc restGenreMockMvc;

//     private Genre genre;

//     private Genre insertedGenre;

//     /**
//      * Create an entity for this test.
//      *
//      * This is a static method, as tests for other entities might also need it,
//      * if they test an entity which requires the current entity.
//      */
//     public static Genre createEntity() {
//         return new Genre().libelle(DEFAULT_LIBELLE);
//     }

//     /**
//      * Create an updated entity for this test.
//      *
//      * This is a static method, as tests for other entities might also need it,
//      * if they test an entity which requires the current entity.
//      */
//     public static Genre createUpdatedEntity() {
//         return new Genre().libelle(UPDATED_LIBELLE);
//     }

//     @BeforeEach
//     void initTest() {
//         genre = createEntity();
//     }

//     @AfterEach
//     void cleanup() {
//         if (insertedGenre != null) {
//             genreRepository.delete(insertedGenre);
//             insertedGenre = null;
//         }
//     }

//     @Test
//     @Transactional
//     void createGenre() throws Exception {
//         long databaseSizeBeforeCreate = getRepositoryCount();
//         // Create the Genre
//         var returnedGenre = om.readValue(
//             restGenreMockMvc
//                 .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(genre)))
//                 .andExpect(status().isCreated())
//                 .andReturn()
//                 .getResponse()
//                 .getContentAsString(),
//             Genre.class
//         );

//         // Validate the Genre in the database
//         assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
//         assertGenreUpdatableFieldsEquals(returnedGenre, getPersistedGenre(returnedGenre));

//         insertedGenre = returnedGenre;
//     }

//     @Test
//     @Transactional
//     void createGenreWithExistingId() throws Exception {
//         // Create the Genre with an existing ID
//         genre.setId(1L);

//         long databaseSizeBeforeCreate = getRepositoryCount();

//         // An entity with an existing ID cannot be created, so this API call must fail
//         restGenreMockMvc
//             .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(genre)))
//             .andExpect(status().isBadRequest());

//         // Validate the Genre in the database
//         assertSameRepositoryCount(databaseSizeBeforeCreate);
//     }

//     @Test
//     @Transactional
//     void getAllGenres() throws Exception {
//         // Initialize the database
//         insertedGenre = genreRepository.saveAndFlush(genre);

//         // Get all the genreList
//         restGenreMockMvc
//             .perform(get(ENTITY_API_URL + "?sort=id,desc"))
//             .andExpect(status().isOk())
//             .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//             .andExpect(jsonPath("$.[*].id").value(hasItem(genre.getId().intValue())))
//             .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));
//     }

//     @Test
//     @Transactional
//     void getGenre() throws Exception {
//         // Initialize the database
//         insertedGenre = genreRepository.saveAndFlush(genre);

//         // Get the genre
//         restGenreMockMvc
//             .perform(get(ENTITY_API_URL_ID, genre.getId()))
//             .andExpect(status().isOk())
//             .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//             .andExpect(jsonPath("$.id").value(genre.getId().intValue()))
//             .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE));
//     }

//     @Test
//     @Transactional
//     void getNonExistingGenre() throws Exception {
//         // Get the genre
//         restGenreMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
//     }

//     @Test
//     @Transactional
//     void putExistingGenre() throws Exception {
//         // Initialize the database
//         insertedGenre = genreRepository.saveAndFlush(genre);

//         long databaseSizeBeforeUpdate = getRepositoryCount();

//         // Update the genre
//         Genre updatedGenre = genreRepository.findById(genre.getId()).orElseThrow();
//         // Disconnect from session so that the updates on updatedGenre are not directly saved in db
//         em.detach(updatedGenre);
//         updatedGenre.libelle(UPDATED_LIBELLE);

//         restGenreMockMvc
//             .perform(
//                 put(ENTITY_API_URL_ID, updatedGenre.getId())
//                     .contentType(MediaType.APPLICATION_JSON)
//                     .content(om.writeValueAsBytes(updatedGenre))
//             )
//             .andExpect(status().isOk());

//         // Validate the Genre in the database
//         assertSameRepositoryCount(databaseSizeBeforeUpdate);
//         assertPersistedGenreToMatchAllProperties(updatedGenre);
//     }

//     @Test
//     @Transactional
//     void putNonExistingGenre() throws Exception {
//         long databaseSizeBeforeUpdate = getRepositoryCount();
//         genre.setId(longCount.incrementAndGet());

//         // If the entity doesn't have an ID, it will throw BadRequestAlertException
//         restGenreMockMvc
//             .perform(put(ENTITY_API_URL_ID, genre.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(genre)))
//             .andExpect(status().isBadRequest());

//         // Validate the Genre in the database
//         assertSameRepositoryCount(databaseSizeBeforeUpdate);
//     }

//     @Test
//     @Transactional
//     void putWithIdMismatchGenre() throws Exception {
//         long databaseSizeBeforeUpdate = getRepositoryCount();
//         genre.setId(longCount.incrementAndGet());

//         // If url ID doesn't match entity ID, it will throw BadRequestAlertException
//         restGenreMockMvc
//             .perform(
//                 put(ENTITY_API_URL_ID, longCount.incrementAndGet())
//                     .contentType(MediaType.APPLICATION_JSON)
//                     .content(om.writeValueAsBytes(genre))
//             )
//             .andExpect(status().isBadRequest());

//         // Validate the Genre in the database
//         assertSameRepositoryCount(databaseSizeBeforeUpdate);
//     }

//     @Test
//     @Transactional
//     void putWithMissingIdPathParamGenre() throws Exception {
//         long databaseSizeBeforeUpdate = getRepositoryCount();
//         genre.setId(longCount.incrementAndGet());

//         // If url ID doesn't match entity ID, it will throw BadRequestAlertException
//         restGenreMockMvc
//             .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(genre)))
//             .andExpect(status().isMethodNotAllowed());

//         // Validate the Genre in the database
//         assertSameRepositoryCount(databaseSizeBeforeUpdate);
//     }

//     @Test
//     @Transactional
//     void partialUpdateGenreWithPatch() throws Exception {
//         // Initialize the database
//         insertedGenre = genreRepository.saveAndFlush(genre);

//         long databaseSizeBeforeUpdate = getRepositoryCount();

//         // Update the genre using partial update
//         Genre partialUpdatedGenre = new Genre();
//         partialUpdatedGenre.setId(genre.getId());

//         restGenreMockMvc
//             .perform(
//                 patch(ENTITY_API_URL_ID, partialUpdatedGenre.getId())
//                     .contentType("application/merge-patch+json")
//                     .content(om.writeValueAsBytes(partialUpdatedGenre))
//             )
//             .andExpect(status().isOk());

//         // Validate the Genre in the database

//         assertSameRepositoryCount(databaseSizeBeforeUpdate);
//         assertGenreUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedGenre, genre), getPersistedGenre(genre));
//     }

//     @Test
//     @Transactional
//     void fullUpdateGenreWithPatch() throws Exception {
//         // Initialize the database
//         insertedGenre = genreRepository.saveAndFlush(genre);

//         long databaseSizeBeforeUpdate = getRepositoryCount();

//         // Update the genre using partial update
//         Genre partialUpdatedGenre = new Genre();
//         partialUpdatedGenre.setId(genre.getId());

//         partialUpdatedGenre.libelle(UPDATED_LIBELLE);

//         restGenreMockMvc
//             .perform(
//                 patch(ENTITY_API_URL_ID, partialUpdatedGenre.getId())
//                     .contentType("application/merge-patch+json")
//                     .content(om.writeValueAsBytes(partialUpdatedGenre))
//             )
//             .andExpect(status().isOk());

//         // Validate the Genre in the database

//         assertSameRepositoryCount(databaseSizeBeforeUpdate);
//         assertGenreUpdatableFieldsEquals(partialUpdatedGenre, getPersistedGenre(partialUpdatedGenre));
//     }

//     @Test
//     @Transactional
//     void patchNonExistingGenre() throws Exception {
//         long databaseSizeBeforeUpdate = getRepositoryCount();
//         genre.setId(longCount.incrementAndGet());

//         // If the entity doesn't have an ID, it will throw BadRequestAlertException
//         restGenreMockMvc
//             .perform(
//                 patch(ENTITY_API_URL_ID, genre.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(genre))
//             )
//             .andExpect(status().isBadRequest());

//         // Validate the Genre in the database
//         assertSameRepositoryCount(databaseSizeBeforeUpdate);
//     }

//     @Test
//     @Transactional
//     void patchWithIdMismatchGenre() throws Exception {
//         long databaseSizeBeforeUpdate = getRepositoryCount();
//         genre.setId(longCount.incrementAndGet());

//         // If url ID doesn't match entity ID, it will throw BadRequestAlertException
//         restGenreMockMvc
//             .perform(
//                 patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
//                     .contentType("application/merge-patch+json")
//                     .content(om.writeValueAsBytes(genre))
//             )
//             .andExpect(status().isBadRequest());

//         // Validate the Genre in the database
//         assertSameRepositoryCount(databaseSizeBeforeUpdate);
//     }

//     @Test
//     @Transactional
//     void patchWithMissingIdPathParamGenre() throws Exception {
//         long databaseSizeBeforeUpdate = getRepositoryCount();
//         genre.setId(longCount.incrementAndGet());

//         // If url ID doesn't match entity ID, it will throw BadRequestAlertException
//         restGenreMockMvc
//             .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(genre)))
//             .andExpect(status().isMethodNotAllowed());

//         // Validate the Genre in the database
//         assertSameRepositoryCount(databaseSizeBeforeUpdate);
//     }

//     @Test
//     @Transactional
//     void deleteGenre() throws Exception {
//         // Initialize the database
//         insertedGenre = genreRepository.saveAndFlush(genre);

//         long databaseSizeBeforeDelete = getRepositoryCount();

//         // Delete the genre
//         restGenreMockMvc
//             .perform(delete(ENTITY_API_URL_ID, genre.getId()).accept(MediaType.APPLICATION_JSON))
//             .andExpect(status().isNoContent());

//         // Validate the database contains one less item
//         assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
//     }

//     protected long getRepositoryCount() {
//         return genreRepository.count();
//     }

//     protected void assertIncrementedRepositoryCount(long countBefore) {
//         assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
//     }

//     protected void assertDecrementedRepositoryCount(long countBefore) {
//         assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
//     }

//     protected void assertSameRepositoryCount(long countBefore) {
//         assertThat(countBefore).isEqualTo(getRepositoryCount());
//     }

//     protected Genre getPersistedGenre(Genre genre) {
//         return genreRepository.findById(genre.getId()).orElseThrow();
//     }

//     protected void assertPersistedGenreToMatchAllProperties(Genre expectedGenre) {
//         assertGenreAllPropertiesEquals(expectedGenre, getPersistedGenre(expectedGenre));
//     }

//     protected void assertPersistedGenreToMatchUpdatableProperties(Genre expectedGenre) {
//         assertGenreAllUpdatablePropertiesEquals(expectedGenre, getPersistedGenre(expectedGenre));
//     }
// }
