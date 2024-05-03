package io.kadai.workbasket.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.kadai.common.rest.models.PageMetadata;
import io.kadai.common.rest.models.PagedRepresentationModel;
import java.beans.ConstructorProperties;
import java.util.Collection;

public class WorkbasketSummaryPagedRepresentationModel
    extends PagedRepresentationModel<WorkbasketSummaryRepresentationModel> {

  @ConstructorProperties({"workbaskets", "page"})
  public WorkbasketSummaryPagedRepresentationModel(
      Collection<WorkbasketSummaryRepresentationModel> content, PageMetadata pageMetadata) {
    super(content, pageMetadata);
  }

  /** the embedded workbaskets. */
  @JsonProperty("workbaskets")
  @Override
  public Collection<WorkbasketSummaryRepresentationModel> getContent() {
    return super.getContent();
  }
}
