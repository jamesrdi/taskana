package io.kadai.workbasket.rest;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import io.kadai.common.rest.RestEndpoints;
import io.kadai.rest.test.BaseRestDocTest;
import io.kadai.testapi.security.JaasExtension;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.WorkbasketType;
import io.kadai.workbasket.internal.models.WorkbasketImpl;
import io.kadai.workbasket.rest.assembler.WorkbasketRepresentationModelAssembler;
import io.kadai.workbasket.rest.models.WorkbasketDefinitionCollectionRepresentationModel;
import io.kadai.workbasket.rest.models.WorkbasketDefinitionRepresentationModel;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(JaasExtension.class)
class WorkbasketDefinitionControllerRestDocTest extends BaseRestDocTest {

  @Autowired WorkbasketService workbasketService;
  @Autowired WorkbasketRepresentationModelAssembler assembler;

  @Test
  void exportWorkbasketDefinitionsDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_WORKBASKET_DEFINITIONS))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithAccessId(user = "admin")
  void importWorkbasketDefinitionDocTest() throws Exception {
    WorkbasketImpl workbasket =
        (WorkbasketImpl) workbasketService.newWorkbasket("neuerKey", "DOMAIN_A");
    workbasket.setName("neuer Name");
    workbasket.setType(WorkbasketType.GROUP);
    workbasket.setId("gibtsNochNicht");

    WorkbasketDefinitionRepresentationModel repModel =
        new WorkbasketDefinitionRepresentationModel();
    repModel.setWorkbasket(assembler.toModel(workbasket));
    WorkbasketDefinitionCollectionRepresentationModel repModelList =
        new WorkbasketDefinitionCollectionRepresentationModel(List.of(repModel));

    mockMvc
        .perform(
            multipart(RestEndpoints.URL_WORKBASKET_DEFINITIONS)
                .file("file", objectMapper.writeValueAsBytes(repModelList)))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }
}
