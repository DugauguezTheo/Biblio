package formation_sopra.commande_service.api.dto.response;

import java.util.List;

import formation_sopra.commande_service.model.Commande;
import formation_sopra.commande_service.model.CommandeDetails;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommandeResponse {
    private Integer id;
    private List<CommandeDetails> commandeDetails;

    public static CommandeResponse convert(Commande commande) {
        CommandeResponse resp = new CommandeResponse();

        resp.setId(commande.getId());
        resp.setCommandeDetails(commande.getCommandeDetails());

        return resp;
    }
}
