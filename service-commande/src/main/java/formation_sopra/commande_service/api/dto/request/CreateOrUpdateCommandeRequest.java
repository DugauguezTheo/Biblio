package formation_sopra.commande_service.api.dto.request;

public record CreateOrUpdateCommandeRequest(double prixTotal, double prixUnitaire, String libelle) {
}
