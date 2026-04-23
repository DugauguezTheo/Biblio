package formation_sopra.biblio.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A Avis.
 */
@Entity
@Table(name = "avis")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Avis implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "note")
    private Double note;

    @Column(name = "commentaires")
    private String commentaires;

    @Column(name = "date")
    private Instant date;

    @ManyToOne
    // @JsonIgnoreProperties(value = { "auteur", "editeur", "collection", "genreses", "avis" }, allowSetters = true)
    private Livre livre;
    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Avis id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getNote() {
        return this.note;
    }

    public Avis note(Double note) {
        this.setNote(note);
        return this;
    }

    public void setNote(Double note) {
        this.note = note;
    }

    public String getCommentaires() {
        return this.commentaires;
    }

    public Avis commentaires(String commentaires) {
        this.setCommentaires(commentaires);
        return this;
    }

    public void setCommentaires(String commentaires) {
        this.commentaires = commentaires;
    }

    public Instant getDate() {
        return this.date;
    }

    public Avis date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Livre getLivre() {
        return this.livre;
    }

    public void setLivre(Livre livre) {
        this.livre = livre;
    }

    // public Set<Livre> getLivres() {
    //     return this.livres;
    // }

    // public void setLivres(Set<Livre> livres) {
    //     if (this.livres != null) {
    //         this.livres.forEach(i -> i.setAvis(null));
    //     }
    //     if (livres != null) {
    //         livres.forEach(i -> i.setAvis(this));
    //     }
    //     this.livres = livres;
    // }

    // public Avis livres(Set<Livre> livres) {
    //     this.setLivres(livres);
    //     return this;
    // }

    // public Avis addLivre(Livre livre) {
    //     this.livres.add(livre);
    //     livre.setAvis(this);
    //     return this;
    // }

    // public Avis removeLivre(Livre livre) {
    //     this.livres.remove(livre);
    //     livre.setAvis(null);
    //     return this;
    // }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Avis)) {
            return false;
        }
        return getId() != null && getId().equals(((Avis) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Avis{" +
            "id=" + getId() +
            ", note=" + getNote() +
            ", commentaires='" + getCommentaires() + "'" +
            ", date='" + getDate() + "'" +
            "}";
    }
}
