package formation_sopra.commande_service.api.dto;

import formation_sopra.commande_service.api.dto.request.CreateOrUpdateCommandeDetailsRequest;
import formation_sopra.commande_service.api.dto.request.CreateOrUpdateCommandeRequest;
import formation_sopra.commande_service.api.dto.response.CommandeResponse;
import formation_sopra.commande_service.feign.ClientFeign;
import formation_sopra.commande_service.feign.ProduitFeign;
import formation_sopra.commande_service.feign.StockFeign;
import formation_sopra.commande_service.model.Commande;
import formation_sopra.commande_service.model.CommandeDetails;
import formation_sopra.commande_service.repository.CommandeDetailsRepository;
import formation_sopra.commande_service.repository.CommandeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/commande")
@RequiredArgsConstructor
@Log4j2
public class CommandeApiController {
    private final CommandeRepository repositoryC;
    private final CommandeDetailsRepository repositoryCD;
    private final ClientFeign clientFeign;
    private final ProduitFeign produitFeign;
    private final StockFeign stockFeign;

    @GetMapping
    public List<CommandeResponse> findAll() {
        log.debug("Listing des commandes ...");

        return this.repositoryC.findAll().stream()
                .map(c -> {
                    CommandeResponse resp = CommandeResponse.convert(c);

                    String nomClient = this.clientFeign.findNomClientById(c.getClientId());

                    resp.setNomClient(nomClient);

                    double prixTotal = c.getCommandeDetails().stream()
                        .mapToDouble(cd -> cd.getPrixUnitaireProduit()*cd.getQuantite())
                        .sum();

                    resp.setPrixTotal(prixTotal);

                    return resp;
                })
                .toList();
    }

    @GetMapping("/by-client-id/{clientId}")
    public List<CommandeResponse> findAllByClientId(@PathVariable Integer clientId){
        return this.repositoryC.findAllByClientId(clientId).stream()
            .map(c -> {
                CommandeResponse resp = CommandeResponse.convert(c);
                return resp;
            })
            .toList();
    }

    @PostMapping
    public CommandeResponse add(@RequestBody CreateOrUpdateCommandeRequest request){
        List<CommandeDetails> cds = request.cds();

        for(CommandeDetails cd : cds){

            Integer idProduit;
            // Vérification que le produit existe
            try{
                idProduit = this.produitFeign.findIdProduitByLibelle(cd.getLibelleProduit());
            } catch (Exception exception) {
                throw new RuntimeException("Le produit " + cd.getLibelleProduit() + " n'existe pas");
            }

            // Vérification des stocks
            if (this.stockFeign.isDisponible(idProduit)) {
                
            }
        }
        return null;
    }
}
