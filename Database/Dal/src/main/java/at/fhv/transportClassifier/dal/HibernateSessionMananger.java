package at.fhv.transportClassifier.dal;

import at.fhv.transportClassifier.dal.interfaces.SessionManager;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Created by Johannes on 13.02.2017.
 */
public class HibernateSessionMananger implements SessionManager {

    private Session session;
    private Transaction transaction;

    public HibernateSessionMananger(Session session) {
        this.session = session;
    }

    public boolean isTransactionOpen(){
        return transaction != null;
    }

    public Session getSession(){
        return session;
    }


    public Transaction getTransaction() {
        return transaction;
    }

    protected void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void startLongTransaction() {
        if(getTransaction() == null) {
            setTransaction(session.beginTransaction());
        }
    }

    @Override
    public void commitLongTransaction() {
        if(getTransaction() == null) {

        }else{
            getTransaction().commit();
            session.clear();
            setTransaction(null);
        }
    }

    @Override
    public void rollbackLongTransaction() {
        getTransaction().rollback();
        session.clear();
        setTransaction(null);
    }
}
