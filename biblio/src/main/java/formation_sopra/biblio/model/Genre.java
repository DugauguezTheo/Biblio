package formation_sopra.biblio.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Schema(description = "not an ignored comment")
@Entity
@Table(name = "genre")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Genre implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "libelle")
    private String libelle;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "genreses")
    @JsonIgnoreProperties(value = { "auteur", "editeur", "collection", "genreses", "avis" }, allowSetters = true)
    private Set<Livre> livres = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Genre id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public Genre libelle(String libelle) {
        this.setLibelle(libelle);
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Set<Livre> getLivres() {
        return this.livres;
    }

    public void setLivres(Set<Livre> livres) {
        if (this.livres != null) {
            this.livres.forEach(i -> i.removeGenres(this));
        }
        if (livres != null) {
            livres.forEach(i -> i.addGenres(this));
        }
        this.livres = livres;
    }

    public Genre livres(Set<Livre> livres) {
        this.setLivres(livres);
        return this;
    }

    public Genre addLivre(Livre livre) {
        this.livres.add(livre);
        livre.getGenreses().add(this);
        return this;
    }

    public Genre removeLivre(Livre livre) {
        this.livres.remove(livre);
        livre.getGenreses().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Genre)) {
            return false;
        }
        return getId() != null && getId().equals(((Genre) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Genre{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            "}";
    }
}
