package com.anani.stockxpert.Repository;

import com.anani.stockxpert.Model.Caisse;
import com.anani.stockxpert.Util.HibernateUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class CaisseRepository implements com.anani.stockxpert.Service.CaisseService {

    private static SessionFactory sessionFactory;
    private Transaction transaction;
    private Session session;

    public CaisseRepository(){
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    @Override
    public List<Caisse>getAll(){
        session = sessionFactory.openSession();
        try{
            String hql = "From Caisse ";

            return session.createQuery(hql).list();
        }
        finally {
            session.close();
        }
    }

    @Override
    public void save(Caisse caisse){
        session = sessionFactory.openSession();

        try{
            transaction = session.beginTransaction();

            session.persist(caisse);

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
        ObservableList<Caisse> data = FXCollections.observableArrayList(getAll());
        Integer som = 0;
        for (Caisse caisse : data) {
            som += caisse.getMontant();
        }
        return som;
    }

}
