package acceptance.events.task;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.List;
import org.apache.ibatis.session.SqlSessionManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.impl.TaskHistoryQueryImpl;
import pro.taskana.simplehistory.impl.TaskanaHistoryEngineImpl;
import pro.taskana.simplehistory.impl.task.TaskHistoryQueryMapper;
import pro.taskana.spi.history.api.TaskanaHistory;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.task.TaskHistoryEventType;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.Task;
import pro.taskana.testapi.DefaultTestEntities;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.WithServiceProvider;
import pro.taskana.testapi.builder.TaskBuilder;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

@WithServiceProvider(
    serviceProviderInterface = TaskanaHistory.class,
    serviceProviders = SimpleHistoryServiceImpl.class)
@TaskanaIntegrationTest
@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnTaskDeletionAccTest {
  @TaskanaInject TaskanaEngine taskanaEngine;
  @TaskanaInject TaskService taskService;
  @TaskanaInject WorkbasketService workbasketService;
  @TaskanaInject ClassificationService classificationService;
  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;
  Task task1;
  Task task2;
  Task task3;
  private SimpleHistoryServiceImpl historyService = new SimpleHistoryServiceImpl();
  private TaskanaHistoryEngineImpl taskanaHistoryEngine;

  @WithAccessId(user = "admin")
  @BeforeAll
  void setUp() throws Exception {
    taskanaHistoryEngine = TaskanaHistoryEngineImpl.createTaskanaEngine(taskanaEngine);
    historyService.initialize(taskanaEngine);

    defaultClassificationSummary =
        DefaultTestEntities.defaultTestClassification()
            .buildAndStoreAsSummary(classificationService);
    defaultWorkbasketSummary =
        DefaultTestEntities.defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);

    task1 = createTask().buildAndStore(taskService);
    task2 = createTask().state(TaskState.COMPLETED).buildAndStore(taskService);
    task3 = createTask().state(TaskState.COMPLETED).buildAndStore(taskService);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_CreateDeleteHistoryEvent_When_TaskIsDeleted() throws Exception {
    historyService.deleteHistoryEventsByTaskIds(List.of(task1.getId()));
    TaskHistoryQueryMapper taskHistoryQueryMapper = getHistoryQueryMapper();
    List<TaskHistoryEvent> events =
        taskHistoryQueryMapper.queryHistoryEvents(
            (TaskHistoryQueryImpl) historyService.createTaskHistoryQuery().taskIdIn(task1.getId()));
    historyService.createTaskHistoryQuery().taskIdIn(task1.getId());
    assertThat(events).isEmpty();

    taskService.forceDeleteTask(task1.getId());

    events =
        taskHistoryQueryMapper.queryHistoryEvents(
            (TaskHistoryQueryImpl) historyService.createTaskHistoryQuery().taskIdIn(task1.getId()));
    assertThat(events).hasSize(1);
    assertDeleteHistoryEvent(events.get(0).getId(), TaskState.READY.toString(), "DELETED", "admin");
  }

  @WithAccessId(user = "admin")
  @Test
  void should_CreateDeleteHistoryEvents_When_MultipleTasksAreDeleted() throws Exception {
    List<String> taskIds = List.of(task2.getId(), task3.getId());
    historyService.deleteHistoryEventsByTaskIds(taskIds);
    TaskHistoryQueryMapper taskHistoryQueryMapper = getHistoryQueryMapper();
    List<TaskHistoryEvent> events =
        taskHistoryQueryMapper.queryHistoryEvents(
            (TaskHistoryQueryImpl)
                historyService.createTaskHistoryQuery().taskIdIn(taskIds.toArray(new String[0])));
    assertThat(events).isEmpty();

    taskService.deleteTasks(taskIds);

    events =
        taskHistoryQueryMapper.queryHistoryEvents(
            (TaskHistoryQueryImpl)
                historyService.createTaskHistoryQuery().taskIdIn(taskIds.toArray(new String[0])));
    assertThat(events)
        .extracting(TaskHistoryEvent::getTaskId)
        .containsExactlyInAnyOrderElementsOf(taskIds);
    for (TaskHistoryEvent event : events) {
      assertDeleteHistoryEvent(event.getId(), TaskState.COMPLETED.toString(), "DELETED", "admin");
    }
  }

  TaskHistoryQueryMapper getHistoryQueryMapper()
      throws NoSuchFieldException, IllegalAccessException {
    Field sessionManagerField = TaskanaHistoryEngineImpl.class.getDeclaredField("sessionManager");
    sessionManagerField.setAccessible(true);
    SqlSessionManager sqlSessionManager =
        (SqlSessionManager) sessionManagerField.get(taskanaHistoryEngine);

    return sqlSessionManager.getMapper(TaskHistoryQueryMapper.class);
  }

  private void assertDeleteHistoryEvent(
      String eventId, String expectedOldValue, String expectedNewValue, String expectedUser)
      throws Exception {
    TaskHistoryEvent event = historyService.getTaskHistoryEvent(eventId);
    assertThat(event.getDetails()).isNotNull();
    JSONArray changes = new JSONObject(event.getDetails()).getJSONArray("changes");
    assertThat(changes.length()).isPositive();
    boolean foundField = false;
    for (int i = 0; i < changes.length() && !foundField; i++) {
      JSONObject change = changes.getJSONObject(i);
      if (change.get("fieldName").equals("state")) {
        foundField = true;
        String oldState = change.get("oldValue").toString();
        String newState = change.get("newValue").toString();
        assertThat(oldState).isEqualTo(expectedOldValue);
        assertThat(newState).isEqualTo(expectedNewValue);
      }
    }
    assertThat(event.getOldValue()).isEqualTo(expectedOldValue);
    assertThat(event.getNewValue()).isEqualTo(expectedNewValue);
    assertThat(event.getUserId()).isEqualTo(expectedUser);
    assertThat(event.getEventType()).isEqualTo(TaskHistoryEventType.DELETED.getName());
  }

  private TaskBuilder createTask() {
    return TaskBuilder.newTask()
        .classificationSummary(defaultClassificationSummary)
        .workbasketSummary(defaultWorkbasketSummary)
        .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build());
  }
}
