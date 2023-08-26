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
@Table(name = "inventaires")
public class Inventaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date date;

    private String num;

    private Integer articles;

    @ManyToOne(optional = false)
    @JoinColumn(name = "utilisateurs_id", nullable = false)
    private Utilisateur utilisateur;

    @OneToMany(mappedBy = "inventaire", cascade = CascadeType.ALL)
    private List<InventaireArticle> inventaireArticles;

}
