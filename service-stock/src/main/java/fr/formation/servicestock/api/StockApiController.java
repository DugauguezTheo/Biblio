package fr.formation.servicestock.api;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.formation.servicestock.feign.ProduitFeign;
import fr.formation.servicestock.model.Stock;
import fr.formation.servicestock.api.dto.request.CreateOrUpdateStockRequest;
import fr.formation.servicestock.api.dto.response.StockResponse;
import fr.formation.servicestock.exception.EntityNotFoundException;
import fr.formation.servicestock.repo.StockRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
@Log4j2
public class StockApiController {
    private final StockRepository repository;
    private final ProduitFeign produitFeign;

    @GetMapping
    public List<StockResponse> findAll() {
        log.debug("Listing des stocks ...");

        return this.repository.findAll().stream()
            .map(p -> {
                StockResponse resp = StockResponse.convert(p);

                return resp;
            })
            .toList()
        ;
    }

    @GetMapping("/{produitId}")
    public Stock findByProduitId(@PathVariable String produitId) {
        return this.repository.findByProduitId(produitId).orElseThrow(EntityNotFoundException::new);
    }

    @GetMapping("/is-disponible/{produitId}")
    public boolean isDisponible(@PathVariable String produitId) {
        log.debug("Est-ce que le produit {} est disponible ?", produitId);

        return this.repository.findByProduitId(produitId).orElseThrow(EntityNotFoundException::new).getQuantite() > 0;
    }

    @PostMapping
    public String create(@Valid @RequestBody CreateOrUpdateStockRequest request) {
        Stock stock = new Stock();

        log.debug("Création d'un nouveau stock ...");

        stock.setProduitId(request.produitId());
        stock.setQuantite(request.quantite());

        this.repository.save(stock);

        log.debug("Stock {} créé !", stock.getId());

        return stock.getId();
    }


    
    @PutMapping("/ajout/{produitId}")
    public String ajoutStock(@PathVariable String produitId, @Valid @RequestBody CreateOrUpdateStockRequest request) {

        log.debug("Vérification de l'existence du produit {} ...", produitId);

        boolean produitExists = this.produitFeign.findProduitById(produitId) > 0;
        
        if (produitExists){

            Stock stock = this.repository.findByProduitId(produitId).orElseThrow(EntityNotFoundException::new);


            log.debug("Ajout de {} quantité dans le stock {} ...", request.quantite(), produitId);

            stock.setQuantite(stock.getQuantite() + request.quantite());

            this.repository.save(stock);

            log.debug("Stock {} modifié !", produitId);

            return stock.getId();
        }

        return null;

    }

    @PutMapping("/retrait/{produitId}")
    public String retraitStock(@PathVariable String produitId, @Valid @RequestBody CreateOrUpdateStockRequest request) {

        log.debug("Vérification de l'existence du produit {} ...", produitId);

        boolean produitExists = this.produitFeign.findProduitById(produitId) > 0;
        
        if (produitExists){

            Stock stock = this.repository.findByProduitId(produitId).orElseThrow(EntityNotFoundException::new);


            log.debug("Retrait de {} quantité dans le stock {} ...", request.quantite(), produitId);

            stock.setQuantite(stock.getQuantite() + request.quantite());

            this.repository.save(stock);

            log.debug("Stock {} modifié !", produitId);

            return stock.getId();
        }

        return null;


    }

    @DeleteMapping("/{id}")
    public String deleteStockById(@PathVariable String id) {
        Stock stock = this.repository.findById(id).orElseThrow(EntityNotFoundException::new);

        log.debug("Suppression du stock {} ...", id);

        this.repository.deleteById(id);

        log.debug("Stock {} supprimé !", id);
        log.debug("Suppression du produit anciennement stocké {} ...", stock.getProduitId());

        this.produitFeign.deleteProduitById(stock.getProduitId());

        return stock.getId();
    }
}
