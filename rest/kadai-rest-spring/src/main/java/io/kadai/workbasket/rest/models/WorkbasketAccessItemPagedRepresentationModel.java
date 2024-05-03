package io.kadai.workbasket.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.kadai.common.rest.models.PageMetadata;
import io.kadai.common.rest.models.PagedRepresentationModel;
import java.beans.ConstructorProperties;
import java.util.Collection;

public class WorkbasketAccessItemPagedRepresentationModel
    extends PagedRepresentationModel<WorkbasketAccessItemRepresentationModel> {

  @ConstructorProperties({"accessItems", "page"})
  public WorkbasketAccessItemPagedRepresentationModel(
      Collection<WorkbasketAccessItemRepresentationModel> content, PageMetadata pageMetadata) {
    super(content, pageMetadata);
  }

  /** the embedded access items. */
  @JsonProperty("accessItems")
  @Override
  public Collection<WorkbasketAccessItemRepresentationModel> getContent() {
    return super.getContent();
  }
}
