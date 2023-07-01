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

public class FactureRepository implements com.anani.stockxpert.Service.FactureService {

    private static SessionFactory sessionFactory;
    private Transaction transaction;
    private Session session;

    public FactureRepository(){
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    @Override
    public List<Facture>getAll(){
        session = sessionFactory.openSession();
        try{
            String hql = "From Facture ";

            return session.createQuery(hql).list();
        }
        finally {
            session.close();
        }
    }

    @Override
    public List<FactureArticle>getAllFactureItems(Integer id){
        session = sessionFactory.openSession();
        try{
            String hql = "From FactureArticle f WHERE f.facture.id ="+id;

            return session.createQuery(hql).list();
        }
        finally {
            session.close();
        }
    }

    @Override
    public Facture save(ObservableList<TempFactureItem> items, String client){
        session = sessionFactory.openSession();

        try{
            transaction = session.beginTransaction();

            Utilisateur utilisateur = session.get(Utilisateur.class, 1);

            Facture facture = new Facture();
            facture.setClient(client);

            // Génération du numéro de facture unique
            String numeroFacture = generateNumeroFacture(session);

            facture.setNum(numeroFacture);
            facture.setDate(new Date());
            facture.setUtilisateur(utilisateur);
            session.persist(facture);
            Integer qte = 0;
            Integer total = 0;
            for (TempFactureItem Item : items) {

                qte += Item.getQte();
                total += Item.getQte() * Item.getPu();

                FactureArticle factureArticle = new FactureArticle();
                factureArticle.setPv(Item.getPu());
                factureArticle.setQte(Item.getQte());

                // Article
                Article article = new ArticleRepository().getFromString(Item.getArticle());
                factureArticle.setArticle(article);
                factureArticle.setFacture(facture);
                session.persist(factureArticle);

                Stock stock = session.get(Stock.class, Item.getId());
                stock.setQte_stock(stock.getQte_stock() - factureArticle.getQte());
                session.persist(stock);
            }
            facture.setArticles(qte);
            facture.setTotal(total);
            session.persist(facture);

            Caisse caisse = new Caisse(null, facture.getNum(), facture.getTotal(), facture.getDate(), facture.getUtilisateur());
            session.persist(caisse);
            transaction.commit();

            return facture;
        }catch (Exception e){
            e.printStackTrace();
            if(transaction != null){
                transaction.rollback();
            }
        }
        finally {
            session.close();
        }

        return null;
    }

    private String generateNumeroFacture(Session session) {
        // Obtenir l'année courante
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        // Requête pour obtenir le compteur actuel des factures
        Query<Integer> query = session.createQuery("SELECT MAX(facture.id) FROM Facture facture WHERE YEAR(facture.date) = :year", Integer.class);
        query.setParameter("year", year);
        Integer maxId = query.uniqueResult();

        int compteur = (maxId != null) ? maxId + 1 : 1;

        // Générer le numéro de facture unique en combinant l'année courante et le compteur
        return "FACT-" + year + "-" + String.format("%04d", compteur);
    }

    @Override
    public List<Object[]> getRevenue() {
        session = sessionFactory.openSession();
        try {
            // Récupération de l'année en cours
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);

            // Requête pour récupérer les chiffres d'affaires par mois de l'année en cours, y compris les mois sans chiffre
            String hql = "SELECT m.month, COALESCE(SUM(f.total), 0) " +
                    "FROM (SELECT 1 AS month UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 " +
                    "UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 " +
                    "UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12) m " +
                    "LEFT JOIN Facture f ON MONTH(f.date) = m.month AND YEAR(f.date) = :year " +
                    "GROUP BY m.month";

            Query<Object[]> query = session.createQuery(hql);
            query.setParameter("year", currentYear);
            return query.getResultList();

        } finally {
            session.close();
        }
    }



}
