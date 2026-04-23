package formation_sopra.biblio.model;

import static formation_sopra.biblio.model.AuteurTestSamples.*;
import static formation_sopra.biblio.model.AvisTestSamples.*;
import static formation_sopra.biblio.model.CollectionTestSamples.*;
import static formation_sopra.biblio.model.EditeurTestSamples.*;
import static formation_sopra.biblio.model.GenreTestSamples.*;
import static formation_sopra.biblio.model.LivreTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import formation_sopra.biblio.api.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class LivreTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Livre.class);
        Livre livre1 = getLivreSample1();
        Livre livre2 = new Livre();
        assertThat(livre1).isNotEqualTo(livre2);

        livre2.setId(livre1.getId());
        assertThat(livre1).isEqualTo(livre2);

        livre2 = getLivreSample2();
        assertThat(livre1).isNotEqualTo(livre2);
    }

    @Test
    void auteurTest() {
        Livre livre = getLivreRandomSampleGenerator();
        Auteur auteurBack = getAuteurRandomSampleGenerator();

        livre.setAuteur(auteurBack);
        assertThat(livre.getAuteur()).isEqualTo(auteurBack);

        livre.auteur(null);
        assertThat(livre.getAuteur()).isNull();
    }

    @Test
    void editeurTest() {
        Livre livre = getLivreRandomSampleGenerator();
        Editeur editeurBack = getEditeurRandomSampleGenerator();

        livre.setEditeur(editeurBack);
        assertThat(livre.getEditeur()).isEqualTo(editeurBack);

        livre.editeur(null);
        assertThat(livre.getEditeur()).isNull();
    }

    @Test
    void collectionTest() {
        Livre livre = getLivreRandomSampleGenerator();
        Collection collectionBack = getCollectionRandomSampleGenerator();

        livre.setCollection(collectionBack);
        assertThat(livre.getCollection()).isEqualTo(collectionBack);

        livre.collection(null);
        assertThat(livre.getCollection()).isNull();
    }

    @Test
    void genresTest() {
        Livre livre = getLivreRandomSampleGenerator();
        Genre genreBack = getGenreRandomSampleGenerator();

        livre.addGenres(genreBack);
        assertThat(livre.getGenreses()).containsOnly(genreBack);

        livre.removeGenres(genreBack);
        assertThat(livre.getGenreses()).doesNotContain(genreBack);

        livre.genreses(new HashSet<>(Set.of(genreBack)));
        assertThat(livre.getGenreses()).containsOnly(genreBack);

        livre.setGenreses(new HashSet<>());
        assertThat(livre.getGenreses()).doesNotContain(genreBack);
    }

    @Test
    void avisTest() {
        Livre livre = getLivreRandomSampleGenerator();
        Avis avisBack = getAvisRandomSampleGenerator();

        livre.setAvis(avisBack);
        assertThat(livre.getAvis()).isEqualTo(avisBack);

        livre.avis(null);
        assertThat(livre.getAvis()).isNull();
    }
}
