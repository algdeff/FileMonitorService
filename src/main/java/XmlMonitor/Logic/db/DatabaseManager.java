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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public final class DatabaseManager implements IListener{

    private static final String CONFIG_FILE_LOCATION = "/hibernate.cfg.xml";

    private static final ThreadLocal<Session> THREAD_LOCAL = new ThreadLocal<>();

    private static Configuration configuration = new Configuration();
    private static SessionFactory sessionFactory;

    private static final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();

    static {
        try {
//            configuration.configure(CONFIG_FILE_LOCATION);
//            sessionFactory = configuration.buildSessionFactory();

            MetadataSources metadataSources = new MetadataSources(registry);
            sessionFactory = metadataSources.buildMetadata().buildSessionFactory();

        } catch (Throwable ex) {
            System.err.println("Error SessionFactory initialization");
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
        registerOnPublisher();
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

//        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
//                .configure()
//                .build();
//        try {
//            MetadataSources metadataSources = new MetadataSources(registry);
//            sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
////metadataSources.addAnnotatedClass(ru.easyjava.data.hibernate.entity.Address.class);
////metadataSources.addAnnotatedClassName("ru.easyjava.data.hibernate.entity.Address.class");
////metadataSources.addResource("classpath:/Address.hbm.xml");
//        } catch (Exception e) {
//            StandardServiceRegistryBuilder.destroy(registry);
//            throw e;
//        }




        _init = true;
    }

    public void saveEntity(Object entity) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();

        session.saveOrUpdate(entity);
        //session.merge(entity);

//        Metamodel mm = session.getMetamodel();
//
//        EntityType dd = mm.entity(entity.getClass());
//        dd.

        //session.flush();
        transaction.commit(); //getCurrent session -> session close
        session.close();
    }

    public List executeQuery(String hqlQuery){
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        //Query queryq = session.createQuery();
        Query query = session.createQuery(hqlQuery);
        query.executeUpdate();
        List result = query.getResultList(); //query.list();
        transaction.commit();
        session.close();

        return result; //query.getResultList();
    }


    public int tableTruncate(String tableName){
        String hql = String.format("delete from %s",tableName);
        executeQuery(hql);
        return 1;
    }

    public void teste() {
        Session session = getSession();
        Transaction tx = session.beginTransaction();
        List messages = session.createQuery("from xml_files_entries").list();
        tx.commit();
        session.close();

        for (Object message : messages) {
            System.out.println(message.toString());
        }

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

    public ArrayList test2() {
        final Session session = getSession();
        ArrayList<Object> result = new ArrayList<>();
        try {
            System.out.println("querying all the managed entities...");
            final Metamodel metamodel = session.getSessionFactory().getMetamodel();
            for (EntityType<?> entityType : metamodel.getEntities()) {
                final String entityName = entityType.getName();
                final Query query = session.createQuery("from " + entityName);
                System.out.println("executing: " + query.getQueryString());
                for (Object o : query.list()) {
                    System.out.println("  " + o);
                    //result = o.toString();
                    result.add(o);
                }

                //EntityManager users = session.getEntityManagerFactory().createEntityManager();
                //users.
                //session.

            }


        } finally {
            session.close();
        }
        return result;
    }

    public void registerOnPublisher() {
        Publisher.getInstance().registerNewListener(this, GROUP_NAME);
    }
    public String[] listenerInterests() {
        return new String[] {
                "message",
                "update",
                "trace"
        };
    }
    public void listenerHandler(IPublisherEvent publisherEvent) {
        if (publisherEvent.getType().equals(Publisher.EVENT_TYPE_GROUP)) {
            System.out.println(GROUP_NAME + " - group Event " + publisherEvent.getBody().toString());
            return;
        }

        switch (publisherEvent.getName()) {
            case "message": {

                System.out.println("message " + publisherEvent.getBody().toString());
            }
            case "trace": {
                System.out.println("trace " + publisherEvent.getBody().toString());
            }

        }
        System.out.println("UI.Event: " + publisherEvent.getName() + publisherEvent.getBody().toString());
    }


}
