package model;

public class Weights {

    private double usedColumns;
    private double usedTables;
    private double refQueries;
    private double typos;
    private double codeFragments;

    public double getUsedColumns() {
        return usedColumns;
    }

    public void setUsedColumns(double usedColumns) {
        this.usedColumns = usedColumns;
    }

    public double getUsedTables() {
        return usedTables;
    }

    public void setUsedTables(double usedTables) {
        this.usedTables = usedTables;
    }

    public double getRefQueries() {
        return refQueries;
    }

    public void setRefQueries(double refQueries) {
        this.refQueries = refQueries;
    }

    public double getTypos() {
        return typos;
    }

    public void setTypos(double typos) {
        this.typos = typos;
    }

    public double getCodeFragments() {
        return codeFragments;
    }

    public void setCodeFragments(double codeFragments) {
        this.codeFragments = codeFragments;
    }
}
