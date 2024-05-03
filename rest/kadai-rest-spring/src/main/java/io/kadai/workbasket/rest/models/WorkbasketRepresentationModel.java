package io.kadai.workbasket.rest.models;

import io.kadai.workbasket.api.models.Workbasket;
import java.time.Instant;

/** EntityModel class for {@link Workbasket}. */
public class WorkbasketRepresentationModel extends WorkbasketSummaryRepresentationModel {

  /**
   * The creation timestamp of the workbasket in the system.
   *
   * <p>The format is ISO-8601.
   */
  private Instant created;
  /**
   * The timestamp of the last modification.
   *
   * <p>The format is ISO-8601.
   */
  private Instant modified;

  public Instant getCreated() {
    return created;
  }

  public void setCreated(Instant created) {
    this.created = created;
  }

  public Instant getModified() {
    return modified;
  }

  public void setModified(Instant modified) {
    this.modified = modified;
  }
}
