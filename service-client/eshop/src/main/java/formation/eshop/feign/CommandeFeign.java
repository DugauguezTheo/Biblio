package formation.eshop.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-commande", url = "http://localhost:8084/api/commande")
public interface CommandeFeign {

    @GetMapping("/by-client-id/{clientId}")
    public String findAllByClientId(@PathVariable Integer clientId);

}
