package at.fhv.transportClassifier.common.transaction;

/**
 * Created by Johannes on 20.06.2017.
 */
public class NoTransaction implements ITransaction {




  @Override
  public void beginn() throws TransactionException{

  }

  @Override
  public void commit() throws TransactionException {

  }

  @Override
  public void rollback() throws TransactionException {

  }
}
