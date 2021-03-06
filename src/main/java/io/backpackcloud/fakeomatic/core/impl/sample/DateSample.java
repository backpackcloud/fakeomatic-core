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
import io.backpackcloud.fakeomatic.core.spi.Sample;
import io.backpackcloud.zipper.UnbelievableException;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

public class DateSample implements Sample<LocalDate> {

  private final Random    random;
  private final LocalDate start;
  private final int       maxDays;

  public DateSample(Random random, LocalDate start, int maxDays) {
    this.random = random;
    this.start = start;
    this.maxDays = maxDays;
  }

  @Override
  public LocalDate get() {
    return start.plus(random.nextInt(maxDays), ChronoUnit.DAYS);
  }

  @JsonCreator
  public static DateSample create(@JacksonInject Random random,
                                  @JsonProperty("from") String fromDate,
                                  @JsonProperty("to") String toDate,
                                  @JsonProperty("period") String periodString,
                                  @JsonProperty("format") String formatString,
                                  @JsonProperty("inclusive") boolean inclusive) {
    DateTimeFormatter format = DateTimeFormatter.ofPattern(Optional.ofNullable(formatString).orElse("yyyy-MM-dd"));

    LocalDate start = parseDate(fromDate, format);
    LocalDate end;
    if (toDate != null) {
      end = parseDate(toDate, format);
    } else if (periodString != null) {
      Period period = Period.parse(periodString);
      end = start.plus(period);
    } else {
      throw new UnbelievableException("No end date or period given.");
    }

    int days = (int) (end.toEpochDay() - start.toEpochDay());

    return new DateSample(random, start, inclusive ? days + 1 : days);
  }

  private static LocalDate parseDate(String date, DateTimeFormatter format) {
    switch (date) {
      case "today":
        return LocalDate.now();
      case "tomorrow":
        return LocalDate.now().plusDays(1);
      case "yesterday":
        return LocalDate.now().minusDays(1);
      default:
        return LocalDate.parse(date, format);
    }
  }

}
