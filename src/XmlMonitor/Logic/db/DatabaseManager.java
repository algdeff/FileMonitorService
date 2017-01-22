package XmlMonitor.Logic.db;

import XmlMonitor.Publisher.Interfaces.IListener;
import XmlMonitor.Publisher.Interfaces.IPublisherEvent;
import XmlMonitor.Publisher.Publisher;
import org.hibernate.HibernateException;
import org.hibernate.Metamodel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import javax.persistence.metamodel.EntityType;
import java.util.ArrayList;


public final class DatabaseManager implements IListener{

    private static final SessionFactory ourSessionFactory;

    public static final String GROUP_NAME = "DB";

    private static class SingletonInstance {
        private static final DatabaseManager INSTANCE = new DatabaseManager();
    }

    private DatabaseManager() {
        registerOnPublisher();
    }

    public static DatabaseManager getInstance() {
        return SingletonInstance.INSTANCE;
    }
    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();

            ourSessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
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

    public ArrayList getUsers() throws Exception {
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
