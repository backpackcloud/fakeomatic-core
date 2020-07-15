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
import io.backpackcloud.fakeomatic.core.UndefinedSampleException;
import io.backpackcloud.fakeomatic.core.spi.Faker;
import io.backpackcloud.fakeomatic.core.spi.Sample;
import io.backpackcloud.zipper.UnbelievableException;

import java.util.function.Supplier;

public class ExpressionSample implements Sample<String> {

  private final Faker       faker;
  private final Supplier<?> expressionSupplier;

  public ExpressionSample(Supplier<?> expressionSupplier, Faker faker) {
    this.expressionSupplier = expressionSupplier;
    this.faker = faker;
  }

  @Override
  public String get() {
    String expression = expressionSupplier.get().toString();
    return faker.expression(expression);
  }

  @JsonCreator
  public static ExpressionSample create(@JsonProperty("sample") String sampleName,
                                        @JsonProperty("expression") String expression,
                                        @JacksonInject Faker faker) {
    if (sampleName != null) {
      return new ExpressionSample(faker.sample(sampleName).orElseThrow(UndefinedSampleException::new), faker);
    } else if (expression != null) {
      return new ExpressionSample(() -> expression, faker);
    } else {
      throw new UnbelievableException("No sample or expression given.");
    }
  }

}
