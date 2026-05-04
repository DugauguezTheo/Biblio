package fr.formation.servicestock.api.dto.response;


import fr.formation.servicestock.model.Stock;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StockResponse {
    private String id;
    private Integer produitId;
    private Integer quantite;

    public static StockResponse convert(Stock stock) {
        StockResponse resp = new StockResponse();

        resp.setId(stock.getId());
        resp.setProduitId(stock.getProduitId());
        resp.setQuantite(stock.getQuantite());

        return resp;
    }
}
