package formation_sopra.commande_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "client-service", url = "http://localhost:8081/api/client")
public interface ClientFeign {
    @GetMapping("/by-commande-id/client/{commandeId}")
    String findClientByCommandeId(Integer id);
}
