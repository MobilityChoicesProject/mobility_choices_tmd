package at.fhv.transportClassifier.mainserver.transaction;

import at.fhv.transportClassifier.common.transaction.ITransaction;
import at.fhv.transportClassifier.common.transaction.TransactionException;
import javax.persistence.EntityTransaction;

/**
 * Created by Johannes on 20.06.2017.
 */
public class EntityManagerTransaction implements ITransaction {

  private EntityTransaction entityTransaction;

  public EntityManagerTransaction(EntityTransaction entityTransaction) {
    this.entityTransaction = entityTransaction;
  }

  @Override
  public void beginn()throws TransactionException {
    entityTransaction.begin();
  }

  @Override
  public void commit() throws TransactionException{
    entityTransaction.commit();
  }

  @Override
  public void rollback() throws TransactionException{
    entityTransaction.rollback();
  }
}
