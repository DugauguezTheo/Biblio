package formation_sopra.commande_service.api.dto.response;

import formation_sopra.commande_service.model.Commande;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommandeResponse {
    private Integer id;

    public static CommandeResponse convert(Commande commande) {
        CommandeResponse resp = new CommandeResponse();

        resp.setId(commande.getId());

        return resp;
    }
}
