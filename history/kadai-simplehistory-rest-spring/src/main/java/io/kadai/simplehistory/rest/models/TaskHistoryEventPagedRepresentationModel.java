package io.kadai.simplehistory.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.kadai.common.rest.models.PageMetadata;
import io.kadai.common.rest.models.PagedRepresentationModel;
import java.beans.ConstructorProperties;
import java.util.Collection;

public class TaskHistoryEventPagedRepresentationModel
    extends PagedRepresentationModel<TaskHistoryEventRepresentationModel> {

  @ConstructorProperties({"taskHistoryEvents", "page"})
  public TaskHistoryEventPagedRepresentationModel(
      Collection<TaskHistoryEventRepresentationModel> content, PageMetadata pageMetadata) {
    super(content, pageMetadata);
  }

  /** the embedded task history events. */
  @JsonProperty("taskHistoryEvents")
  @Override
  public Collection<TaskHistoryEventRepresentationModel> getContent() {
    return super.getContent();
  }
}
