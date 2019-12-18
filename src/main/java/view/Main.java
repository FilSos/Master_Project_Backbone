package view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    protected static final String CURRENT_VERSION = "0.91";
    protected static final String LAST_CHANGE_DATE = "17.12.2019 21:51";
    private static Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("STARTING APPLICATION Version: " + CURRENT_VERSION);
        logger.info("LAST CHANGE MADE: " + LAST_CHANGE_DATE);
        Start.main(args);
    }
}
