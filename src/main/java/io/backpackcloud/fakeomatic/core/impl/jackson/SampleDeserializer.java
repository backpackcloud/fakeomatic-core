package io.backpackcloud.fakeomatic.core.impl.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.backpackcloud.fakeomatic.core.impl.sample.CachedSample;
import io.backpackcloud.fakeomatic.core.impl.sample.CharSample;
import io.backpackcloud.fakeomatic.core.impl.sample.JoiningSample;
import io.backpackcloud.fakeomatic.core.impl.sample.DateSample;
import io.backpackcloud.fakeomatic.core.impl.sample.ExpressionSample;
import io.backpackcloud.fakeomatic.core.impl.sample.JsonValueSample;
import io.backpackcloud.fakeomatic.core.impl.sample.ListSample;
import io.backpackcloud.fakeomatic.core.impl.sample.RangeSample;
import io.backpackcloud.fakeomatic.core.impl.sample.SourceSample;
import io.backpackcloud.fakeomatic.core.impl.sample.UuidSample;
import io.backpackcloud.fakeomatic.core.impl.sample.WeightedSample;
import io.backpackcloud.fakeomatic.core.spi.Sample;
import io.backpackcloud.zipper.UnbelievableException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Deserializer for parsing sample configuration using Jackson.
 */
public class SampleDeserializer extends JsonDeserializer<Sample> {

  private final ObjectMapper mapper;

  private final Map<String, Class<? extends Sample>> sampleTypes;

  public SampleDeserializer(ObjectMapper mapper) {
    this.mapper = mapper;
    this.sampleTypes = new HashMap<>();

    this.sampleTypes.put("cache", CachedSample.class);
    this.sampleTypes.put("chars", CharSample.class);
    this.sampleTypes.put("join", JoiningSample.class);
    this.sampleTypes.put("date", DateSample.class);
    this.sampleTypes.put("expression", ExpressionSample.class);
    this.sampleTypes.put("json", JsonValueSample.class);
    this.sampleTypes.put("list", ListSample.class);
    this.sampleTypes.put("range", RangeSample.class);
    this.sampleTypes.put("source", SourceSample.class);
    this.sampleTypes.put("uuid", UuidSample.class);
    this.sampleTypes.put("weight", WeightedSample.class);
  }

  /**
   * Registers a new Sample.
   *
   * @param type        the identifier to use
   * @param sampleClass the class of the Sample to register
   * @return this instance.
   */
  public SampleDeserializer register(String type, Class<? extends Sample> sampleClass) {
    this.sampleTypes.put(type, sampleClass);
    return this;
  }

  @Override
  public Sample deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException, JsonProcessingException {
    JsonNode jsonNode = ctx.readTree(jsonParser);

    String type = jsonNode.at("/type").asText();

    if (sampleTypes.containsKey(type)) {
      return mapper.readValue(jsonNode.toString(), sampleTypes.get(type));
    }

    throw new UnbelievableException(String.format("Type '%s' not recognized.", type));
  }

}
