package sample.modul;

import javafx.scene.Node;

/**
 * Created by Johannes on 19.04.2017.
 */
public interface Modul {

  void init(ModulContext context);

  void trackingChanged();

  void  activate();

  void  deActivate();

  Node getNode();

  String getName();

}
