package io.kadai.user.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.kadai.common.rest.models.CollectionRepresentationModel;
import java.beans.ConstructorProperties;
import java.util.Collection;

public class UserCollectionRepresentationModel
    extends CollectionRepresentationModel<UserRepresentationModel> {
  @ConstructorProperties("users")
  public UserCollectionRepresentationModel(Collection<UserRepresentationModel> content) {
    super(content);
  }

  /** The embedded users. */
  @JsonProperty("users")
  @Override
  public Collection<UserRepresentationModel> getContent() {
    return super.getContent();
  }
}
