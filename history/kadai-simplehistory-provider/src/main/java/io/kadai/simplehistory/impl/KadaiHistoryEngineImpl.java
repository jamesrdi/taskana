package io.kadai.simplehistory.impl;

import io.kadai.KadaiConfiguration;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.KadaiRole;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.OracleSqlSessionFactory;
import io.kadai.common.internal.configuration.DB;
import io.kadai.common.internal.persistence.InstantTypeHandler;
import io.kadai.common.internal.persistence.MapTypeHandler;
import io.kadai.common.internal.persistence.StringTypeHandler;
import io.kadai.simplehistory.KadaiHistoryEngine;
import io.kadai.simplehistory.impl.classification.ClassificationHistoryEventMapper;
import io.kadai.simplehistory.impl.classification.ClassificationHistoryQueryMapper;
import io.kadai.simplehistory.impl.task.TaskHistoryEventMapper;
import io.kadai.simplehistory.impl.task.TaskHistoryQueryMapper;
import io.kadai.simplehistory.impl.workbasket.WorkbasketHistoryEventMapper;
import io.kadai.simplehistory.impl.workbasket.WorkbasketHistoryQueryMapper;
import io.kadai.spi.history.api.KadaiHistory;
import io.kadai.user.internal.UserMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This is the implementation of KadaiHistoryEngine. */
public class KadaiHistoryEngineImpl implements KadaiHistoryEngine {

  protected static final ThreadLocal<Deque<SqlSessionManager>> SESSION_STACK = new ThreadLocal<>();
  private static final Logger LOGGER = LoggerFactory.getLogger(KadaiHistoryEngineImpl.class);
  private static final String DEFAULT = "default";
  private final SqlSessionManager sessionManager;
  private final KadaiConfiguration kadaiConfiguration;
  private final KadaiEngine kadaiEngine;
  private TransactionFactory transactionFactory;
  private KadaiHistory kadaiHistoryService;

  protected KadaiHistoryEngineImpl(KadaiEngine kadaiEngine) {
    this.kadaiConfiguration = kadaiEngine.getConfiguration();
    this.kadaiEngine = kadaiEngine;

    createTransactionFactory(kadaiConfiguration.isUseManagedTransactions());
    sessionManager = createSqlSessionManager();
  }

  public static KadaiHistoryEngineImpl createKadaiEngine(KadaiEngine kadaiEngine) {
    return new KadaiHistoryEngineImpl(kadaiEngine);
  }

  @Override
  public KadaiHistory getKadaiHistoryService() {
    if (kadaiHistoryService == null) {
      SimpleHistoryServiceImpl historyService = new SimpleHistoryServiceImpl();
      historyService.initialize(kadaiEngine);
      kadaiHistoryService = historyService;
    }
    return kadaiHistoryService;
  }

  public boolean isUserInRole(KadaiRole... roles) {
    if (!getConfiguration().isSecurityEnabled()) {
      return true;
    }

    Set<String> rolesMembers =
        Arrays.stream(roles)
            .map(role -> getConfiguration().getRoleMap().get(role))
            .collect(HashSet::new, Set::addAll, Set::addAll);

    return kadaiEngine.getCurrentUserContext().getAccessIds().stream()
        .anyMatch(rolesMembers::contains);
  }

  public void checkRoleMembership(KadaiRole... roles) throws NotAuthorizedException {
    if (!isUserInRole(roles)) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "Throwing NotAuthorizedException because accessIds {} are not member of roles {}",
            kadaiEngine.getCurrentUserContext().getAccessIds(),
            Arrays.toString(roles));
      }
      throw new NotAuthorizedException(kadaiEngine.getCurrentUserContext().getUserid(), roles);
    }
  }

  public KadaiConfiguration getConfiguration() {
    return this.kadaiConfiguration;
  }

  protected SqlSessionManager createSqlSessionManager() {
    Environment environment =
        new Environment(DEFAULT, this.transactionFactory, kadaiConfiguration.getDataSource());
    Configuration configuration = new Configuration(environment);

    // set databaseId
    DB db;
    try (Connection con = kadaiConfiguration.getDataSource().getConnection()) {
      db = DB.getDB(con);
      configuration.setDatabaseId(db.dbProductId);
    } catch (SQLException e) {
      throw new SystemException("Could not open a connection to set the databaseId", e);
    }

    // register type handlers
    if (DB.ORACLE == db) {
      // Use NULL instead of OTHER when jdbcType is not specified for null values,
      // otherwise oracle driver will chunck on null values
      configuration.setJdbcTypeForNull(JdbcType.NULL);
      configuration.getTypeHandlerRegistry().register(String.class, new StringTypeHandler());
    }
    configuration.getTypeHandlerRegistry().register(new MapTypeHandler());
    configuration.getTypeHandlerRegistry().register(Instant.class, new InstantTypeHandler());
    configuration.getTypeHandlerRegistry().register(JdbcType.TIMESTAMP, new InstantTypeHandler());

    // add mappers
    configuration.addMapper(TaskHistoryEventMapper.class);
    configuration.addMapper(TaskHistoryQueryMapper.class);
    configuration.addMapper(WorkbasketHistoryEventMapper.class);
    configuration.addMapper(WorkbasketHistoryQueryMapper.class);
    configuration.addMapper(ClassificationHistoryEventMapper.class);
    configuration.addMapper(ClassificationHistoryQueryMapper.class);
    configuration.addMapper(UserMapper.class);

    SqlSessionFactory localSessionFactory;
    if (DB.ORACLE == db) {
      localSessionFactory =
          new SqlSessionFactoryBuilder() {
            @Override
            public SqlSessionFactory build(Configuration config) {
              return new OracleSqlSessionFactory(config);
            }
          }.build(configuration);
    } else {
      localSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }
    return SqlSessionManager.newInstance(localSessionFactory);
  }

  protected static void pushSessionToStack(SqlSessionManager session) {
    getSessionStack().push(session);
  }

  protected static void popSessionFromStack() {
    Deque<SqlSessionManager> stack = getSessionStack();
    if (!stack.isEmpty()) {
      stack.pop();
    }
  }

  /**
   * With sessionStack, we maintain a Stack of SqlSessionManager objects on a per thread basis.
   * SqlSessionManager is the MyBatis object that wraps database connections. The purpose of this
   * stack is to keep track of nested calls. Each external API call is wrapped into
   * kadaiEngineImpl.openConnection(); ..... kadaiEngineImpl.returnConnection(); calls. In order
   * to avoid duplicate opening / closing of connections, we use the sessionStack in the following
   * way: Each time, an openConnection call is received, we push the current sessionManager onto the
   * stack. On the first call to openConnection, we call sessionManager.startManagedSession() to
   * open a database connection. On each call to returnConnection() we pop one instance of
   * sessionManager from the stack. When the stack becomes empty, we close the database connection
   * by calling sessionManager.close()
   *
   * @return Stack of SqlSessionManager
   */
  protected static Deque<SqlSessionManager> getSessionStack() {
    Deque<SqlSessionManager> stack = SESSION_STACK.get();
    if (stack == null) {
      stack = new ArrayDeque<>();
      SESSION_STACK.set(stack);
    }
    return stack;
  }

  protected static SqlSessionManager getSessionFromStack() {
    Deque<SqlSessionManager> stack = getSessionStack();
    if (stack.isEmpty()) {
      return null;
    }
    return stack.peek();
  }

  /**
   * Open the connection to the database. to be called at the begin of each Api call that accesses
   * the database
   *
   * @throws SQLException thrown if the connection could not be opened.
   */
  void openConnection() throws SQLException {
    initSqlSession();
    this.sessionManager.getConnection().setSchema(kadaiConfiguration.getSchemaName());
  }

  /**
   * Returns the database connection into the pool. In the case of nested calls, simply pops the
   * latest session from the session stack. Closes the connection if the session stack is empty. In
   * mode AUTOCOMMIT commits before the connection is closed. To be called at the end of each Api
   * call that accesses the database
   */
  void returnConnection() {
    popSessionFromStack();
    if (getSessionStack().isEmpty()
        && this.sessionManager != null
        && this.sessionManager.isManagedSessionStarted()) {
      try {
        this.sessionManager.commit();
      } catch (Exception e) {
        // ignore
      }
      this.sessionManager.close();
    }
  }

  /** Initializes the SqlSessionManager. */
  void initSqlSession() {
    this.sessionManager.startManagedSession();
  }

  /**
   * retrieve the SqlSession used by kadai.
   *
   * @return the myBatis SqlSession object used by kadai
   */
  SqlSession getSqlSession() {
    return this.sessionManager;
  }

  /**
   * creates the MyBatis transaction factory.
   *
   * @param useManagedTransactions true if KADAI should use a ManagedTransactionFactory.
   */
  private void createTransactionFactory(boolean useManagedTransactions) {
    if (useManagedTransactions) {
      this.transactionFactory = new ManagedTransactionFactory();
    } else {
      this.transactionFactory = new JdbcTransactionFactory();
    }
  }
}
