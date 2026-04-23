package formation_sopra.biblio.model;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AuteurTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Auteur getAuteurSample1() {
        return new Auteur().id(1L).nom("nom1").prenom("prenom1").nationalite("nationalite1");
    }

    public static Auteur getAuteurSample2() {
        return new Auteur().id(2L).nom("nom2").prenom("prenom2").nationalite("nationalite2");
    }

    public static Auteur getAuteurRandomSampleGenerator() {
        return new Auteur()
            .id(longCount.incrementAndGet())
            .nom(UUID.randomUUID().toString())
            .prenom(UUID.randomUUID().toString())
            .nationalite(UUID.randomUUID().toString());
    }
}
