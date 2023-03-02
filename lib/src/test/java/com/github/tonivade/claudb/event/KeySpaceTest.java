/*
 * Copyright (c) 2015-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.event;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

class KeySpaceTest {
  
  @Test
  void commandEvent() {
    KeySpace event = new KeySpace(safeString("command"),
                                  safeString("key"), 0);
    
    assertThat(event.getValue(), equalTo(safeString("key")));
    assertThat(event.getChannel(), equalTo("__keyspace__@0__:command"));
  }
}
