package visitors;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.HashSet;
import java.util.Set;

public class ColumnNamesFinder extends TablesNamesFinder {

    Set<String> tableColumns = new HashSet<>();

    @Override
    public void visit(Column tableColumn) {
        tableColumns.add(tableColumn.getFullyQualifiedName());
    }

    public Set<String> getTableColumns(Statement statement) {
        tableColumns = new HashSet<>();
        statement.accept(this);
        return tableColumns;
    }

    public Set<String> getTableColumns(Delete delete) {
        tableColumns = new HashSet<>();
        delete.getTable().accept(this);
        delete.getWhere().accept(this);
        return tableColumns;
    }

    public Set<String> getTableColumns(Insert insert) {
        tableColumns = new HashSet<>();
        insert.getTable().accept(this);
        if (insert.getItemsList() != null) {
            insert.getItemsList().accept(this);
        }
        return tableColumns;
    }

    public Set<String> getTableColumns(Replace replace) {
        tableColumns = new HashSet<>();
        if (replace.getExpressions() != null) {
            for (Expression expression : replace.getExpressions()) {
                expression.accept(this);
            }
        }
        if (replace.getItemsList() != null) {
            replace.getItemsList().accept(this);
        }

        return tableColumns;
    }

    public Set<String> getTableColumns(Select select) {
        tableColumns = new HashSet<>();
        if (select.getWithItemsList() != null) {
            for (WithItem withItem : select.getWithItemsList()) {
                withItem.accept(this);
            }
        }
        select.getSelectBody().accept(this);

        return tableColumns;
    }

    public Set<String> getTableColumns(Update update) {
        tableColumns = new HashSet<>();
        update.getTable().accept(this);
        if (update.getExpressions() != null) {
            for (Expression expression : update.getExpressions()) {
                expression.accept(this);
            }
        }

        if (update.getFromItem() != null) {
            update.getFromItem().accept(this);
        }

        if (update.getJoins() != null) {
            for (Join join : update.getJoins()) {
                join.getRightItem().accept(this);
            }
        }

        if (update.getWhere() != null) {
            update.getWhere().accept(this);
        }

        return tableColumns;
    }

    @Override
    public void visit(PlainSelect plainSelect) {
        if (plainSelect.getSelectItems() != null) {
            for (SelectItem item : plainSelect.getSelectItems()) {
                item.accept(this);
            }
        }

        if (plainSelect.getFromItem() != null) {
            plainSelect.getFromItem().accept(this);
        }

        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                if (join.getOnExpression() != null) {
                    join.getOnExpression().accept(this);
                }
                if (join.getUsingColumns() != null) {
                    join.getUsingColumns().forEach(column -> column.accept(this));
                }
            }
        }
        if (plainSelect.getWhere() != null) {
            plainSelect.getWhere().accept(this);
        }

        if (plainSelect.getHaving() != null) {
            plainSelect.getHaving().accept(this);
        }

        if (plainSelect.getOracleHierarchical() != null) {
            plainSelect.getOracleHierarchical().accept(this);
        }
    }

    @Override
    public void visit(Table tableName) {
    }
}
