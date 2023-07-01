package com.anani.stockxpert.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer qte_stock;

    private Integer qte_alerte;

    @ManyToOne(optional = false)
    @JoinColumn(name = "articles_id", nullable = false)
    private Article article;

}
