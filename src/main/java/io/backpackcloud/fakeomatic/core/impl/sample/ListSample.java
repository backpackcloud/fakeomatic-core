/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Marcelo Guimarães <ataxexe@backpackcloud.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.backpackcloud.fakeomatic.core.impl.sample;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.backpackcloud.fakeomatic.core.spi.Faker;
import io.backpackcloud.fakeomatic.core.spi.Sample;
import io.backpackcloud.zipper.Configuration;
import io.backpackcloud.zipper.UnbelievableException;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * This sample can pick any item from a given list of objects. The object will be used in its
 * `string` form. Useful for defining a set of data that is meant to be read, like cities and names.
 *
 * @author Marcelo Guimarães
 */
public class ListSample<E> implements Sample<E> {

  private final Random          random;
  private final List<Sample<E>> samples;

  public ListSample(Random random, List<Sample<E>> samples) {
    this.random = random;
    this.samples = samples;
  }

  public List<Sample<E>> samples() {
    return samples;
  }

  @Override
  public E get() {
    int       index        = random.nextInt(samples.size());
    Sample<E> randomSample = samples.get(index);
    return randomSample.get();
  }

  @JsonCreator
  public static ListSample<?> create(@JacksonInject Random random,
                                     @JacksonInject Faker faker,
                                     @JsonProperty("values") List<Object> values,
                                     @JsonProperty("samples") List<String> samplesNames,
                                     @JsonProperty("source") Configuration source) {
    List<Sample> samples;
    if (values != null) {
      samples = values.stream()
                      .map(Sample::of)
                      .collect(Collectors.toList());
    } else if (samplesNames != null) {
      samples = samplesNames.stream()
                            .map(faker::sample)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(Collectors.toList());
    } else if (source != null) {
      samples = source.readLines().stream()
                      .map(Sample::of)
                      .collect(Collectors.toList());
    } else {
      throw new UnbelievableException("No valid configuration supplied");
    }
    return new ListSample(random, samples);
  }

}
