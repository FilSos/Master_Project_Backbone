package model;

import java.util.List;

public class QueryDataSet {
    String surname;
    String name;
    String email;
    List<String> excercises;

    public QueryDataSet(String surname, String name, String email, List<String> excercises) {
        this.surname = surname;
        this.name = name;
        this.email = email;
        this.excercises = excercises;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getExcercises() {
        return excercises;
    }

    public void setExcercises(List<String> excercises) {
        this.excercises = excercises;
    }

    private QueryDataSet(Builder builder) {
        surname = builder.surname;
        name = builder.name;
        email = builder.email;
        excercises = builder.excercises;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(QueryDataSet copy) {
        Builder builder = new Builder();
        builder.surname = copy.getSurname();
        builder.name = copy.getName();
        builder.email = copy.getEmail();
        builder.excercises = copy.getExcercises();
        return builder;
    }


    public static final class Builder {
        private String surname;
        private String name;
        private String email;
        private List<String> excercises;

        private Builder() {
        }

        public Builder withSurname(String val) {
            surname = val;
            return this;
        }

        public Builder withName(String val) {
            name = val;
            return this;
        }

        public Builder withEmail(String val) {
            email = val;
            return this;
        }

        public Builder withExcercises(List<String> val) {
            excercises = val;
            return this;
        }

        public QueryDataSet build() {
            return new QueryDataSet(this);
        }
    }
}
