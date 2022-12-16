/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.scripting;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.resp.protocol.RedisToken.nullString;
import static com.github.tonivade.resp.protocol.RedisToken.status;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import com.github.tonivade.resp.protocol.RedisToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SchemeInterpreterTest {

  @Mock
  private RedisLibrary redis;

  private SchemeInterpreter interpreter;

  @Before
  public void setUp() {
    interpreter = new SchemeInterpreter(new SchemeRedisBinding(redis));
  }

  @Test
  public void keys() {
    RedisToken token = interpreter.execute(safeString("(vector-ref KEYS 0)"),
                                           asList(safeString("key1")),
                                           emptyList());

    assertThat(token, equalTo(string("key1")));
  }

  @Test
  public void argv() {
    RedisToken token = interpreter.execute(safeString("(vector-ref ARGV 0)"),
                                           asList(safeString("key1")),
                                           asList(safeString("value1")));

    assertThat(token, equalTo(string("value1")));
  }

  @Test
  public void keysAndArgv() {
    RedisToken token = interpreter.execute(safeString("(vector (vector-ref KEYS 0) (vector-ref ARGV 0))"),
                                           asList(safeString("key1")),
                                           asList(safeString("value1")));

    assertThat(token, equalTo(array(string("key1"), string("value1"))));
  }

  @Test
  public void number() {
    RedisToken token = interpreter.execute(safeString("1"),
                                           emptyList(),
                                           emptyList());

    assertThat(token, equalTo(integer(1)));
  }

  @Test
  public void boolTrue() {
    RedisToken token = interpreter.execute(safeString("#true"),
                                           emptyList(),
                                           emptyList());

    assertThat(token, equalTo(integer(1)));
  }

  @Test
  public void boolFalse() {
    RedisToken token = interpreter.execute(safeString("#false"),
                                           emptyList(),
                                           emptyList());

    assertThat(token, equalTo(nullString()));
  }

  @Test
  public void ping() {
    when(redis.call(safeString("ping"))).thenReturn(status("PONG"));

    RedisToken token = interpreter.execute(safeString("(call-redis \"ping\")"),
                                           emptyList(),
                                           emptyList());

    assertThat(token, equalTo(status("PONG")));
  }

  @Test
  public void echo() {
    when(redis.call(safeString("echo"), safeString("hello"))).thenReturn(string("hello"));

    RedisToken token = interpreter.execute(safeString("(call-redis \"echo\" \"hello\")"),
                                           emptyList(),
                                           emptyList());

    assertThat(token, equalTo(string("hello")));
  }

  @Test
  public void pcall() {
    when(redis.pcall(safeString("echo"), safeString("hello"))).thenReturn(error("message"));

    RedisToken token = interpreter.execute(safeString("(pcall-redis \"echo\" \"hello\")"),
                                           emptyList(),
                                           emptyList());

    assertThat(token, equalTo(error("message")));
  }
}
