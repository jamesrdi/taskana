package io.kadai.common.rest;

import io.kadai.KadaiConfiguration;
import io.kadai.common.api.ConfigurationService;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.security.CurrentUserContext;
import io.kadai.common.rest.models.CustomAttributesRepresentationModel;
import io.kadai.common.rest.models.KadaiUserInfoRepresentationModel;
import io.kadai.common.rest.models.VersionRepresentationModel;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Controller for KadaiEngine related tasks. */
@RestController
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class KadaiEngineController {

  private final KadaiConfiguration kadaiConfiguration;
  private final KadaiEngine kadaiEngine;
  private final CurrentUserContext currentUserContext;
  private final ConfigurationService configurationService;

  @Autowired
  KadaiEngineController(
      KadaiConfiguration kadaiConfiguration,
      KadaiEngine kadaiEngine,
      CurrentUserContext currentUserContext,
      ConfigurationService configurationService) {
    this.kadaiConfiguration = kadaiConfiguration;
    this.kadaiEngine = kadaiEngine;
    this.currentUserContext = currentUserContext;
    this.configurationService = configurationService;
  }

  /**
   * This endpoint retrieves all configured Domains.
   *
   * @return An array with the domain-names as strings
   */
  @GetMapping(path = RestEndpoints.URL_DOMAIN)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<List<String>> getDomains() {
    return ResponseEntity.ok(kadaiConfiguration.getDomains());
  }

  /**
   * This endpoint retrieves the configured classification categories for a specific classification
   * type.
   *
   * @param type the classification type whose categories should be determined. If not specified all
   *     classification categories will be returned.
   * @return the classification categories for the requested type.
   */
  @GetMapping(path = RestEndpoints.URL_CLASSIFICATION_CATEGORIES)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<List<String>> getClassificationCategories(
      @RequestParam(required = false) String type) {
    if (type != null) {
      return ResponseEntity.ok(kadaiConfiguration.getClassificationCategoriesByType(type));
    }
    return ResponseEntity.ok(kadaiConfiguration.getAllClassificationCategories());
  }

  /**
   * This endpoint retrieves the configured classification types.
   *
   * @return the configured classification types.
   */
  @GetMapping(path = RestEndpoints.URL_CLASSIFICATION_TYPES)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<List<String>> getClassificationTypes() {
    return ResponseEntity.ok(kadaiConfiguration.getClassificationTypes());
  }

  /**
   * This endpoint retrieves all configured classification categories grouped by each classification
   * type.
   *
   * @return the configured classification categories
   */
  @GetMapping(path = RestEndpoints.URL_CLASSIFICATION_CATEGORIES_BY_TYPES)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<Map<String, List<String>>> getClassificationCategoriesByTypeMap() {
    return ResponseEntity.ok(kadaiConfiguration.getClassificationCategoriesByType());
  }

  /**
   * This endpoint computes all information of the current user.
   *
   * @return the information of the current user.
   */
  @GetMapping(path = RestEndpoints.URL_CURRENT_USER)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<KadaiUserInfoRepresentationModel> getCurrentUserInfo() {
    KadaiUserInfoRepresentationModel resource = new KadaiUserInfoRepresentationModel();
    resource.setUserId(currentUserContext.getUserid());
    resource.setGroupIds(currentUserContext.getGroupIds());
    kadaiConfiguration.getRoleMap().keySet().stream()
        .filter(kadaiEngine::isUserInRole)
        .forEach(resource.getRoles()::add);
    return ResponseEntity.ok(resource);
  }

  /**
   * This endpoint checks if the history module is in use.
   *
   * @return true, when the history is enabled, otherwise false
   */
  @GetMapping(path = RestEndpoints.URL_HISTORY_ENABLED)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<Boolean> getIsHistoryProviderEnabled() {
    return ResponseEntity.ok(kadaiEngine.isHistoryEnabled());
  }

  /**
   * This endpoint retrieves the saved custom configuration.
   *
   * @title Get custom configuration
   * @return custom configuration
   */
  @GetMapping(path = RestEndpoints.URL_CUSTOM_ATTRIBUTES)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<CustomAttributesRepresentationModel> getCustomAttributes() {
    Map<String, Object> allCustomAttributes = configurationService.getAllCustomAttributes();
    return ResponseEntity.ok(new CustomAttributesRepresentationModel(allCustomAttributes));
  }

  /**
   * This endpoint overrides the custom configuration.
   *
   * @param customAttributes the new custom configuration
   * @title Set all custom configuration
   * @return the new custom configuration
   */
  @PutMapping(path = RestEndpoints.URL_CUSTOM_ATTRIBUTES)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<CustomAttributesRepresentationModel> setCustomAttributes(
      @RequestBody CustomAttributesRepresentationModel customAttributes) {
    configurationService.setAllCustomAttributes(customAttributes.getCustomAttributes());
    return ResponseEntity.ok(customAttributes);
  }

  /**
   * Get the current application version.
   *
   * @return The current version.
   */
  @GetMapping(path = RestEndpoints.URL_VERSION)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<VersionRepresentationModel> currentVersion() {
    VersionRepresentationModel resource = new VersionRepresentationModel();
    resource.setVersion(KadaiConfiguration.class.getPackage().getImplementationVersion());
    return ResponseEntity.ok(resource);
  }
}
