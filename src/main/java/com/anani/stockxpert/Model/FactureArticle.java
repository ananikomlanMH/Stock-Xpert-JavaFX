package com.anani.stockxpert.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "factures_articles")
public class FactureArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer qte;

    private Integer pv;

    @ManyToOne(optional = false)
    @JoinColumn(name = "factures_id", nullable = false)
    private Facture facture;

    @ManyToOne(optional = false)
    @JoinColumn(name = "articles_id", nullable = false)
    private Article article;
}
