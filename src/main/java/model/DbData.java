package model;

public class DbData {
    private String name;
    private String driver;
    private String url;
    private String dialect;


    public DbData(String name, String driver, String url, String dialect) {
        this.name = name;
        this.driver = driver;
        this.url = url;
        this.dialect = dialect;
    }

    public DbData() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }
}
