package acceptance.task.lock;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestClassification;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestObjectReference;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestWorkbasket;

import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.builder.TaskBuilder;
import pro.taskana.testapi.builder.UserBuilder;
import pro.taskana.testapi.builder.WorkbasketAccessItemBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.user.api.UserService;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

@TaskanaIntegrationTest
class LockTaskAccTest {
  @TaskanaInject TaskService taskService;
  @TaskanaInject ClassificationService classificationService;
  @TaskanaInject WorkbasketService workbasketService;
  @TaskanaInject UserService userService;

  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;
  ObjectReference defaultObjectReference;

  @WithAccessId(user = "admin")
  @BeforeAll
  void setup() throws Exception {
    defaultClassificationSummary =
        defaultTestClassification().buildAndStoreAsSummary(classificationService);
    defaultWorkbasketSummary = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary.getId())
        .accessId("user-1-2")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.EDITTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    defaultObjectReference = defaultTestObjectReference().build();

    UserBuilder.newUser()
        .id("user-1-2")
        .firstName("Max")
        .lastName("Mustermann")
        .longName("Long name of user-1-2")
        .buildAndStore(userService);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_SelectAndLockTasks_When_TasksUnlocked() throws Exception {
    Task task1 =
        createDefaultTaskWithLock(false).buildAndStore(taskService);

    Task task2 =
        createDefaultTaskWithLock(false).buildAndStore(taskService);

    List<String> taskIdsToLock = List.of(task1.getId(), task2.getId());

    List<String> lockedTaskIds = taskService.selectAndLock(taskIdsToLock);

    assertThat(lockedTaskIds).containsExactlyInAnyOrder(task1.getId(),task2.getId());
    List<TaskSummary> lockedTasks =
        taskService
            .createTaskQuery()
            .idIn(taskIdsToLock.toArray(new String[0]))
            .list();
    for (TaskSummary taskSummary : lockedTasks) {
      assertThat(taskSummary.isLocked()).isTrue();
    }
  }

  @WithAccessId(user = "admin")
  @Test
  void should_OnlySelectAndLockUnlockedTasks_When_SomeTasksLocked() throws Exception {
    Task task1 =
        createDefaultTaskWithLock(false).buildAndStore(taskService);
    Task task2 =
        createDefaultTaskWithLock(true).buildAndStore(taskService);

    List<String> taskIds = List.of(task1.getId(), task2.getId());
    List<String> lockedTaskIds = taskService.selectAndLock(taskIds);

    assertThat(lockedTaskIds).containsExactlyInAnyOrder(task1.getId());
    List<TaskSummary> lockedTasks =
        taskService
            .createTaskQuery()
            .idIn(lockedTaskIds.toArray(new String[0]))
            .list();
    for (TaskSummary taskSummary : lockedTasks) {
      assertThat(taskSummary.isLocked()).isTrue();
    }
  }

  @WithAccessId(user = "admin")
  @Test
  void should_SelectAndUnlockTasks_When_TasksLocked() throws Exception {
    Task task1 =
        createDefaultTaskWithLock(true).buildAndStore(taskService);
    Task task2 =
        createDefaultTaskWithLock(true).buildAndStore(taskService);

    List<String> taskIdsToUnlock = List.of(task1.getId(), task2.getId());
    List<String> unlockedTaskIds = taskService.selectAndUnlock(taskIdsToUnlock);

    assertThat(unlockedTaskIds).containsExactlyInAnyOrder(task1.getId(), task2.getId());
    List<TaskSummary> unlockedTasks =
        taskService
            .createTaskQuery()
            .idIn(taskIdsToUnlock.toArray(new String[0]))
            .list();
    for (TaskSummary taskSummary : unlockedTasks) {
      assertThat(taskSummary.isLocked()).isFalse();
    }
  }

  @WithAccessId(user = "admin")
  @Test
  void should_OnlySelectAndUnlockLockedTasks_When_SomeTasksUnlocked() throws Exception {
    Task task1 =
        createDefaultTaskWithLock(false).buildAndStore(taskService);
    Task task2 =
        createDefaultTaskWithLock(true).buildAndStore(taskService);

    List<String> taskIds = List.of(task1.getId(), task2.getId());
    List<String> lockedTaskIds = taskService.selectAndLock(taskIds);

    assertThat(lockedTaskIds).containsExactlyInAnyOrder(task1.getId());
    List<TaskSummary> lockedTasks =
        taskService
            .createTaskQuery()
            .idIn(lockedTaskIds.toArray(new String[0]))
            .list();
    for (TaskSummary taskSummary : lockedTasks) {
      assertThat(taskSummary.isLocked()).isTrue();
    }
  }

  private TaskBuilder createDefaultTaskWithLock(Boolean locked) {
    return TaskBuilder.newTask()
        .classificationSummary(defaultClassificationSummary)
        .workbasketSummary(defaultWorkbasketSummary)
        .primaryObjRef(defaultObjectReference)
        .locked(locked);
  }
}
