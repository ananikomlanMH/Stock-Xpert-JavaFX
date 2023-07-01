package com.anani.stockxpert.Repository;

import com.anani.stockxpert.Model.Categorie;
import com.anani.stockxpert.Util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class CategorieRepository implements com.anani.stockxpert.Service.CategorieService {

    private static SessionFactory sessionFactory;
    private Transaction transaction;
    private Session session;

    public CategorieRepository(){
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    @Override
    public List<Categorie>getAll(){
        session = sessionFactory.openSession();
        try{
            String hql = "From Categorie ";

            return session.createQuery(hql).list();
        }
        finally {
            session.close();
        }
    }

    @Override
    public void save(Categorie categorie){
        session = sessionFactory.openSession();

        try{
            transaction = session.beginTransaction();

            session.persist(categorie);

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
    public void edit(Categorie categorie){
        session = sessionFactory.openSession();

        try{
            transaction = session.beginTransaction();

            Categorie cat = session.get(Categorie.class, categorie.getId());
            cat.setLibelle(categorie.getLibelle());
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

            Categorie categorie = session.get(Categorie.class, id);
            session.remove(categorie);
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
    public Categorie getFromString(String libelle){
        session = sessionFactory.openSession();
        try {
            String hql = "FROM Categorie c WHERE c.libelle = :libelle";
            Query<Categorie> query = session.createQuery(hql, Categorie.class);
            query.setParameter("libelle", libelle);
            return query.uniqueResult();
        } finally {
            session.close();
        }
    }

}
