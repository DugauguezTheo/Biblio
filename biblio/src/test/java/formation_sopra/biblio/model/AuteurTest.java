package formation_sopra.biblio.model;

import static formation_sopra.biblio.model.AuteurTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import formation_sopra.biblio.api.TestUtil;

class AuteurTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Auteur.class);
        Auteur auteur1 = getAuteurSample1();
        Auteur auteur2 = new Auteur();
        assertThat(auteur1).isNotEqualTo(auteur2);

        auteur2.setId(auteur1.getId());
        assertThat(auteur1).isEqualTo(auteur2);

        auteur2 = getAuteurSample2();
        assertThat(auteur1).isNotEqualTo(auteur2);
    }
}
