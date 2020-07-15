package io.backpackcloud.fakeomatic.core.impl;

import io.backpackcloud.fakeomatic.BaseTest;
import io.backpackcloud.fakeomatic.core.spi.Faker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FakerTest extends BaseTest {

  Faker faker = createFaker("faker.yaml");

  @Test
  public void testSomeMethod() {
    String letter = faker.some("letter");
    assertNotNull(letter);
  }

}
