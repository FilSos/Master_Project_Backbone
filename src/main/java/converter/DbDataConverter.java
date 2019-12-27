package converter;

import javafx.util.StringConverter;
import model.DbData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DbDataConverter extends StringConverter<DbData> {

    private static Logger logger = LogManager.getLogger(DbDataConverter.class);



    // Method to convert a DbData-Object to a String
    @Override
    public String toString(DbData dbData) {
        return dbData == null ? null : dbData.getName();
    }

    // Method to convert a String to a DbData-Object
    @Override
    public DbData fromString(String string) {
        DbData dbData = null;
        String[] arrayOfWords = string.split(",");
        if (arrayOfWords.length == 4) {
            String name = arrayOfWords[0];
            String driver = arrayOfWords[1];
            String url = arrayOfWords[2];
            String dialect = arrayOfWords[3];
            dbData = new DbData(name, driver, url, dialect);
        } else {
            logger.info("Obiekt jest pusty!!!");
        }
        return dbData;
    }
}
