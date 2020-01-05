package controller;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import static view.Start.mainController;

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
            mainController.startProgramStatus.setText("Wystąpił błąd w programie." + "\nSprawdź login i hasło oraz dostęp do bazy danych.");
            throw new ExceptionInInitializerError(ex);
        }
    }

    // public static final ThreadLocal session = new ThreadLocal();

    public static SessionFactory getSessionFactory() {
        if (sessionFactory != null) {
            return sessionFactory;
        } else {
            modifyConfiguration(null);
            return sessionFactory;
        }
    }

    private HibernateUtil() {
    }

}
