package pro.taskana.workbasket.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import java.util.Collection;

import io.swagger.v3.oas.annotations.media.Schema;
import pro.taskana.common.rest.models.CollectionRepresentationModel;

public class WorkbasketDefinitionCollectionRepresentationModel
    extends CollectionRepresentationModel<WorkbasketDefinitionRepresentationModel> {

  @ConstructorProperties("workbasketDefinitions")
  public WorkbasketDefinitionCollectionRepresentationModel(
      Collection<WorkbasketDefinitionRepresentationModel> content) {
    super(content);
  }

  /** the embedded workbasket definitions. */
  @Schema(name = "workbasketDefinitions", description = "the embedded workbasket definitions.")
  @JsonProperty("workbasketDefinitions")
  @Override
  public Collection<WorkbasketDefinitionRepresentationModel> getContent() {
    return super.getContent();
  }
}
