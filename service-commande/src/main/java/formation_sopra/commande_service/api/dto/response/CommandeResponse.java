package formation_sopra.commande_service.api.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommandeResponse {
    private Integer id;

    public static CommandeResponse convert(Produit produit) {
        CommandeResponse resp = new CommandeResponse();

        resp.setId(produit.getId());
        resp.setLibelle(produit.getLibelle());
        resp.setPrix(produit.getPrix());

        return resp;
    }
}
