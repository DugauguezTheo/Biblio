package fr.formation.serviceproduit.api;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/produit")
public class ProduitController {

    private final ProduitService ProduitService;
    
    public ProduitController(ProduitService ProduitService) {
        this.ProduitService = ProduitService;
    }

    @GetMapping
    public List<Produit> findAll() {
        return ProduitService.findAll();
    }

    @GetMapping("/{id}")
    public Produit findById(Integer id) {
        return ProduitService.findById(id);
    }

    @PostMapping
    public Produit save(@RequestBody Produit Produit) {
        return ProduitService.save(Produit);
    }

    @PutMapping("/{id}")
    public Produit update(@RequestBody Produit Produit) {
        return ProduitService.save(Produit);
    }

    @DeleteMapping("/{id}")
    public void deleteById(Integer id) {
        ProduitService.deleteById(id);
    }
}

