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

package io.backpackcloud.fakeomatic.core.spi;

import io.backpackcloud.zipper.UnbelievableException;

import java.util.List;
import java.util.Optional;

/**
 * Defines a component that can produce fake data.
 * <p>
 * This is the root object of the templates.
 *
 * @author Marcelo Guimarães
 * @see Sample
 */
public interface Faker {

  /**
   * Gets all the samples that this fake data holds.
   *
   * @return a list of the samples.
   */
  List<Sample> samples();

  /**
   * Returns the Sample data associated with the given name.
   *
   * @param sampleName the key for locating the Sample object.
   * @return the Sample object associated with the given name.
   */
  <E> Optional<Sample<E>> sample(String sampleName);

  /**
   * Returns a random data from the Sample associated with the given placeholder.
   *
   * @param placeholder the placeholder to get the sample.
   * @return a random data.
   */
  String some(char placeholder);

  /**
   * Returns a random data from the Sample associated with the given name.
   *
   * @param sampleName the name of the Sample.
   * @return a random data.
   */
  default <E> E some(String sampleName) {
    Optional<Sample<Object>> sample = sample(sampleName);
    return (E) sample.map(Sample::get)
                     .orElseThrow(UnbelievableException
                         .because("Sample '" + sampleName + "' wasn't defined."));
  }

  /**
   * Generates a random expression looking for placeholders in the given expression.
   *
   * @param expression the expression to evaluate.
   * @return a random generated expression.
   * @see #some(char)
   */
  default String expression(String expression) {
    StringBuilder builder = new StringBuilder(expression.length());

    for (int i = 0; i < expression.length(); i++) {
      builder.append(some(expression.charAt(i)));
    }

    return builder.toString();
  }

}
