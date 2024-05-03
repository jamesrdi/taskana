package io.kadai.task.rest.models;

import io.kadai.task.api.models.Attachment;
import java.util.HashMap;
import java.util.Map;

/** EntityModel class for {@link Attachment}. */
public class AttachmentRepresentationModel extends AttachmentSummaryRepresentationModel {

  /** All additional information of the Attachment. */
  private Map<String, String> customAttributes = new HashMap<>();

  public Map<String, String> getCustomAttributes() {
    return customAttributes;
  }

  public void setCustomAttributes(Map<String, String> customAttributes) {
    this.customAttributes = customAttributes;
  }
}
