package fr.formation.servicestock.model;


import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Stock {
    @Id
    @UuidGenerator
    private String id;

    @Column(nullable = false)
    private String produitId;

    @Column(nullable = false)
    private Integer quantite;


    
}
