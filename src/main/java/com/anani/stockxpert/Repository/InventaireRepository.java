package com.anani.stockxpert.Repository;

import com.anani.stockxpert.Model.*;
import com.anani.stockxpert.Util.HibernateUtil;
import javafx.collections.ObservableList;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class InventaireRepository implements com.anani.stockxpert.Service.InventaireService {

    private static SessionFactory sessionFactory;
    private Transaction transaction;
    private Session session;

    public InventaireRepository() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    @Override
    public List<Inventaire> getAll() {
        session = sessionFactory.openSession();
        try {
            String hql = "From Inventaire ";

            return session.createQuery(hql).list();
        } finally {
            session.close();
        }
    }

    @Override
    public List<InventaireArticle> getAllInventaireItems(Integer id) {
        session = sessionFactory.openSession();
        try {
            String hql = "From InventaireArticle i WHERE i.inventaire.id =" + id;

            return session.createQuery(hql).list();
        } finally {
            session.close();
        }
    }

    @Override
    public Inventaire save(ObservableList<TempInventaireItem> items) {
        session = sessionFactory.openSession();

        try {
            transaction = session.beginTransaction();

            Utilisateur utilisateur = session.get(Utilisateur.class, 1);

            Inventaire inventaire = new Inventaire();

            // Génération du numéro de facture unique
            String numeroInventaire = generateNumeroInventaire(session);

            inventaire.setNum(numeroInventaire);
            inventaire.setDate(new Date());
            inventaire.setUtilisateur(utilisateur);
            inventaire.setArticles(items.size());
            session.persist(inventaire);

            for (TempInventaireItem Item : items) {

                InventaireArticle inventaireArticle = new InventaireArticle();
                inventaireArticle.setQte(Item.getQte());

                // Article
                Article article = new ArticleRepository().getFromString(Item.getArticle());
                inventaireArticle.setArticle(article);
                inventaireArticle.setInventaire(inventaire);

                Stock stock = session.get(Stock.class, Item.getId());

                inventaireArticle.setQte_reel(stock.getQte_stock());

                session.persist(inventaireArticle);
            }

            transaction.commit();

            return inventaire;
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }

        return null;
    }

    private String generateNumeroInventaire(Session session) {
        // Obtenir l'année courante
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        // Requête pour obtenir le compteur actuel des factures
        Query<Integer> query = session.createQuery("SELECT MAX(inventaire.id) FROM Inventaire inventaire WHERE YEAR(inventaire.date) = :year", Integer.class);
        query.setParameter("year", year);
        Integer maxId = query.uniqueResult();

        int compteur = (maxId != null) ? maxId + 1 : 1;

        // Générer le numéro de facture unique en combinant l'année courante et le compteur
        return "INVT-" + year + "-" + String.format("%04d", compteur);
    }


}
