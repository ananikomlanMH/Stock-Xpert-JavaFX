package com.anani.stockxpert.Service;

import com.anani.stockxpert.Model.Categorie;
import com.anani.stockxpert.Model.Depense;

import java.util.List;

public interface DepenseService {
    List<Depense> getAll();

    void save(Depense depense);

    void edit(Depense depense);

    void delete(Integer id);

    Integer getSum();

    List<Object[]> getTotalDepense();
}
