package com.anani.stockxpert.Service;

import com.anani.stockxpert.Model.Caisse;

import java.util.List;

public interface CaisseService {
    List<Caisse> getAll();

    void save(Caisse caisse);

    Integer getSum();
}
