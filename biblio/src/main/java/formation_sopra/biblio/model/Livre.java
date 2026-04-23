package formation_sopra.biblio.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "livre")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Livre implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "titre")
    private String titre;

    @Column(name = "resume")
    private String resume;

    @Column(name = "annee")
    private String annee;

    @ManyToOne
    private Auteur auteur;

    @ManyToOne
    private Editeur editeur;

    @ManyToOne
    private Collection collection;

    // @ManyToMany(fetch = FetchType.LAZY)
    // @JoinTable(
    //     name = "rel_livre__genres",
    //     joinColumns = @JoinColumn(name = "livre_id"),
    //     inverseJoinColumns = @JoinColumn(name = "genres_id")
    // )
    // @JsonIgnoreProperties(value = { "livres" }, allowSetters = true)
    // private Set<Genre> genreses = new HashSet<>();

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JsonIgnoreProperties(value = { "livres" }, allowSetters = true)
    // private Avis avis;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Livre id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return this.titre;
    }

    public Livre titre(String titre) {
        this.setTitre(titre);
        return this;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getResume() {
        return this.resume;
    }

    public Livre resume(String resume) {
        this.setResume(resume);
        return this;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getAnnee() {
        return this.annee;
    }

    public Livre annee(String annee) {
        this.setAnnee(annee);
        return this;
    }

    public void setAnnee(String annee) {
        this.annee = annee;
    }

    public Auteur getAuteur() {
        return this.auteur;
    }

    public void setAuteur(Auteur auteur) {
        this.auteur = auteur;
    }

    public Livre auteur(Auteur auteur) {
        this.setAuteur(auteur);
        return this;
    }

    public Editeur getEditeur() {
        return this.editeur;
    }

    public void setEditeur(Editeur editeur) {
        this.editeur = editeur;
    }

    public Livre editeur(Editeur editeur) {
        this.setEditeur(editeur);
        return this;
    }

    public Collection getCollection() {
        return this.collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public Livre collection(Collection collection) {
        this.setCollection(collection);
        return this;
    }

    // public Set<Genre> getGenreses() {
    //     return this.genreses;
    // }

    // public void setGenreses(Set<Genre> genres) {
    //     this.genreses = genres;
    // }

    // public Livre genreses(Set<Genre> genres) {
    //     this.setGenreses(genres);
    //     return this;
    // }

    // public Livre addGenres(Genre genre) {
    //     this.genreses.add(genre);
    //     return this;
    // }

    // public Livre removeGenres(Genre genre) {
    //     this.genreses.remove(genre);
    //     return this;
    // }

    // public Avis getAvis() {
    //     return this.avis;
    // }

    // public void setAvis(Avis avis) {
    //     this.avis = avis;
    // }

    // public Livre avis(Avis avis) {
    //     this.setAvis(avis);
    //     return this;
    // }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Livre)) {
            return false;
        }
        return getId() != null && getId().equals(((Livre) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Livre{" +
            "id=" + getId() +
            ", titre='" + getTitre() + "'" +
            ", resume='" + getResume() + "'" +
            ", annee='" + getAnnee() + "'" +
            "}";
    }
}
