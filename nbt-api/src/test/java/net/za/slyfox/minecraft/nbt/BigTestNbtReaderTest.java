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
package net.za.slyfox.minecraft.nbt;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.junit.Before;
import org.junit.Test;

public abstract class BigTestNbtReaderTest extends AbstractNbtReaderTest {

	private NbtCompound nbt;

	@Before
	public void readNbt() throws IOException {

		nbt = createReader(new GZIPInputStream(
				BigTestNbtReaderTest.class.getResourceAsStream("bigtest.nbt")))
				.read();
	}

	@Test
	public void rootCompoundName() {

		assertThat(nbt.getName(), is("Level"));
	}

	@Test
	public void nestedCompoundTest() {

		assertThat(nbt.getCompound("nested compound test"), is(notNullValue(NbtCompound.class)));
	}

	@Test
	public void nestedCompoundTestEgg() {

		assertThat(nbt.getCompound("nested compound test").getCompound("egg"),
				is(notNullValue(NbtCompound.class)));
	}

	@Test
	public void nestedCompoundTestEggName() {

		assertThat(nbt.getCompound("nested compound test").getCompound("egg").getString("name"),
				is("Eggbert"));
	}

	@Test
	public void nestedCompoundTestEggValue() {

		assertThat(nbt.getCompound("nested compound test").getCompound("egg").getFloat("value"),
				is(0.5f));
	}
}
