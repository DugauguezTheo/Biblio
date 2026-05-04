package formation_sopra.commande_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "details_commandes")
public class CommandeDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDetails;

    @ManyToOne
    @JoinColumn(name = "id_commande", nullable = false)
    private Commande commande;

    @Column(name = "produit", nullable = false, length = 50)
    private String libelleProduit;

    @Column(name = "prix_unitaire", nullable = false)
    private double prixUnitaireProduit;

}
