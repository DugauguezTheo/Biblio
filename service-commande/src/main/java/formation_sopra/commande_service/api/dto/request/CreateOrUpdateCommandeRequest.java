package formation_sopra.commande_service.api.dto.request;

import java.util.List;

import formation_sopra.commande_service.model.CommandeDetails;

public record CreateOrUpdateCommandeRequest(Integer idClient, List<CommandeDetails> cds) {

}
