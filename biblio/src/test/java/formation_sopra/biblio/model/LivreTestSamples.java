package formation_sopra.biblio.model;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LivreTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Livre getLivreSample1() {
        return new Livre().id(1L).titre("titre1").resume("resume1").annee("annee1");
    }

    public static Livre getLivreSample2() {
        return new Livre().id(2L).titre("titre2").resume("resume2").annee("annee2");
    }

    public static Livre getLivreRandomSampleGenerator() {
        return new Livre()
            .id(longCount.incrementAndGet())
            .titre(UUID.randomUUID().toString())
            .resume(UUID.randomUUID().toString())
            .annee(UUID.randomUUID().toString());
    }
}
