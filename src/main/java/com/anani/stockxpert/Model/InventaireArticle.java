package com.anani.stockxpert.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "inventaires_articles")
public class InventaireArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer qte;

    private Integer qte_reel;

    @ManyToOne(optional = false)
    @JoinColumn(name = "inventaires_id ", nullable = false)
    private Inventaire inventaire;

    @ManyToOne(optional = false)
    @JoinColumn(name = "articles_id", nullable = false)
    private Article article;
}
