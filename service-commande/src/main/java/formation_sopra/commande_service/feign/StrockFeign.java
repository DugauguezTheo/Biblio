package formation_sopra.commande_service.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value="stock-service", url = "http://localhost:8083/api/stock")
public interface StrockFeign {

}
