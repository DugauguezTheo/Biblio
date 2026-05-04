package formation.eshop.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import formation.eshop.model.Client;

public interface IDAOClient extends JpaRepository<Client, Integer> {

}
