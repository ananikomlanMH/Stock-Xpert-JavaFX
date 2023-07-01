package com.anani.stockxpert.Service;

import com.anani.stockxpert.Model.Categorie;

import java.util.List;

public interface CategorieService {
    List<Categorie> getAll();

    void save(Categorie categorie);

    void edit(Categorie categorie);

    void delete(Integer id);

    Categorie getFromString(String libelle);
}
