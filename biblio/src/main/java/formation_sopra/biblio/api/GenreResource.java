// package formation_sopra.biblio.api;

// import java.net.URI;
// import java.net.URISyntaxException;
// import java.util.List;
// import java.util.Objects;
// import java.util.Optional;
// import java.util.function.Consumer;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.http.ResponseEntity;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PatchMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import formation_sopra.biblio.dao.GenreRepository;
// import formation_sopra.biblio.errors.BadRequestAlertException;
// import formation_sopra.biblio.model.Genre;


// /**
//  * REST controller for managing {@link com.formationsopra.biblio.domain.Genre}.
//  */
// @RestController
// @RequestMapping("/api/genre")
// @Transactional
// public class GenreResource {

//     private static final Logger LOG = LoggerFactory.getLogger(GenreResource.class);

//     private static final String ENTITY_NAME = "genre";


//     private final GenreRepository genreRepository;

//     public GenreResource(GenreRepository genreRepository) {
//         this.genreRepository = genreRepository;
//     }

//     /**
//      * {@code POST  /genre} : Create a new genre.
//      *
//      * @param genre the genre to create.
//      * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new genre, or with status {@code 400 (Bad Request)} if the genre has already an ID.
//      * @throws URISyntaxException if the Location URI syntax is incorrect.
//      */
//     @PostMapping("")
//     public ResponseEntity<Genre> createGenre(@RequestBody Genre genre) throws URISyntaxException {
//         LOG.debug("REST request to save Genre : {}", genre);
//         if (genre.getId() != null) {
//             throw new BadRequestAlertException("A new genre cannot already have an ID", ENTITY_NAME, "idexists");
//         }
//         genre = genreRepository.save(genre);
//         return ResponseEntity.created(new URI("/api/genre/" + genre.getId()))
//             .body(genre);
//     }

//     /**
//      * {@code PUT  /genre/:id} : Updates an existing genre.
//      *
//      * @param id the id of the genre to save.
//      * @param genre the genre to update.
//      * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated genre,
//      * or with status {@code 400 (Bad Request)} if the genre is not valid,
//      * or with status {@code 500 (Internal Server Error)} if the genre couldn't be updated.
//      * @throws URISyntaxException if the Location URI syntax is incorrect.
//      */
//     @PutMapping("/{id}")
//     public ResponseEntity<Genre> updateGenre(@PathVariable(value = "id", required = false) final Long id, @RequestBody Genre genre)
//         throws URISyntaxException {
//         LOG.debug("REST request to update Genre : {}, {}", id, genre);
//         if (genre.getId() == null) {
//             throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//         }
//         if (!Objects.equals(id, genre.getId())) {
//             throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//         }

//         if (!genreRepository.existsById(id)) {
//             throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//         }

//         genre = genreRepository.save(genre);
//         return ResponseEntity.ok()
//             .body(genre);
//     }

//     /**
//      * {@code PATCH  /genre/:id} : Partial updates given fields of an existing genre, field will ignore if it is null
//      *
//      * @param id the id of the genre to save.
//      * @param genre the genre to update.
//      * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated genre,
//      * or with status {@code 400 (Bad Request)} if the genre is not valid,
//      * or with status {@code 404 (Not Found)} if the genre is not found,
//      * or with status {@code 500 (Internal Server Error)} if the genre couldn't be updated.
//      * @throws URISyntaxException if the Location URI syntax is incorrect.
//      */
//     @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
//     public ResponseEntity<Genre> partialUpdateGenre(@PathVariable(value = "id", required = false) final Long id, @RequestBody Genre genre)
//         throws URISyntaxException {
//         LOG.debug("REST request to partial update Genre partially : {}, {}", id, genre);
//         if (genre.getId() == null) {
//             throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//         }
//         if (!Objects.equals(id, genre.getId())) {
//             throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//         }

//         if (!genreRepository.existsById(id)) {
//             throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//         }

//         Optional<Genre> result = genreRepository
//             .findById(genre.getId())
//             .map(existingGenre -> {
//                 updateIfPresent(existingGenre::setLibelle, genre.getLibelle());

//                 return existingGenre;
//             })
//             .map(genreRepository::save);

//         return result
//             .map(updatedGenre -> ResponseEntity.ok().body(updatedGenre))
//             .orElseGet(() -> ResponseEntity.notFound().build());
//     }

//     /**
//      * {@code GET  /genre} : get all the Genres.
//      *
//      * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Genres in body.
//      */
//     @GetMapping("")
//     public List<Genre> getAllGenres() {
//         LOG.debug("REST request to get all Genres");
//         return genreRepository.findAll();
//     }

//     /**
//      * {@code GET  /genre/:id} : get the "id" genre.
//      *
//      * @param id the id of the genre to retrieve.
//      * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the genre, or with status {@code 404 (Not Found)}.
//      */
//     @GetMapping("/{id}")
//     public ResponseEntity<Genre> getGenre(@PathVariable("id") Long id) {
//         LOG.debug("REST request to get Genre : {}", id);
//         Optional<Genre> genre = genreRepository.findById(id);
//         return genre.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//     }

//     /**
//      * {@code DELETE  /genre/:id} : delete the "id" genre.
//      *
//      * @param id the id of the genre to delete.
//      * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//      */
//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> deleteGenre(@PathVariable("id") Long id) {
//         LOG.debug("REST request to delete Genre : {}", id);
//         genreRepository.deleteById(id);
//         return ResponseEntity.noContent()
//             .build();
//     }

//     private <T> void updateIfPresent(Consumer<T> setter, T value) {
//         if (value != null) {
//             setter.accept(value);
//         }
//     }
// }
