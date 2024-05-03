package io.kadai.classification.rest;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.Classification;
import io.kadai.classification.rest.assembler.ClassificationRepresentationModelAssembler;
import io.kadai.classification.rest.models.ClassificationCollectionRepresentationModel;
import io.kadai.common.rest.RestEndpoints;
import io.kadai.rest.test.BaseRestDocTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class ClassificationDefinitionControllerRestDocTest extends BaseRestDocTest {

  @Autowired ClassificationRepresentationModelAssembler assembler;
  @Autowired ClassificationService classificationService;

  @Test
  void exportClassificationDefinitionsDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_CLASSIFICATION_DEFINITIONS))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void importClassificationDefinitionsDocTest() throws Exception {
    Classification classification =
        classificationService.newClassification("Key0815", "DOMAIN_B", "TASK");
    classification.setServiceLevel("P1D");

    ClassificationCollectionRepresentationModel importCollection =
        new ClassificationCollectionRepresentationModel(List.of(assembler.toModel(classification)));

    this.mockMvc
        .perform(
            multipart(RestEndpoints.URL_CLASSIFICATION_DEFINITIONS)
                .file("file", objectMapper.writeValueAsBytes(importCollection)))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }
}
