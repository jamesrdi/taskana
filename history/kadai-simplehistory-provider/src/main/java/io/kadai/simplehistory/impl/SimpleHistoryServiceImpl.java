package io.kadai.simplehistory.impl;

import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.KadaiRole;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.simplehistory.impl.classification.ClassificationHistoryEventMapper;
import io.kadai.simplehistory.impl.classification.ClassificationHistoryQuery;
import io.kadai.simplehistory.impl.task.TaskHistoryEventMapper;
import io.kadai.simplehistory.impl.task.TaskHistoryQuery;
import io.kadai.simplehistory.impl.workbasket.WorkbasketHistoryEventMapper;
import io.kadai.simplehistory.impl.workbasket.WorkbasketHistoryQuery;
import io.kadai.spi.history.api.KadaiHistory;
import io.kadai.spi.history.api.events.classification.ClassificationHistoryEvent;
import io.kadai.spi.history.api.events.task.TaskHistoryEvent;
import io.kadai.spi.history.api.events.workbasket.WorkbasketHistoryEvent;
import io.kadai.spi.history.api.exceptions.KadaiHistoryEventNotFoundException;
import io.kadai.user.api.models.User;
import io.kadai.user.internal.UserMapper;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This is the implementation of KadaiHistory. */
public class SimpleHistoryServiceImpl implements KadaiHistory {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHistoryServiceImpl.class);
  private KadaiHistoryEngineImpl kadaiHistoryEngine;
  private TaskHistoryEventMapper taskHistoryEventMapper;
  private WorkbasketHistoryEventMapper workbasketHistoryEventMapper;
  private ClassificationHistoryEventMapper classificationHistoryEventMapper;
  private UserMapper userMapper;

  public void initialize(KadaiEngine kadaiEngine) {

    this.kadaiHistoryEngine = getKadaiEngine(kadaiEngine);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Simple history service implementation initialized with schemaName: {} ",
          kadaiEngine.getConfiguration().getSchemaName());
    }

    this.taskHistoryEventMapper =
        this.kadaiHistoryEngine.getSqlSession().getMapper(TaskHistoryEventMapper.class);
    this.workbasketHistoryEventMapper =
        this.kadaiHistoryEngine.getSqlSession().getMapper(WorkbasketHistoryEventMapper.class);
    this.classificationHistoryEventMapper =
        this.kadaiHistoryEngine.getSqlSession().getMapper(ClassificationHistoryEventMapper.class);
    this.userMapper = kadaiHistoryEngine.getSqlSession().getMapper(UserMapper.class);
  }

  @Override
  public void create(TaskHistoryEvent event) {
    try {
      kadaiHistoryEngine.openConnection();
      if (event.getCreated() == null) {
        Instant now = Instant.now();
        event.setCreated(now);
      }
      taskHistoryEventMapper.insert(event);
    } catch (SQLException e) {
      LOGGER.error("Error while inserting task history event into database", e);
    } finally {
      kadaiHistoryEngine.returnConnection();
    }
  }

  @Override
  public void create(WorkbasketHistoryEvent event) {
    try {
      kadaiHistoryEngine.openConnection();
      if (event.getCreated() == null) {
        Instant now = Instant.now();
        event.setCreated(now);
      }
      workbasketHistoryEventMapper.insert(event);
    } catch (SQLException e) {
      LOGGER.error("Error while inserting workbasket history event into database", e);
    } finally {
      kadaiHistoryEngine.returnConnection();
    }
  }

  @Override
  public void create(ClassificationHistoryEvent event) {
    try {
      kadaiHistoryEngine.openConnection();
      if (event.getCreated() == null) {
        Instant now = Instant.now();
        event.setCreated(now);
      }
      classificationHistoryEventMapper.insert(event);
    } catch (SQLException e) {
      LOGGER.error("Error while inserting classification history event into database", e);
    } finally {
      kadaiHistoryEngine.returnConnection();
    }
  }

  @Override
  public void deleteHistoryEventsByTaskIds(List<String> taskIds)
      throws InvalidArgumentException, NotAuthorizedException {
    kadaiHistoryEngine.checkRoleMembership(KadaiRole.ADMIN);

    if (taskIds == null) {
      throw new InvalidArgumentException("List of taskIds must not be null.");
    }

    try {
      kadaiHistoryEngine.openConnection();
      taskHistoryEventMapper.deleteMultipleByTaskIds(taskIds);
    } catch (SQLException e) {
      LOGGER.error("Caught exception while trying to delete history events", e);
    } finally {
      kadaiHistoryEngine.returnConnection();
    }
  }

  public TaskHistoryEvent getTaskHistoryEvent(String historyEventId)
      throws KadaiHistoryEventNotFoundException {
    TaskHistoryEvent resultEvent = null;
    try {
      kadaiHistoryEngine.openConnection();
      resultEvent = taskHistoryEventMapper.findById(historyEventId);

      if (resultEvent == null) {
        throw new KadaiHistoryEventNotFoundException(historyEventId);
      }

      if (kadaiHistoryEngine.getConfiguration().isAddAdditionalUserInfo()) {
        User user = userMapper.findById(resultEvent.getUserId());
        if (user != null) {
          resultEvent.setUserLongName(user.getLongName());
        }
      }
      return resultEvent;

    } catch (SQLException e) {
      LOGGER.error("Caught exception while trying to retrieve a history event", e);
      return resultEvent;
    } finally {
      kadaiHistoryEngine.returnConnection();
    }
  }

  public TaskHistoryQuery createTaskHistoryQuery() {
    return new TaskHistoryQueryImpl(kadaiHistoryEngine);
  }

  public WorkbasketHistoryQuery createWorkbasketHistoryQuery() {
    return new WorkbasketHistoryQueryImpl(kadaiHistoryEngine);
  }

  public ClassificationHistoryQuery createClassificationHistoryQuery() {
    return new ClassificationHistoryQueryImpl(kadaiHistoryEngine);
  }

  /*
   * ATTENTION: This method exists for testing purposes.
   */
  KadaiHistoryEngineImpl getKadaiEngine(KadaiEngine kadaiEngine) {
    return KadaiHistoryEngineImpl.createKadaiEngine(kadaiEngine);
  }
}
