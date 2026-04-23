// package formation_sopra.biblio.model;

// import static formation_sopra.biblio.model.GenreTestSamples.*;
// import static formation_sopra.biblio.model.LivreTestSamples.*;
// import static org.assertj.core.api.Assertions.assertThat;

// import formation_sopra.biblio.api.TestUtil;
// import java.util.HashSet;
// import java.util.Set;
// import org.junit.jupiter.api.Test;

// class GenreTest {

//     @Test
//     void equalsVerifier() throws Exception {
//         TestUtil.equalsVerifier(Genre.class);
//         Genre genre1 = getGenreSample1();
//         Genre genre2 = new Genre();
//         assertThat(genre1).isNotEqualTo(genre2);

//         genre2.setId(genre1.getId());
//         assertThat(genre1).isEqualTo(genre2);

//         genre2 = getGenreSample2();
//         assertThat(genre1).isNotEqualTo(genre2);
//     }

//     @Test
//     void livreTest() {
//         Genre genre = getGenreRandomSampleGenerator();
//         Livre livreBack = getLivreRandomSampleGenerator();

//         genre.addLivre(livreBack);
//         assertThat(genre.getLivres()).containsOnly(livreBack);
//         assertThat(livreBack.getGenreses()).containsOnly(genre);

//         genre.removeLivre(livreBack);
//         assertThat(genre.getLivres()).doesNotContain(livreBack);
//         assertThat(livreBack.getGenreses()).doesNotContain(genre);

//         genre.livres(new HashSet<>(Set.of(livreBack)));
//         assertThat(genre.getLivres()).containsOnly(livreBack);
//         assertThat(livreBack.getGenreses()).containsOnly(genre);

//         genre.setLivres(new HashSet<>());
//         assertThat(genre.getLivres()).doesNotContain(livreBack);
//         assertThat(livreBack.getGenreses()).doesNotContain(genre);
//     }
// }
