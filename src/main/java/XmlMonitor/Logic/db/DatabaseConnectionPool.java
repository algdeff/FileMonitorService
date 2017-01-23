package XmlMonitor.Logic.db;

import org.hibernate.SessionFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DatabaseConnectionPool {
    private static final SessionFactory sessionFactory;

    static {
        try {
            // Create the SessionFactory
            Configuration configuration = new Configuration();
            configuration.configure();
            configuration.getProperties().list(System.out);
            sessionFactory = configuration.buildSessionFactory();
        } catch (HibernateException ex) {
            throw new RuntimeException("Configuration problem: " + ex.getMessage(), ex);
        }
    }

    public static final ThreadLocal<Session> SESSION = new ThreadLocal<>();

    public static Session currentSession() throws HibernateException {
        Session s = SESSION.get();
        // Open a new Session, if this Thread has none yet
        if (s == null) {
            s = sessionFactory.openSession();
            SESSION.set(s);
        }
        return s;
    }

    public static void closeSession() throws HibernateException {
        Session s = SESSION.get();
        SESSION.set(null);
        if (s != null)
            s.close();
    }

//    public void example() {
//        PositionsEntity position = new PositionsEntity();
//        position.setId(2);
//        position.setPositionName("it director");
//
//        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("office");
//        EntityManager em = entityManagerFactory.createEntityManager();
//        em.getTransaction().begin();
//        em.persist(position);
//        em.getTransaction().commit();
//        em.close();
//    }

}