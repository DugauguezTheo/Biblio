package formation_sopra.biblio.model;

import static formation_sopra.biblio.model.CollectionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import formation_sopra.biblio.api.TestUtil;
import org.junit.jupiter.api.Test;

class CollectionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Collection.class);
        Collection collection1 = getCollectionSample1();
        Collection collection2 = new Collection();
        assertThat(collection1).isNotEqualTo(collection2);

        collection2.setId(collection1.getId());
        assertThat(collection1).isEqualTo(collection2);

        collection2 = getCollectionSample2();
        assertThat(collection1).isNotEqualTo(collection2);
    }
}
