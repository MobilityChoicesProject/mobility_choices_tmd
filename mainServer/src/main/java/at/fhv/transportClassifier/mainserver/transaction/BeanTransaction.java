package at.fhv.transportClassifier.mainserver.transaction;

import at.fhv.transportClassifier.common.transaction.ITransaction;
import at.fhv.transportClassifier.common.transaction.TransactionException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 * Created by Johannes on 20.06.2017.
 */
public class BeanTransaction implements ITransaction {

  private UserTransaction userTransaction;


  public BeanTransaction(UserTransaction userTransaction) {
    this.userTransaction = userTransaction;
  }

  @Override
  public void beginn() throws TransactionException {
    try {
      userTransaction.begin();
    } catch (NotSupportedException e) {
      throw new TransactionException(e);
    } catch (SystemException e) {
      throw new TransactionException(e);
    }
  }

  @Override
  public void commit() throws TransactionException {
    try {
      userTransaction.commit();
    } catch (HeuristicMixedException e) {
      throw new TransactionException(e);
    } catch (HeuristicRollbackException e) {
      throw new TransactionException(e);
    } catch (RollbackException e) {
      throw new TransactionException(e);
    } catch (SystemException e) {
      throw new TransactionException(e);
    }
  }

  @Override
  public void rollback() throws TransactionException {
    try {
      userTransaction.commit();
    } catch (HeuristicMixedException e) {
      throw new TransactionException(e);
    } catch (HeuristicRollbackException e) {
      throw new TransactionException(e);
    } catch (RollbackException e) {
      throw new TransactionException(e);
    } catch (SystemException e) {
      throw new TransactionException(e);
    }

  }
}
