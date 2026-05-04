package formation_sopra.commande_service.api.dto.response;

import formation_sopra.commande_service.model.CommandeDetails;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommandeDetailsResponse {

    private Integer id;
    private Integer idCommande;
    private int qte;
    private double prixUnitaire;
    private String libelle;

    public static CommandeDetailsResponse convert(CommandeDetails commandeDetails) {
        CommandeDetailsResponse resp = new CommandeDetailsResponse();

        resp.setId(commandeDetails.getIdDetails());
        resp.setIdCommande(commandeDetails.getCommande().getId());
        resp.setQte(commandeDetails.getQuantite());
        resp.setPrixUnitaire(commandeDetails.getPrixUnitaireProduit());
        resp.setLibelle(commandeDetails.getLibelleProduit());

        return resp;
    }

}
