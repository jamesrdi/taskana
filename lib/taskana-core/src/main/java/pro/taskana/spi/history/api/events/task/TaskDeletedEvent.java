package pro.taskana.spi.history.api.events.task;

import java.time.Instant;
import pro.taskana.task.api.models.TaskSummary;

public class TaskDeletedEvent extends TaskHistoryEvent {

  public TaskDeletedEvent(
      String id,
      TaskSummary taskSummary,
      String oldState,
      String newState,
      String userId,
      String details) {
    super(id, taskSummary, userId, details);
    eventType = TaskHistoryEventType.DELETED.getName();
    created = Instant.now();
    this.oldValue = oldState;
    this.newValue = newState;
  }
}
