package fr.formation.servicestock.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "produit-service", url = "http://localhost:8082/api/produit")
public interface ProduitFeign {
    @GetMapping("/{id}")
    public Integer findProduitById(@PathVariable String produitId);

    @DeleteMapping("/{id}")
    public void deleteProduitById(@PathVariable String produitId);

}
