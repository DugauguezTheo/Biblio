package formation_sopra.commande_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value="stock-service", url = "http://localhost:8083/api/stock")
public interface StockFeign {

    @GetMapping("/is-disponible/{produitId}")
    public boolean isDisponible(@PathVariable Integer produitId, @RequestParam int quantite);

    @PutMapping("/retrait/{produitId}")
    public String retraitStock(@PathVariable Integer produitId, @RequestParam int quantite);
}
