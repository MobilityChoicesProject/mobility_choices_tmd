package at.fhv.transportClassifier.common;

import java.util.function.Supplier;

/**
 * Created by Johannes on 11.08.2017.
 */
public class LazyLoggingStatement {

  private Supplier<Object> supplier;
  public LazyLoggingStatement(Supplier<Object> supplier){
    this.supplier = supplier;
  }



  public String toString(){
    return supplier.get().toString();
  }

}
