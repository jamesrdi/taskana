package io.kadai.workbasket.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.kadai.common.rest.models.CollectionRepresentationModel;
import java.beans.ConstructorProperties;
import java.util.Collection;

public class WorkbasketAccessItemCollectionRepresentationModel
    extends CollectionRepresentationModel<WorkbasketAccessItemRepresentationModel> {

  @ConstructorProperties("accessItems")
  public WorkbasketAccessItemCollectionRepresentationModel(
      Collection<WorkbasketAccessItemRepresentationModel> content) {
    super(content);
  }

  /** the embedded access items. */
  @JsonProperty("accessItems")
  @Override
  public Collection<WorkbasketAccessItemRepresentationModel> getContent() {
    return super.getContent();
  }
}
