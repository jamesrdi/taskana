package io.kadai.simplehistory;

import io.kadai.common.api.KadaiRole;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.spi.history.api.KadaiHistory;

/** The KadaiHistoryEngine represents an overall set of all needed services. */
public interface KadaiHistoryEngine {
  /**
   * The KadaiHistory can be used for operations on all history events.
   *
   * @return the HistoryService
   */
  KadaiHistory getKadaiHistoryService();

  /**
   * check whether the current user is member of one of the roles specified.
   *
   * @param roles The roles that are checked for membership of the current user
   * @return true if the current user is a member of at least one of the specified groups
   */
  boolean isUserInRole(KadaiRole... roles);

  /**
   * Checks whether current user is member of any of the specified roles.
   *
   * @param roles The roles that are checked for membership of the current user
   * @throws NotAuthorizedException If the current user is not member of any specified role
   */
  void checkRoleMembership(KadaiRole... roles) throws NotAuthorizedException;
}
