package io.kadai.common.rest;

import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.common.api.KadaiRole;
import io.kadai.common.rest.models.CustomAttributesRepresentationModel;
import io.kadai.common.rest.models.KadaiUserInfoRepresentationModel;
import io.kadai.rest.test.KadaiSpringBootTest;
import io.kadai.rest.test.RestHelper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/** Test KadaiEngineController. */
@KadaiSpringBootTest
class KadaiEngineControllerIntTest {

  private static final RestTemplate TEMPLATE = RestHelper.TEMPLATE;

  private final RestHelper restHelper;

  @Autowired
  KadaiEngineControllerIntTest(RestHelper restHelper) {
    this.restHelper = restHelper;
  }

  @Test
  void testDomains() {
    String url = restHelper.toUrl(RestEndpoints.URL_DOMAIN);
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<List<String>> response =
        TEMPLATE.exchange(
            url, HttpMethod.GET, auth, ParameterizedTypeReference.forType(List.class));
    assertThat(response.getBody()).contains("DOMAIN_A");
  }

  @Test
  void testClassificationTypes() {
    String url = restHelper.toUrl(RestEndpoints.URL_CLASSIFICATION_TYPES);
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<List<String>> response =
        TEMPLATE.exchange(
            url, HttpMethod.GET, auth, ParameterizedTypeReference.forType(List.class));
    assertThat(response.getBody()).containsExactlyInAnyOrder("TASK", "DOCUMENT");
  }

  @Test
  void should_ReturnAllClassifications_When_GetClassificationCategories_isCalledWithoutType() {
    String url = restHelper.toUrl(RestEndpoints.URL_CLASSIFICATION_CATEGORIES);
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<List<String>> response =
        TEMPLATE.exchange(
            url, HttpMethod.GET, auth, ParameterizedTypeReference.forType(List.class));
    assertThat(response.getBody())
        .containsExactlyInAnyOrder("EXTERNAL", "MANUAL", "AUTOMATIC", "PROCESS", "EXTERNAL");
  }

  @Test
  void should_ReturnOnlyClassificationsForTypeTask_When_GetClassificationCategories_isCalled() {
    String url = restHelper.toUrl(RestEndpoints.URL_CLASSIFICATION_CATEGORIES) + "?type=TASK";
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<List<String>> response =
        TEMPLATE.exchange(
            url, HttpMethod.GET, auth, ParameterizedTypeReference.forType(List.class));
    assertThat(response.getBody()).containsExactly("EXTERNAL", "MANUAL", "AUTOMATIC", "PROCESS");
  }

  @Test
  void should_ReturnOnlyClassificationsForTypeDocument_When_GetClassificationCategories_isCalled() {
    String url = restHelper.toUrl(RestEndpoints.URL_CLASSIFICATION_CATEGORIES) + "?type=DOCUMENT";
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<List<String>> response =
        TEMPLATE.exchange(
            url, HttpMethod.GET, auth, ParameterizedTypeReference.forType(List.class));
    assertThat(response.getBody()).containsExactly("EXTERNAL");
  }

  @Test
  void testGetCurrentUserInfo() {
    String url = restHelper.toUrl(RestEndpoints.URL_CURRENT_USER);
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<KadaiUserInfoRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(KadaiUserInfoRepresentationModel.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getUserId()).isEqualTo("teamlead-1");
    assertThat(response.getBody().getGroupIds())
        .contains("cn=business-admins,cn=groups,ou=test,o=kadai");
    assertThat(response.getBody().getRoles())
        .contains(KadaiRole.BUSINESS_ADMIN)
        .doesNotContain(KadaiRole.ADMIN);
  }

  @Test
  void should_ReturnCustomAttributes() {
    String url = restHelper.toUrl(RestEndpoints.URL_CUSTOM_ATTRIBUTES);
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<CustomAttributesRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(CustomAttributesRepresentationModel.class));

    assertThat(response.getBody()).isNotNull();
  }
}
