package com.anani.stockxpert.Service;

import com.anani.stockxpert.Model.Facture;
import com.anani.stockxpert.Model.FactureArticle;
import com.anani.stockxpert.Model.TempFactureItem;
import javafx.collections.ObservableList;

import java.util.List;

public interface FactureService {
    List<Facture> getAll();

    List<FactureArticle>getAllFactureItems(Integer id);

    Facture save(ObservableList<TempFactureItem> items, String text);

    List<Object[]> getRevenue();
}
