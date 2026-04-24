package formation_sopra.biblio.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import formation_sopra.biblio.model.Personne;


public interface IDAOPersonne extends JpaRepository<Personne, Integer> {
    public Optional<Personne> findByLogin(String login);
}
