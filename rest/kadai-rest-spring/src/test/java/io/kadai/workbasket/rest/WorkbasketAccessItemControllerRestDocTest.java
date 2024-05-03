package io.kadai.workbasket.rest;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

import io.kadai.common.rest.RestEndpoints;
import io.kadai.rest.test.BaseRestDocTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class WorkbasketAccessItemControllerRestDocTest extends BaseRestDocTest {

  @Test
  void getWorkbasketAccessItemsDocTest() throws Exception {
    mockMvc
        .perform(
            get(
                RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS
                    + "?sort-by=WORKBASKET_KEY&order=ASCENDING&access-id=user-2-2"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void removeWorkbasketAccessItemsDocTest() throws Exception {
    mockMvc
        .perform(delete(RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS + "?access-id=user-2-1"))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }
}
