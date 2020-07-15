package io.backpackcloud.fakeomatic.core.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.backpackcloud.fakeomatic.core.impl.jackson.SampleDeserializer;
import io.backpackcloud.fakeomatic.core.spi.Faker;
import io.backpackcloud.fakeomatic.core.spi.Sample;
import io.backpackcloud.zipper.UnbelievableException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class FakerBuilder {

  private final Map<String, Sample>    samples;
  private final Map<Character, String> placeholders;
  private final RootFaker              rootFaker;
  private final ObjectMapper           mapper;
  private final SampleDeserializer     deserializer;
  private final InjectableValues.Std   std;

  public FakerBuilder(Random random) {
    samples = new HashMap<>();
    placeholders = new HashMap<>();
    rootFaker = new RootFaker();
    mapper = new ObjectMapper(new YAMLFactory());
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    std = new InjectableValues.Std();
    std.addValue(Random.class, random);
    std.addValue(Faker.class, rootFaker);

    mapper.setInjectableValues(std);

    SimpleModule module = new SimpleModule();
    deserializer = new SampleDeserializer(mapper);
    module.addDeserializer(Sample.class, deserializer);

    mapper.registerModule(module);
  }

  public ObjectMapper mapper() {
    return mapper;
  }

  public FakerBuilder register(String type, Class<? extends Sample> sampleClass) {
    deserializer.register(type, sampleClass);
    return this;
  }

  public <E> FakerBuilder inject(Class<E> type, E instance) {
    std.addValue(type, instance);
    return this;
  }

  public FakerBuilder inject(String key, Object instance) {
    std.addValue(key, instance);
    return this;
  }

  public FakerBuilder addPlaceholder(char placeholder, String sampleName) {
    placeholders.put(placeholder, sampleName);
    return this;
  }

  public FakerBuilder addSample(String name, Sample sample) {
    samples.put(name, sample);
    return this;
  }

  public FakerBuilder loadFrom(InputStream source) {
    try {
      return load(mapper.readValue(source, FakerImpl.class));
    } catch (IOException e) {
      throw new UnbelievableException(e);
    }
  }

  public FakerBuilder loadFrom(File source) {
    try {
      return load(mapper.readValue(source, FakerImpl.class));
    } catch (IOException e) {
      throw new UnbelievableException(e);
    }
  }

  public FakerBuilder loadFrom(String source) {
    try {
      return load(mapper.readValue(source, FakerImpl.class));
    } catch (IOException e) {
      throw new UnbelievableException(e);
    }
  }

  private FakerBuilder load(FakerImpl faker) {
    samples.putAll(faker.samples);
    placeholders.putAll(faker.placeholders);
    return this;
  }

  public Faker build() {
    FakerImpl faker = new FakerImpl(samples, placeholders);
    rootFaker.delegate = faker;
    return faker;
  }

  static class RootFaker implements Faker {

    Faker delegate;

    @Override
    public Optional<Sample> sample(String sampleName) {
      return Optional.ofNullable(() -> delegate.sample(sampleName).orElseThrow(UnbelievableException::new).get());
    }

    @Override
    public String some(char placeholder) {
      return delegate.some(placeholder);
    }

    @Override
    public Object some(String sampleName) {
      return delegate.some(sampleName);
    }

    @Override
    public String expression(String expression) {
      return delegate.expression(expression);
    }

    @Override
    public List<Sample> samples() {
      return delegate.samples();
    }
  }

}
