package io.kadai.task.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.kadai.common.rest.models.CollectionRepresentationModel;
import java.beans.ConstructorProperties;
import java.util.Collection;

public class TaskCommentCollectionRepresentationModel
    extends CollectionRepresentationModel<TaskCommentRepresentationModel> {

  @ConstructorProperties("taskComments")
  public TaskCommentCollectionRepresentationModel(
      Collection<TaskCommentRepresentationModel> content) {
    super(content);
  }

  /** The embedded task comments. */
  @JsonProperty("taskComments")
  @Override
  public Collection<TaskCommentRepresentationModel> getContent() {
    return super.getContent();
  }
}
