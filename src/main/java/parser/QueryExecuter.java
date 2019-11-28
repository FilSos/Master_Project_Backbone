package parser;

import com.google.gson.Gson;
import controller.HibernateUtil;
import net.sf.jsqlparser.statement.select.Select;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import query.QueryData;

import java.util.ArrayList;
import java.util.List;

public class QueryExecuter {

    public QueryData execute(QueryData queryData) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        List queryReturns = new ArrayList();
        try {
            NativeQuery sqlQuery = session.createSQLQuery(queryData.getQueryString());
            if (queryData.getStatement() instanceof Select) {
                queryReturns = sqlQuery.list();
            } else {
                sqlQuery.executeUpdate();
            }
            transaction.rollback();

            return QueryData.newBuilder(queryData)
                    .withResult(queryReturns)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback();
            return QueryData.newBuilder(queryData)
                    .withIsValid(false)
                    .withErrorReason("Error while executing on DB: " + e.getCause().getCause().getMessage())
                    .withResultMatchScore(0)
                    .build();
        }
    }

    public boolean compareResults(List l1, List l2) {
        Gson gson = new Gson();
        String result1 = gson.toJson(l1);
        String result2 = gson.toJson(l2);
        return result1.equals(result2);
    }
}
