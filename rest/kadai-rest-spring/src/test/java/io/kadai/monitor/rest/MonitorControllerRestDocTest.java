package io.kadai.monitor.rest;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

import io.kadai.common.rest.RestEndpoints;
import io.kadai.rest.test.BaseRestDocTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class MonitorControllerRestDocTest extends BaseRestDocTest {

  @Test
  void computeWorkbasketReportDocTest() throws Exception {
    mockMvc
        .perform(
            get(
                RestEndpoints.URL_MONITOR_WORKBASKET_REPORT
                    + "?state=READY&state=CLAIMED&state=COMPLETED"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void computeWorkbasketPriorityReportDocTest() throws Exception {
    mockMvc
        .perform(
            get(RestEndpoints.URL_MONITOR_WORKBASKET_PRIORITY_REPORT + "?workbasket-type=GROUP"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void computeClassificationCategoryReportDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_MONITOR_CLASSIFICATION_CATEGORY_REPORT))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void computeClassificationReportDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_MONITOR_CLASSIFICATION_REPORT))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void computeDetailedClassificationReportDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_MONITOR_DETAILED_CLASSIFICATION_REPORT))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void computeTaskCustomFieldValueReportDocTest() throws Exception {
    mockMvc
        .perform(
            get(
                RestEndpoints.URL_MONITOR_TASK_CUSTOM_FIELD_VALUE_REPORT
                    + "?custom-field=CUSTOM_14"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void computeTaskStatusReportDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_MONITOR_TASK_STATUS_REPORT))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void computeTimestampReportDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_MONITOR_TIMESTAMP_REPORT))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
