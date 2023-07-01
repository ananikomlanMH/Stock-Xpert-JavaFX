package com.anani.stockxpert.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "factures")
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String num;

    private String client;

    private Integer articles;

    private Integer total;

    private Date date;

    @ManyToOne(optional = false)
    @JoinColumn(name = "utilisateurs_id", nullable = false)
    private Utilisateur utilisateur;

    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL)
    private List<FactureArticle> factureArticles;

}

