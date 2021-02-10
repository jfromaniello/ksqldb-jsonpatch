package com.auth0.ksql.jsonpatch;

import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.diff.JsonDiff;

/**
 * An example UDF that operates on two scalars.
 *
 * Usage in KSQL: `MULTIPLY(col1, col2)`.
 */
@UdfDescription(
  name = "JSONPATCH",
  description = "Generates a json patch from two jsons"
)
public class JsonPatch {

  // See
  // https://docs.confluent.io/current/ksql/docs/developer-guide/udf.html#null-handling
  // for more information how your UDF should handle `null` input.

  @Udf(description = "Compute the json patch from an array of jsons.")
  public String jsonpatch(
      @UdfParameter("input") final String... input
  ) throws JsonProcessingException, IOException {

    JsonNode a, b;

    final ObjectMapper mapper = new ObjectMapper();

    if (input.length < 2) {
      a = mapper.readTree("null");
      b = mapper.readTree(input[0].toString());
    } else {
      a = mapper.readTree(input[0].toString());
      b = mapper.readTree(input[1].toString());
    }

    return JsonDiff.asJson(a, b).toString();
  }
}
