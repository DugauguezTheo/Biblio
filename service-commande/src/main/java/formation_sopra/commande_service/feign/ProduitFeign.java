package formation_sopra.commande_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "produit-service", url = "http://localhost:8082/api/produit")
public interface ProduitFeign {

    @GetMapping("/by-produit-id/prix/{produitId}")
    public double findPrixByProduitId(@PathVariable Integer produitId);

    @DeleteMapping("/by-produit-id/{produitId}")
    public String deleteAllByProduitId(@PathVariable Integer produitId);

}
