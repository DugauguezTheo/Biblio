package formation_sopra.biblio.model;

import static formation_sopra.biblio.model.AvisTestSamples.*;
import static formation_sopra.biblio.model.LivreTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import formation_sopra.biblio.api.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AvisTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Avis.class);
        Avis avis1 = getAvisSample1();
        Avis avis2 = new Avis();
        assertThat(avis1).isNotEqualTo(avis2);

        avis2.setId(avis1.getId());
        assertThat(avis1).isEqualTo(avis2);

        avis2 = getAvisSample2();
        assertThat(avis1).isNotEqualTo(avis2);
    }

    @Test
    void livreTest() {
        Avis avis = getAvisRandomSampleGenerator();
        Livre livreBack = getLivreRandomSampleGenerator();

        avis.addLivre(livreBack);
        assertThat(avis.getLivres()).containsOnly(livreBack);
        assertThat(livreBack.getAvis()).isEqualTo(avis);

        avis.removeLivre(livreBack);
        assertThat(avis.getLivres()).doesNotContain(livreBack);
        assertThat(livreBack.getAvis()).isNull();

        avis.livres(new HashSet<>(Set.of(livreBack)));
        assertThat(avis.getLivres()).containsOnly(livreBack);
        assertThat(livreBack.getAvis()).isEqualTo(avis);

        avis.setLivres(new HashSet<>());
        assertThat(avis.getLivres()).doesNotContain(livreBack);
        assertThat(livreBack.getAvis()).isNull();
    }
}
