package model;

public class Base {

    private String name;
    private String driver;
    private String url;
    private String dialect;
    private String username;
    private String password;
    private String queryString;


    public Base(String name, String driver, String url, String dialect, String username, String password, String queryString) {
        this.name = name;
        this.driver = driver;
        this.url = url;
        this.dialect = dialect;
        this.username = username;
        this.password = password;
        this.queryString = queryString;
    }

    public Base() {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }
}
