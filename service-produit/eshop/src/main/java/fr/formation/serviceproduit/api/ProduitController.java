package fr.formation.serviceproduit.api;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.formation.serviceproduit.model.Produit;
import fr.formation.serviceproduit.repository.ProduitRepository;


@RestController
@RequestMapping("/api/produit")
public class ProduitController {

    private final ProduitRepository ProduitRepository;
    
    public ProduitController(ProduitRepository ProduitRepository) {
        this.ProduitRepository = ProduitRepository;
    }

    @GetMapping
    public List<Produit> findAll() {
        return ProduitRepository.findAll();
    }

    @GetMapping("/{id}")
    public Produit findById(Integer id) {
        return ProduitRepository.findById(id).orElseThrow();
    }

    @GetMapping("/id-by-libelle-produit/{libelle}")
    public Integer findIdProduitByLibelle(@PathVariable String libelle) {
        return this.ProduitRepository.findByLibelle(libelle).getFirst().getId();
    }

    @PostMapping
    public Produit save(@RequestBody Produit Produit) {
        return ProduitRepository.save(Produit);
    }

    @PutMapping("/{id}")
    public Produit update(@RequestBody Produit Produit) {
        return ProduitRepository.save(Produit);
    }

    @DeleteMapping("/{id}")
    public void deleteById(Integer id) {
        ProduitRepository.deleteById(id);
    }
}

