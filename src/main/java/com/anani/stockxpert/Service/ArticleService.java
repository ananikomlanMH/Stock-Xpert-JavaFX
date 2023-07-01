package com.anani.stockxpert.Service;

import com.anani.stockxpert.Model.Article;

import java.util.List;

public interface ArticleService {
    List<Article> getAll();

    void save(Article categorie);

    void edit(Article categorie);

    void delete(Integer id);

    Article getFromString(String libelle);

    List<?>getCountCategorie();
}
