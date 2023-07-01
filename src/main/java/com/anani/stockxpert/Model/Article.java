package com.anani.stockxpert.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String libelle;

    private Integer pv;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categories_id", nullable = false)
    private Categorie categorie;

    public Article(String text) {
        this.libelle = text;
    }
}
