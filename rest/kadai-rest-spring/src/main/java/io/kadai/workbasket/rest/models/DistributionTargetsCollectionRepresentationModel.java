package io.kadai.workbasket.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.kadai.common.rest.models.CollectionRepresentationModel;
import java.beans.ConstructorProperties;
import java.util.Collection;

public class DistributionTargetsCollectionRepresentationModel
    extends CollectionRepresentationModel<WorkbasketSummaryRepresentationModel> {

  @ConstructorProperties({"distributionTargets"})
  public DistributionTargetsCollectionRepresentationModel(
      Collection<WorkbasketSummaryRepresentationModel> content) {
    super(content);
  }

  /** the embedded distribution targets. */
  @JsonProperty("distributionTargets")
  @Override
  public Collection<WorkbasketSummaryRepresentationModel> getContent() {
    return super.getContent();
  }
}
