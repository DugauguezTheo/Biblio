package formation_sopra.commande_service.api.dto.request;

public record CreateOrUpdateCommandeDetailsRequest(int quantite, double prixUnitaire, String libelle) {
}
