package formation_sopra.biblio.model;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EditeurTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Editeur getEditeurSample1() {
        return new Editeur().id(1L).nom("nom1").pays("pays1");
    }

    public static Editeur getEditeurSample2() {
        return new Editeur().id(2L).nom("nom2").pays("pays2");
    }

    public static Editeur getEditeurRandomSampleGenerator() {
        return new Editeur().id(longCount.incrementAndGet()).nom(UUID.randomUUID().toString()).pays(UUID.randomUUID().toString());
    }
}
