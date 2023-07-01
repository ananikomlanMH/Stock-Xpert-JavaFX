package com.anani.stockxpert.Repository;

import com.anani.stockxpert.Model.Utilisateur;
import com.anani.stockxpert.Util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class UtilisateurRepository implements com.anani.stockxpert.Service.UtilisateurService {

    private static SessionFactory sessionFactory;
    private Transaction transaction;
    private Session session;

    public UtilisateurRepository() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    @Override
    public List<Utilisateur> getAll() {
        session = sessionFactory.openSession();
        try {
            String hql = "From Utilisateur ";

            return session.createQuery(hql).list();
        } finally {
            session.close();
        }
    }

    @Override
    public void save(Utilisateur utilisateur) {
        session = sessionFactory.openSession();

        try {
            transaction = session.beginTransaction();

            session.persist(utilisateur);

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }

    }

    @Override
    public void edit(Utilisateur utilisateur) {
        session = sessionFactory.openSession();

        try {
            transaction = session.beginTransaction();

            Utilisateur data = session.get(Utilisateur.class, utilisateur.getId());
            data.setNom(utilisateur.getNom());
            data.setPrenom(utilisateur.getPrenom());
            data.setTelephone(utilisateur.getTelephone());
            data.setAdresse(utilisateur.getAdresse());
            data.setLogin(utilisateur.getLogin());
            data.setPassword(utilisateur.getPassword());
            data.setType(utilisateur.getType());

            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }

    }

    @Override
    public void delete(Integer id) {
        session = sessionFactory.openSession();

        try {
            transaction = session.beginTransaction();

            Utilisateur utilisateur = session.get(Utilisateur.class, id);
            session.remove(utilisateur);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }
    }

    @Override
    public Boolean login(String login, String password) {
        session = sessionFactory.openSession();
        try {
            String hql = "FROM Utilisateur u WHERE u.login = :login";
            Query<Utilisateur> query = session.createQuery(hql, Utilisateur.class);
            query.setParameter("login", login);
            Utilisateur user = query.uniqueResult();
            if (user == null) {
                return false;
            } else {
                String ps = user.getPassword();
                return BCrypt.checkpw(password, ps);
            }
        } finally {
            session.close();
        }
    }

}
