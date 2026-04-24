package formation_sopra.biblio.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import formation_sopra.biblio.dao.IDAOPersonne;
import formation_sopra.biblio.dto.request.AuthRequest;
import formation_sopra.biblio.dto.request.SubscriptionRequest;
import formation_sopra.biblio.dto.response.AuthResponse;
import formation_sopra.biblio.dto.response.EntityCreatedOrUpdatedResponse;
import formation_sopra.biblio.model.Personne;
import formation_sopra.biblio.security.jwt.JwtUtils;

import jakarta.validation.Valid;

@RestController
public class SecurityApiController {
    private final static Logger log = LoggerFactory.getLogger(SecurityApiController.class);
    private final AuthenticationManager authenticationManager;
    private final IDAOPersonne daoPersonne;
    private final PasswordEncoder passwordEncoder;

    public SecurityApiController(AuthenticationManager authenticationManager, IDAOPersonne daoPersonne, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.daoPersonne = daoPersonne;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/api/auth")
    public AuthResponse auth(@Valid @RequestBody AuthRequest request) {
        try {
            log.debug("Tentative d'authentification ...");

            Authentication authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Authentification validée !");

            return new AuthResponse(true, JwtUtils.generate(authentication));
        }

        catch (BadCredentialsException ex) {
            log.error("Authentification impossible : mauvais identifiants.");
        }

        catch (Exception ex) {
            log.error("Authentification impossible : erreur ({}).", ex.getClass().getSimpleName());
        }

        return new AuthResponse(false, "");
    }


    @PostMapping("/api/inscription")
    public EntityCreatedOrUpdatedResponse subscribe(@Valid @RequestBody SubscriptionRequest request) {
        Personne personne = new Personne();

        personne.setLogin(request.getUsername());
        personne.setPassword(this.passwordEncoder.encode(request.getPassword()));

        this.daoPersonne.save(personne);

        return new EntityCreatedOrUpdatedResponse(personne.getId());
    }
}
