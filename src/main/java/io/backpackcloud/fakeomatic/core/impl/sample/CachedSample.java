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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.backpackcloud.fakeomatic.core.spi.Sample;
import io.backpackcloud.fakeomatic.core.spi.SampleConfiguration;
import io.backpackcloud.zipper.Configuration;
import io.backpackcloud.zipper.UnbelievableException;

import java.util.Optional;

public class CachedSample implements Sample {

  private final Sample sample;
  private final int    ttl;

  private Object cachedValue;
  private int    hits;

  public CachedSample(Sample sample, int ttl) {
    this.sample = sample;
    this.ttl = ttl;
  }

  @Override
  public Object get() {
    if (this.cachedValue == null) {
      this.cachedValue = this.sample.get();
    }
    try {
      return this.cachedValue;
    } finally {
      if (++hits == ttl) {
        this.cachedValue = null;
      }
    }
  }

  @JsonCreator
  public static CachedSample create(@JsonProperty("source") SampleConfiguration source,
                                    @JsonProperty("ttl") Configuration ttl) {
    return new CachedSample(
        Optional.ofNullable(source)
                .map(SampleConfiguration::sample)
                .orElseThrow(UnbelievableException
                    .because("No source was given.")),

        Optional.ofNullable(ttl)
                .filter(Configuration::isSet)
                .map(Configuration::asInt)
                .orElse(Integer.MAX_VALUE)
    );
  }

}
