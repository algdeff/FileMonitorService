package XmlMonitor.Logic.db;

import XmlMonitor.Publisher.Interfaces.IListener;
import XmlMonitor.Publisher.Interfaces.IPublisherEvent;
import XmlMonitor.Publisher.Publisher;
import org.hibernate.*;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import java.util.ArrayList;
import java.util.List;


public final class DatabaseManager implements IListener{

//    private static final String CONFIG_FILE_LOCATION = "/hibernate.cfg.xml";
//    private static Configuration configuration = new Configuration();

    private static final ThreadLocal<Session> THREAD_LOCAL = new ThreadLocal<>();
    private static SessionFactory sessionFactory;

    private static final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();

    static {
        try {
//            configuration.configure(CONFIG_FILE_LOCATION);
//            sessionFactory = configuration.buildSessionFactory();
            MetadataSources metadataSources = new MetadataSources(registry);
            sessionFactory = metadataSources.buildMetadata().buildSessionFactory();

        } catch (Throwable ex) {
            System.err.println("Error DataBase initialization");
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static final String GROUP_NAME = "DB";
    private static boolean _init = false;

    private static class SingletonInstance {
        private static final DatabaseManager INSTANCE = new DatabaseManager();
    }

    private DatabaseManager() {
    }

    public static DatabaseManager getInstance() {
        return SingletonInstance.INSTANCE;
    }

    private static void rebuildSessionFactory() {
        try {
//            configuration.configure(CONFIG_FILE_LOCATION);
//            sessionFactory = configuration.buildSessionFactory();

            MetadataSources metadataSources = new MetadataSources(registry);
            sessionFactory = metadataSources.buildMetadata().buildSessionFactory();

        } catch (Exception e) {
            System.err.println("Error Creating SessionFactory");
            e.printStackTrace();
        }
    }

    public static Session getSession() throws HibernateException {
        Session session = THREAD_LOCAL.get();

        if (session == null || !session.isOpen()) {
            if (sessionFactory == null) rebuildSessionFactory();
            session = sessionFactory.openSession();
            THREAD_LOCAL.set(session);
        }

        return session;
    }

    public static void closeSession() throws HibernateException {
        Session session = THREAD_LOCAL.get();
        THREAD_LOCAL.set(null);

        if (session != null) {
            session.close();
        }
    }

    public void init() {
        if (_init) return;

        registerOnPublisher();

        _init = true;
    }

    public void saveEntity(Object entity) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        session.saveOrUpdate(entity);
        transaction.commit();
        session.close();
    }

    public List executeQuery(String hqlQuery){
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        Query query = session.createQuery(hqlQuery);
        query.executeUpdate();
        List result = query.getResultList(); //query.list();
        transaction.commit();
        session.close();
        return result;
    }

    public void registerOnPublisher() {
        Publisher.getInstance().registerNewListener(this, GROUP_NAME);
    }
    public String[] listenerInterests() {
        return new String[] {
                "message",
                "update"
        };
    }
    public void listenerHandler(IPublisherEvent publisherEvent) {
        if (publisherEvent.getType().equals(Publisher.EVENT_TYPE_GROUP)) {

//            return;
        }

//        switch (publisherEvent.getName()) {
//            case "message": {

//            }
//            case "update": {

//            }
//        }
//        System.out.println("UI.Event: " + publisherEvent.getName() + publisherEvent.getBody().toString());
    }

}
