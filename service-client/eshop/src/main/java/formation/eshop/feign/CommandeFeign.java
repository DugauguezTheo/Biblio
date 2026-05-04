package formation.eshop.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import formation.eshop.dto.response.CommandeResponse;

@FeignClient(value = "service-commande", url = "http://localhost:8084/api/commande")
public interface CommandeFeign {

    @GetMapping("/by-client-id/{clientId}")
    public List<CommandeResponse> findAllByClientId(@PathVariable Integer clientId);

}
