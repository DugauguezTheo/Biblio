package fr.formation.servicestock.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.formation.servicestock.model.Stock;

public interface StockRepository extends JpaRepository<Stock, String> {

    public Optional<Stock> findByProduitId(String produitId);
    

}
