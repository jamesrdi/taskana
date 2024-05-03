package io.kadai.task.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.kadai.common.rest.models.PageMetadata;
import io.kadai.common.rest.models.PagedRepresentationModel;
import java.beans.ConstructorProperties;
import java.util.Collection;

public class TaskSummaryPagedRepresentationModel
    extends PagedRepresentationModel<TaskSummaryRepresentationModel> {

  @ConstructorProperties({"tasks", "page"})
  public TaskSummaryPagedRepresentationModel(
      Collection<TaskSummaryRepresentationModel> content, PageMetadata pageMetadata) {
    super(content, pageMetadata);
  }

  /** The embedded tasks. */
  @JsonProperty("tasks")
  @Override
  public Collection<TaskSummaryRepresentationModel> getContent() {
    return super.getContent();
  }
}
