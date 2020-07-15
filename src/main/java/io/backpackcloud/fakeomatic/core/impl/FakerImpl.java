package io.backpackcloud.fakeomatic.core.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.backpackcloud.fakeomatic.core.UndefinedSampleException;
import io.backpackcloud.fakeomatic.core.spi.Faker;
import io.backpackcloud.fakeomatic.core.spi.Sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FakerImpl implements Faker {

  final Map<String, Sample>    samples;
  final Map<Character, String> placeholders;

  @JsonCreator
  public FakerImpl(@JsonProperty("samples") Map<String, Sample> samples,
                   @JsonProperty("placeholders") Map<Character, String> placeholders) {
    this.samples = Optional.ofNullable(samples).orElseGet(Collections::emptyMap);
    this.placeholders = Optional.ofNullable(placeholders).orElseGet(Collections::emptyMap);
  }

  @Override
  public List<Sample> samples() {
    return new ArrayList<>(this.samples.values());
  }

  @Override
  public <E> Optional<Sample<E>> sample(String sampleName) {
    if (samples.containsKey(sampleName)) {
      return Optional.of(samples.get(sampleName));
    }
    return Optional.empty();
  }

  @Override
  public String some(char placeholder) {
    if (placeholders.containsKey(placeholder)) {
      return sample(placeholders.get(placeholder))
          .map(Sample::get)
          .map(Object::toString)
          .orElseThrow(UndefinedSampleException::new);
    }
    return String.valueOf(placeholder);
  }


}
