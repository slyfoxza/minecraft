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
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public abstract class HelloWorldNbtReaderTest extends AbstractNbtReaderTest {

	private NbtCompound nbt;

	@Before
	public void readNbt() {

		nbt = createReader(HelloWorldNbtReaderTest.class.getResourceAsStream("hello_world.nbt"))
				.read();
	}

	@Test
	public void rootCompoundName() {

		assertThat(nbt.getName(), is("hello world"));
	}

	@Test
	public void nameValue() {

		assertThat(nbt.getString("name"), is("Bananrama"));
	}
}