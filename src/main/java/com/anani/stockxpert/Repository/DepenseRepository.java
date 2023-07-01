package com.anani.stockxpert.Repository;

import com.anani.stockxpert.Model.Depense;
import com.anani.stockxpert.Util.HibernateUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Calendar;
import java.util.List;

public class DepenseRepository implements com.anani.stockxpert.Service.DepenseService {

    private static SessionFactory sessionFactory;
    private Transaction transaction;
    private Session session;

    public DepenseRepository(){
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    @Override
    public List<Depense>getAll(){
        session = sessionFactory.openSession();
        try{
            String hql = "From Depense ";

            return session.createQuery(hql).list();
        }
        finally {
            session.close();
        }
    }

    @Override
    public void save(Depense depense){
        session = sessionFactory.openSession();

        try{
            transaction = session.beginTransaction();

            session.persist(depense);

            transaction.commit();
        }catch (Exception e){
            e.printStackTrace();
            if(transaction != null){
                transaction.rollback();
            }
        }
        finally {
            session.close();
        }

    }

    @Override
    public void edit(Depense depense){
        session = sessionFactory.openSession();

        try{
            transaction = session.beginTransaction();

            Depense cat = session.get(Depense.class, depense.getId());
            cat.setMotif(depense.getMotif());
            cat.setMontant(depense.getMontant());
            cat.setDate(depense.getDate());
            transaction.commit();

        }catch (Exception e){
            e.printStackTrace();
            if(transaction != null){
                transaction.rollback();
            }
        }
        finally {
            session.close();
        }

    }

    @Override
    public void delete(Integer id){
        session = sessionFactory.openSession();

        try{
            transaction = session.beginTransaction();

            Depense depense = session.get(Depense.class, id);
            session.remove(depense);
            transaction.commit();
        }catch (Exception e){
            e.printStackTrace();
            if(transaction != null){
                transaction.rollback();
            }
        }
        finally {
            session.close();
        }
    }

    @Override
    public Integer getSum(){
        ObservableList<Depense> data = FXCollections.observableArrayList(getAll());
        Integer som = 0;
        for (Depense depense : data) {
            som += depense.getMontant();
        }
        return som;
    }

    @Override
    public List<Object[]> getTotalDepense() {
        session = sessionFactory.openSession();
        try {
            // Récupération de l'année en cours
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);

            // Requête pour récupérer les chiffres d'affaires par mois de l'année en cours, y compris les mois sans chiffre
            String hql = "SELECT m.month, COALESCE(SUM(f.montant), 0) " +
                    "FROM (SELECT 1 AS month UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 " +
                    "UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 " +
                    "UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12) m " +
                    "LEFT JOIN Depense f ON MONTH(f.date) = m.month AND YEAR(f.date) = :year " +
                    "GROUP BY m.month";

            Query<Object[]> query = session.createQuery(hql);
            query.setParameter("year", currentYear);
            return query.getResultList();

        } finally {
            session.close();
        }
    }
}
