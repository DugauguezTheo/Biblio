package formation_sopra.commande_service.api.dto.request;

public record CreateOrUpdateCommandeDetailsRequest(double prixTotal, double prixUnitaire, String libelle) {
}
