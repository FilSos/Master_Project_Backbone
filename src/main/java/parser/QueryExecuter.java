package parser;

import com.google.gson.Gson;
import controller.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import query.QueryData;

import java.util.List;

public class QueryExecuter {

    public QueryData execute(QueryData queryData) {
        Session session = HibernateUtil.getSessionFactory()
                .getCurrentSession();
        Transaction transaction = session.beginTransaction();
        NativeQuery sqlQuery = session.createSQLQuery(queryData.getQueryString());
        List queryReturns = sqlQuery.list();
        transaction.commit();

        return QueryData.newBuilder(queryData)
                .withResult(queryReturns)
                .build();
    }

    public boolean compareResults(List l1, List l2) {
        Gson gson = new Gson();
        String result1 = gson.toJson(l1);
        String result2 = gson.toJson(l2);
        return result1.equals(result2);
    }
}
