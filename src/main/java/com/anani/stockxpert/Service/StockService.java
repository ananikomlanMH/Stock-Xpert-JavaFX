package com.anani.stockxpert.Service;

import com.anani.stockxpert.Model.Stock;

import java.util.List;

public interface StockService {
    List<Stock> getAll();

    List<Stock>getAllFromSearch(String s);

    void save(Stock stock);

    void edit(Stock stock);

    void delete(Integer id);

    List<?> getAllArticle();

    Stock getFromString(String libelle);
}
