package io.kadai.classification.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.kadai.common.rest.models.PageMetadata;
import io.kadai.common.rest.models.PagedRepresentationModel;
import jakarta.validation.constraints.NotNull;
import java.beans.ConstructorProperties;
import java.util.Collection;

public class ClassificationSummaryPagedRepresentationModel
    extends PagedRepresentationModel<ClassificationSummaryRepresentationModel> {

  @ConstructorProperties({"classifications", "page"})
  public ClassificationSummaryPagedRepresentationModel(
      Collection<ClassificationSummaryRepresentationModel> content, PageMetadata pageMetadata) {
    super(content, pageMetadata);
  }

  /** the embedded classifications. */
  @Override
  @JsonProperty("classifications")
  public @NotNull Collection<ClassificationSummaryRepresentationModel> getContent() {
    return super.getContent();
  }
}
