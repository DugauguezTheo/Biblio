package formation_sopra.biblio.model;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AvisTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Avis getAvisSample1() {
        return new Avis().id(1L).commentaire("commentaires1");
    }

    public static Avis getAvisSample2() {
        return new Avis().id(2L).commentaire("commentaires2");
    }

    public static Avis getAvisRandomSampleGenerator() {
        return new Avis().id(longCount.incrementAndGet()).commentaire(UUID.randomUUID().toString());
    }
}
