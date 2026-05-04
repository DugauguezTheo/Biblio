package formation_sopra.commande_service.api.dto.response;

import java.util.List;

import formation_sopra.commande_service.model.Commande;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommandeResponse {
    private Integer id;
    private List<CommandeDetailsResponse> commandeDetails;
    private Integer clientId;
    private String nomClient;
    private double prixTotal;

    public static CommandeResponse convert(Commande commande) {
        CommandeResponse resp = new CommandeResponse();

        resp.setId(commande.getId());
        resp.setCommandeDetails(commande.getCommandeDetails().stream()
            .map(cd -> CommandeDetailsResponse.convert(cd))
            .toList()
        );

        return resp;
    }
}
