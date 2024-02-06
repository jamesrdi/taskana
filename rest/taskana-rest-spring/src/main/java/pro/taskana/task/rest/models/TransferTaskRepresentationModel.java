package pro.taskana.task.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;

public class TransferTaskRepresentationModel {

  @JsonProperty("setTransferFlag")
  private final Boolean setTransferFlag;

  @JsonProperty("owner")
  private final String owner;

  @ConstructorProperties({"setTransferFlag", "owner"})
  public TransferTaskRepresentationModel(boolean setTransferFlag, String owner) {
    this.setTransferFlag = setTransferFlag;
    this.owner = owner;
  }

  public Boolean getSetTransferFlag() {
    if (setTransferFlag != null) {
      return setTransferFlag;
    } else {
      return true;
    }
  }

  public String getOwner() {
    return owner;
  }
}
