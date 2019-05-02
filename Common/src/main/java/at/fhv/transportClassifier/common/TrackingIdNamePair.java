package at.fhv.transportClassifier.common;

/**
 * Created by Johannes on 26.06.2017.
 */
public class TrackingIdNamePair {

  private long id;
  private String name;

  public TrackingIdNamePair(long id, String name) {
    this.id = id;
    this.name = name;
  }

  public TrackingIdNamePair() {
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    TrackingIdNamePair that = (TrackingIdNamePair) o;

    if (getId() != that.getId()) {
      return false;
    }
    return getName().equals(that.getName());
  }

  @Override
  public int hashCode() {
    int result = (int) (getId() ^ (getId() >>> 32));
    result = 31 * result + getName().hashCode();
    return result;
  }
}
