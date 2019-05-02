package experiments;

import helper.OverlappingResult;

/**
 * Created by Johannes on 31.07.2017.
 */
public class OverlappingResults {

  private OverlappingResult randomForestOverlappingResult;
  private OverlappingResult droolsOverlappingResult;
  private OverlappingResult randomForestPostProcessOverlappingResult;
  private OverlappingResult droolsPostProcessOverlappingResult;
  private long id;

  public OverlappingResults(OverlappingResult randomForestOverlappingResult,
      OverlappingResult droolsOverlappingResult,
      OverlappingResult randomForestPostProcessOverlappingResult,
      OverlappingResult droolsPostProcessOverlappingResult, long id) {
    this.randomForestOverlappingResult = randomForestOverlappingResult;
    this.droolsOverlappingResult = droolsOverlappingResult;
    this.randomForestPostProcessOverlappingResult = randomForestPostProcessOverlappingResult;
    this.droolsPostProcessOverlappingResult = droolsPostProcessOverlappingResult;
    this.id = id;
  }

  public OverlappingResult getRandomForestOverlappingResult() {
    return randomForestOverlappingResult;
  }

  public OverlappingResult getDroolsOverlappingResult() {
    return droolsOverlappingResult;
  }

  public OverlappingResult getRandomForestPostProcessOverlappingResult() {
    return randomForestPostProcessOverlappingResult;
  }

  public OverlappingResult getDroolsPostProcessOverlappingResult() {
    return droolsPostProcessOverlappingResult;
  }

  public long getId() {
    return id;
  }
}
