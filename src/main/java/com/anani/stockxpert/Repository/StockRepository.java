package com.anani.stockxpert.Repository;

import com.anani.stockxpert.Model.Stock;
import com.anani.stockxpert.Util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class StockRepository implements com.anani.stockxpert.Service.StockService {

    private static SessionFactory sessionFactory;
    private Transaction transaction;
    private Session session;

    public StockRepository(){
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    @Override
    public List<Stock>getAll(){
        session = sessionFactory.openSession();
        try{
            String hql = "From Stock ";

            return session.createQuery(hql).list();
        }
        finally {
            session.close();
        }
    }

    @Override
    public List<Stock>getAllFromSearch(String q){
        session = sessionFactory.openSession();
        try{
            String hql = "From Stock st WHERE st.article.libelle LIKE '%"+q+"%'";
            Query<Stock> query = session.createQuery(hql, Stock.class);
//            query.setParameter("lib", q);
            return query.list();
        }
        finally {
            session.close();
        }
    }

    @Override
    public void save(Stock stock){
        session = sessionFactory.openSession();

        try{
            transaction = session.beginTransaction();

            session.persist(stock);

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
    public void edit(Stock stock){
        session = sessionFactory.openSession();

        try{
            transaction = session.beginTransaction();

            Stock data = session.get(Stock.class, stock.getId());
            data.setQte_stock(stock.getQte_stock());
            data.setQte_alerte(stock.getQte_alerte());
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

            Stock stock = session.get(Stock.class, id);
            session.remove(stock);
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
    public List<?> getAllArticle() {
        session = sessionFactory.openSession();
        try {
            String hql = "SELECT DISTINCT s.article.libelle FROM Stock s";

            return session.createQuery(hql).list();
        } finally {
            session.close();
        }
    }

    @Override
    public Stock getFromString(String libelle){
        session = sessionFactory.openSession();
        try {
            String hql = "FROM Stock s WHERE s.article.libelle = :libelle";
            Query<Stock> query = session.createQuery(hql, Stock.class);
            query.setParameter("libelle", libelle);
            return query.uniqueResult();
        } finally {
            session.close();
        }
    }

}
