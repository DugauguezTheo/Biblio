package formation_sopra.biblio.model;

import static formation_sopra.biblio.model.EditeurTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import formation_sopra.biblio.api.TestUtil;
import org.junit.jupiter.api.Test;

class EditeurTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Editeur.class);
        Editeur editeur1 = getEditeurSample1();
        Editeur editeur2 = new Editeur();
        assertThat(editeur1).isNotEqualTo(editeur2);

        editeur2.setId(editeur1.getId());
        assertThat(editeur1).isEqualTo(editeur2);

        editeur2 = getEditeurSample2();
        assertThat(editeur1).isNotEqualTo(editeur2);
    }
}
