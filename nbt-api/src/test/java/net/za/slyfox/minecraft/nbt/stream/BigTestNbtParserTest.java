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

import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.junit.Test;

import net.za.slyfox.minecraft.nbt.stream.NbtParser.Event;

public abstract class BigTestNbtParserTest extends AbstractNbtParserTest {

	@Test
	public void parseNbt() throws IOException {

		NbtParser parser = createParser(new GZIPInputStream(
				BigTestNbtParserTest.class.getResourceAsStream("/net/za/slyfox/minecraft/nbt/bigtest.nbt")));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.COMPOUND));

		assertThat(parser.next(), is(Event.TAG_NAME));
		assertThat(parser.getString(), is("Level"));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.LONG));

		assertThat(parser.next(), is(Event.TAG_NAME));
		assertThat(parser.getString(), is("longTest"));

		assertThat(parser.next(), is(Event.VALUE_NUMBER));
		assertThat(parser.getNumber(), is((Number)9223372036854775807L));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.SHORT));

		assertThat(parser.next(), is(Event.TAG_NAME));
		assertThat(parser.getString(), is("shortTest"));

		assertThat(parser.next(), is(Event.VALUE_NUMBER));
		assertThat(parser.getNumber(), is((Number)((short)32767)));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.STRING));

		assertThat(parser.next(), is(Event.TAG_NAME));
		assertThat(parser.getString(), is("stringTest"));

		assertThat(parser.next(), is(Event.VALUE_STRING));
		assertThat(parser.getString(), is("HELLO WORLD THIS IS A TEST STRING \u00C5\u00C4\u00D6!"));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.FLOAT));

		assertThat(parser.next(), is(Event.TAG_NAME));
		assertThat(parser.getString(), is("floatTest"));

		assertThat(parser.next(), is(Event.VALUE_NUMBER));
		assertThat(parser.getNumber(), is((Number)0.49823147058486938f));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.INT));

		assertThat(parser.next(), is(Event.TAG_NAME));
		assertThat(parser.getString(), is("intTest"));

		assertThat(parser.next(), is(Event.VALUE_NUMBER));
		assertThat(parser.getNumber(), is((Number)2147483647));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.COMPOUND));

		assertThat(parser.next(), is(Event.TAG_NAME));
		assertThat(parser.getString(), is("nested compound test"));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.COMPOUND));

		assertThat(parser.next(), is(Event.TAG_NAME));
		assertThat(parser.getString(), is("ham"));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.STRING));

		assertThat(parser.next(), is(Event.TAG_NAME));
		assertThat(parser.getString(), is("name"));

		assertThat(parser.next(), is(Event.VALUE_STRING));
		assertThat(parser.getString(), is("Hampus"));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.FLOAT));

		assertThat(parser.next(), is(Event.TAG_NAME));
		assertThat(parser.getString(), is("value"));

		assertThat(parser.next(), is(Event.VALUE_NUMBER));
		assertThat(parser.getNumber(), is((Number)0.75f));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.END));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.COMPOUND));

		assertThat(parser.next(), is(Event.TAG_NAME));
		assertThat(parser.getString(), is("egg"));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.STRING));

		assertThat(parser.next(), is(Event.TAG_NAME));
		assertThat(parser.getString(), is("name"));

		assertThat(parser.next(), is(Event.VALUE_STRING));
		assertThat(parser.getString(), is("Eggbert"));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.FLOAT));

		assertThat(parser.next(), is(Event.TAG_NAME));
		assertThat(parser.getString(), is("value"));

		assertThat(parser.next(), is(Event.VALUE_NUMBER));
		assertThat(parser.getNumber(), is((Number)0.5f));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.END));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.END));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.LIST));

		assertThat(parser.next(), is(Event.TAG_NAME));
		assertThat(parser.getString(), is("listTest (long)"));

		assertThat(parser.next(), is(Event.LIST_TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.LONG));

		assertThat(parser.next(), is(Event.ARRAY_SIZE));
		assertThat(parser.getNumber(), is((Number)5));

		for(long i = 11L; i <= 15L; ++i) {
			assertThat(parser.next(), is(Event.VALUE_NUMBER));
			assertThat(parser.getNumber(), is((Number)i));
		}

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.LIST));

		assertThat(parser.next(), is(Event.TAG_NAME));
		assertThat(parser.getString(), is("listTest (compound)"));

		assertThat(parser.next(), is(Event.LIST_TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.COMPOUND));

		assertThat(parser.next(), is(Event.ARRAY_SIZE));
		assertThat(parser.getNumber(), is((Number)2));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.STRING));

		assertThat(parser.next(), is(Event.TAG_NAME));
		assertThat(parser.getString(), is("name"));

		assertThat(parser.next(), is(Event.VALUE_STRING));
		assertThat(parser.getString(), is("Compound tag #0"));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.LONG));

		assertThat(parser.next(), is(Event.TAG_NAME));
		assertThat(parser.getString(), is("created-on"));

		assertThat(parser.next(), is(Event.VALUE_NUMBER));
		assertThat(parser.getNumber(), is((Number)1264099775885L));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.END));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.STRING));

		assertThat(parser.next(), is(Event.TAG_NAME));
		assertThat(parser.getString(), is("name"));

		assertThat(parser.next(), is(Event.VALUE_STRING));
		assertThat(parser.getString(), is("Compound tag #1"));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.LONG));

		assertThat(parser.next(), is(Event.TAG_NAME));
		assertThat(parser.getString(), is("created-on"));

		assertThat(parser.next(), is(Event.VALUE_NUMBER));
		assertThat(parser.getNumber(), is((Number)1264099775885L));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.END));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.BYTE));

		assertThat(parser.next(), is(Event.TAG_NAME));
		assertThat(parser.getString(), is("byteTest"));

		assertThat(parser.next(), is(Event.VALUE_NUMBER));
		assertThat(parser.getNumber(), is((Number)((byte)127)));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.BYTE_ARRAY));

		assertThat(parser.next(), is(Event.TAG_NAME));
		assertThat(parser.getString(), is("byteArrayTest (the first 1000 values of "
				+ "(n*n*255+n*7)%100, starting with n=0 (0, 62, 34, 16, 8, ...))"));

		assertThat(parser.next(), is(Event.ARRAY_SIZE));
		assertThat(parser.getNumber(), is((Number)1000));

		for(int i = 0; i < 1000; ++i) {
			byte expected = (byte)((i * i * 255 + i * 7) % 100);
			assertThat(parser.next(), is(Event.VALUE_NUMBER));
			assertThat(parser.getNumber(), is((Number)expected));
		}

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.DOUBLE));

		assertThat(parser.next(), is(Event.TAG_NAME));
		assertThat(parser.getString(), is("doubleTest"));

		assertThat(parser.next(), is(Event.VALUE_NUMBER));
		assertThat(parser.getNumber(), is((Number)0.49312871321823148));

		assertThat(parser.next(), is(Event.TAG_ID));
		assertThat(parser.getTagType(), is(NbtTagType.END));

		assertThat(parser.hasNext(), is(false));
	}
}
