/*
 * Copyright 2014 Philip Cronje
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package net.za.slyfox.minecraft.nbt.stream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.za.slyfox.minecraft.nbt.stream.NbtParser.Event;

public abstract class HelloWorldNbtParserTest extends AbstractNbtParserTest {

	@Test
	public void parseNbt() {

		NbtParser parser = createParser(HelloWorldNbtParserTest.class.getResourceAsStream(
				"/net/za/slyfox/minecraft/nbt/hello_world.nbt"));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.COMPOUND));

		assertThat(parser.next(), is(Event.TAG_NAME));
		assertThat(parser.getString(), is("hello world"));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.STRING));

		assertThat(parser.next(), is(Event.TAG_NAME));
		assertThat(parser.getString(), is("name"));

		assertThat(parser.next(), is(Event.VALUE_STRING));
		assertThat(parser.getString(), is("Bananrama"));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.END));

		assertThat(parser.hasNext(), is(false));
	}
}
