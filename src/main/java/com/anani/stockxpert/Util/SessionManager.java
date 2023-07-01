package com.anani.stockxpert.Util;

import com.anani.stockxpert.Model.Utilisateur;

public class SessionManager {
    private static SessionManager instance;
    private Utilisateur loggedInUtilisateur;

    private SessionManager() {
        // Empêche l'instanciation directe de la classe
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return loggedInUtilisateur != null;
    }

    public void login(Utilisateur user) {
        loggedInUtilisateur = user;
    }

    public void logout() {
        loggedInUtilisateur = null;
    }

    public Utilisateur getLoggedInUtilisateur() {
        return loggedInUtilisateur;
    }
}
