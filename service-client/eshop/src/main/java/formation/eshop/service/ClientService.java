package formation.eshop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import formation.eshop.dao.IDAOClient;
import formation.eshop.dto.response.CommandeResponse;
import formation.eshop.feign.CommandeFeign;
import formation.eshop.model.Client;

@Service
public class ClientService {

    private final IDAOClient daoClient;
    private final CommandeFeign commandeFeign;

    public ClientService(IDAOClient daoClient, CommandeFeign commandeFeign) {
        this.daoClient = daoClient;
        this.commandeFeign = commandeFeign;
    }

    public List<Client> findAll() {
        return daoClient.findAll();
    }

    public Client findById(Integer id) {
        return daoClient.findById(id).orElseThrow(() -> new RuntimeException("Client non trouvé"));
    }

    public Client save(Client client) {
        return daoClient.save(client);
    }

    public String findNomClientById(Integer id){
        return daoClient.findById(id).orElse(null).getNom();
    }

    public void deleteById(Integer id) {
        List<CommandeResponse> commandes = commandeFeign.findAllByClientId(id);
        if (commandes.isEmpty()) {
            daoClient.deleteById(id);
        } else {
            throw new RuntimeException("Impossible de supprimer le client car il a des commandes associées");
        }
    }

}
