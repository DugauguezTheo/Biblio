package formation_sopra.biblio.model;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CollectionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Collection getCollectionSample1() {
        return new Collection().id(1L).nom("nom1");
    }

    public static Collection getCollectionSample2() {
        return new Collection().id(2L).nom("nom2");
    }

    public static Collection getCollectionRandomSampleGenerator() {
        return new Collection().id(longCount.incrementAndGet()).nom(UUID.randomUUID().toString());
    }
}
