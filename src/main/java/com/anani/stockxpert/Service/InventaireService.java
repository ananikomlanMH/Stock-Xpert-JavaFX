package com.anani.stockxpert.Service;

import com.anani.stockxpert.Model.Inventaire;
import com.anani.stockxpert.Model.InventaireArticle;
import com.anani.stockxpert.Model.TempInventaireItem;
import javafx.collections.ObservableList;

import java.util.List;

public interface InventaireService {
    List<Inventaire> getAll();

    List<InventaireArticle> getAllInventaireItems(Integer id);

    Inventaire save(ObservableList<TempInventaireItem> items);
}
