package fr.formation.serviceproduit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.formation.serviceproduit.model.Produit;

public interface ProduitRepository extends JpaRepository<Produit, Integer> {

    public List<Produit> findByLibelle(String libelle);
}
