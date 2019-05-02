package at.fhv.transportClassifier.common.transaction;

/**
 * Created by Johannes on 20.06.2017.
 */
public interface ITransaction {

  void beginn() throws TransactionException;

  void commit()throws TransactionException;

  void rollback()throws TransactionException;


}
