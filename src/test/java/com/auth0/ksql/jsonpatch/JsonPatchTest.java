package com.auth0.ksql.jsonpatch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonPatchTest {
  private final ObjectMapper mapper = new ObjectMapper();

  private JsonNode compute(String... objs) throws JsonProcessingException, IOException {
    JsonPatch m = new JsonPatch();
    String result = m.jsonpatch(objs);
    return mapper.readTree(result);
  }


  @Test
  public void shouldReturnEmptyWhenBothAreEqual() throws IOException {
    JsonNode result = compute(
      mapper.writeValueAsString(Map.of(
        "user_id", "auth0|xyz",
        "displayName", "Boor"
      )),
      mapper.writeValueAsString(Map.of(
        "user_id", "auth0|xyz",
        "displayName", "Boor"
      ))
    );

    assertThat(result.isArray()).isTrue();
    assertThat(result.isEmpty()).isTrue();
  }


  @Test
  public void shouldReturnAnUpdateRootWhenSecondIsNull() throws IOException {
    JsonNode result = compute(
      mapper.writeValueAsString(Map.of(
        "user_id", "auth0|xyz",
        "displayName", "Boor"
      ))
    );

    assertThat(result.isArray()).isTrue();
    assertThat(result.size()).isEqualTo(1);

    JsonNode change = result.get(0);
    assertThat(change.get("op").asText()).isEqualTo("replace");
    assertThat(change.get("path").asText()).isEqualTo("");
  }

  @Test
  public void shouldWorkWhenAPropertyChange() throws IOException {
    JsonNode result = compute(
      mapper.writeValueAsString(Map.of(
        "user_id", "auth0|xyz",
        "displayName", "Boor"
      )),
      mapper.writeValueAsString(Map.of(
        "user_id", "auth0|xyz",
        "displayName", "Fux"
      ))
    );

    assertThat(result.isArray()).isTrue();
    assertThat(result.size()).isEqualTo(1);

    JsonNode change = result.get(0);
    assertThat(change.get("op").asText()).isEqualTo("replace");
    assertThat(change.get("path").asText()).isEqualTo("/displayName");
  }


}
