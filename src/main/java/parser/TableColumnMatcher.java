package parser;

import model.ParsingParameters;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.TablesNamesFinder;
import query.QueryData;
import visitors.ColumnNamesFinder;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TableColumnMatcher {

    private ParsingParameters parsingParameters;

    public TableColumnMatcher(ParsingParameters parsingParameters) {
        this.parsingParameters = parsingParameters;
    }

    public double getTableMatches(QueryData query, Statement statement) {
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        Set<String> tableList = new HashSet<>(tablesNamesFinder.getTableList(statement));
        tableList = normalizeNames(tableList);
        String[] split = parsingParameters.getUsedTables().get(query.getExNumber()).split(",");
        int matches = 0;
        for (String table : split) {
            if (tableList.contains(table.toLowerCase().trim())) {
                matches++;
            }
        }
        return (double) matches / split.length;
    }

    public double getColumnsMatches(QueryData query, Statement statement) {
        ColumnNamesFinder columnNamesFinder = new ColumnNamesFinder();
        Set<String> tableColumns = columnNamesFinder.getTableColumns(statement);
        tableColumns = normalizeNames(tableColumns);
        String[] split = parsingParameters.getUsedColumns().get(query.getExNumber()).split(",");
        int matches = 0;
        for (String column : split) {
            if (tableColumns.contains(column.trim().toLowerCase())) {
                matches++;
            }
        }
        return (double) matches / split.length;
    }

    private Set<String> normalizeNames(Set<String> strings) {
        return strings.stream()
                .map(this::deletePrefix)
                .collect(Collectors.toSet());
    }

    private String deletePrefix(String name) {
        if (name.contains(".")) {
            return name.split("\\.")[1];
        }
        return name;
    }

}
