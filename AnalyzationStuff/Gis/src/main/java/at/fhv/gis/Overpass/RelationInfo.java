package at.fhv.gis.Overpass;

/**
 * Created by Johannes on 09.04.2017.
 */
public class RelationInfo {

  protected enum RelationType{
    way,
    relation,
    node
  }

    private RelationType relationType;
    private long ref;

    public RelationInfo(RelationType relationType, long ref) {
      this.relationType = relationType;
      this.ref = ref;
    }

    public RelationType getRelationType() {
      return relationType;
    }

    public void setRelationType(RelationType relationType) {
      this.relationType = relationType;
    }

    public long getRef() {
      return ref;
    }

    public void setRef(int ref) {
      this.ref = ref;
    }

}
