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

final class NbtStringImpl extends NbtValueImpl implements NbtString {

	private final String value;

	NbtStringImpl(String value) {

		this(null, value);
	}

	NbtStringImpl(String name, String value) {

		super(name);
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {

		if(!(obj instanceof NbtString)) {
			return false;
		}
		if(!super.equals(obj)) {
			return false;
		}
		NbtString other = (NbtString)obj;
		return value.equals(other.getString());
	}

	@Override
	public String getString() {

		return value;
	}

	@Override
	public int hashCode() {

		return value.hashCode();
	}

	@Override
	public String toString() {

		return "TAG_String(\'" + getName() + "\'): \'" + getString() + '\'';
	}
}
