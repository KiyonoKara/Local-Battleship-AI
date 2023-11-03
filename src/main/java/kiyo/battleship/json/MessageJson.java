package kiyo.battleship.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * The JSON message with a method name and
 *
 * @param methodName Method name
 * @param arguments Nested arguments
 */
public record MessageJson(@JsonProperty("method-name") String methodName,
                          @JsonProperty("arguments") JsonNode arguments) {
}
