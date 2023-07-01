package com.anani.stockxpert.Repository;

import com.anani.stockxpert.Model.Article;
import com.anani.stockxpert.Util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ArticleRepository implements com.anani.stockxpert.Service.ArticleService {

    private static SessionFactory sessionFactory;
    private Transaction transaction;
    private Session session;

    public ArticleRepository(){
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    @Override
    public List<Article>getAll(){
        session = sessionFactory.openSession();
        try{
            String hql = "From Article ";

            return session.createQuery(hql).list();
        }
        finally {
            session.close();
        }
    }

    @Override
    public void save(Article article){
        session = sessionFactory.openSession();

        try{
            transaction = session.beginTransaction();

            session.persist(article);

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
    public void edit(Article article){
        session = sessionFactory.openSession();

        try{
            transaction = session.beginTransaction();

            Article art = session.get(Article.class, article.getId());
            art.setLibelle(article.getLibelle());
            art.setPv(article.getPv());
            art.setCategorie(article.getCategorie());
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

            Article article = session.get(Article.class, id);
            session.remove(article);
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
    public Article getFromString(String libelle){
        session = sessionFactory.openSession();
        try {
            String hql = "FROM Article c WHERE c.libelle = :libelle";
            Query<Article> query = session.createQuery(hql, Article.class);
            query.setParameter("libelle", libelle);
            return query.uniqueResult();
        } finally {
            session.close();
        }
    }

    @Override
    public List<?> getCountCategorie() {
        session = sessionFactory.openSession();
        try {

            String hql = "SELECT COUNT(a.categorie.id) as c, a.categorie.libelle FROM Article a GROUP BY a.categorie.id";
            return session.createQuery(hql).list();

        } finally {
            session.close();
        }
    }

}
