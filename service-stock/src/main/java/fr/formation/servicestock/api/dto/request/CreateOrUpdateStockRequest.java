package fr.formation.servicestock.api.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateOrUpdateStockRequest(@NotBlank String produitId, @Positive Integer quantite) {

}
