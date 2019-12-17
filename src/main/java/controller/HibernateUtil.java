package controller;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static void modifyConfiguration(Configuration cfg) {
        try {
            // Session factory creation using the hibernate.cfg.xml file
            if (cfg != null) {
                sessionFactory = cfg.buildSessionFactory();
            } else {
                sessionFactory = new Configuration().configure("Hibernate.cfg.xml").buildSessionFactory();
            }
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    // public static final ThreadLocal session = new ThreadLocal();

    public static SessionFactory getSessionFactory() {
        modifyConfiguration(null);
        return sessionFactory;
    }

    private HibernateUtil() {
    }

}
