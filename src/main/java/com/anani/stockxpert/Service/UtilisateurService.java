package com.anani.stockxpert.Service;

import com.anani.stockxpert.Model.Utilisateur;

import java.util.List;

public interface UtilisateurService {
    List<Utilisateur> getAll();

    void save(Utilisateur utilisateur);

    void edit(Utilisateur utilisateur);

    void delete(Integer id);
}
