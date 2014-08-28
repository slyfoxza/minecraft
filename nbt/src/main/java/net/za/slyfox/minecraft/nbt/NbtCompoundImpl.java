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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class NbtCompoundImpl extends NbtValueImpl implements NbtCompound {

	private Map<String, NbtValue> values = new HashMap<>();

	NbtCompoundImpl() {

		this(null);
	}

	NbtCompoundImpl(String name) {

		super(name);
	}

	@Override
	public void clear() {

		values.clear();
	}

	@Override
	public boolean containsKey(Object key) {

		return values.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {

		return values.containsValue(value);
	}

	@Override
	public Set<Entry<String, NbtValue>> entrySet() {

		return values.entrySet();
	}

	@Override
	public Set<String> keySet() {

		return values.keySet();
	}

	@Override
	public boolean isEmpty() {

		return values.isEmpty();
	}

	@Override
	public NbtValue put(String key, NbtValue value) {

		set(key, value);
		return value;
	}

	@Override
	public void putAll(Map<? extends String, ? extends NbtValue> m) {

		values.putAll(m);
	}

	@Override
	public NbtValue remove(Object key) {

		return values.remove(key);
	}

	@Override
	public boolean remove(Object key, Object value) {

		return values.remove(key, value);
	}

	@Override
	public int size() {

		return values.size();
	}

	@Override
	public Collection<NbtValue> values() {

		return values.values();
	}

	@Override
	public NbtValue get(Object key) {

		return values.get(key);
	}

	public void set(String name, NbtValue value) {

		values.put(name, value);
	}

	@Override
	public NbtCompound getCompound(String name) {

		return (NbtCompound)values.get(name);
	}

	public void setCompound(String name, NbtCompound compound) {

		values.put(name, compound);
	}

	@Override
	public float getFloat(String name) {

		return ((NbtNumber)values.get(name)).floatValue();
	}

	@Override
	public String getString(String name) {

		return ((NbtString)values.get(name)).getString();
	}

	public void setString(String name, String value) {

		values.put(name, new NbtStringImpl(name, value));
	}
}
