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
package net.za.slyfox.minecraft.nbt.spi;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import net.za.slyfox.minecraft.nbt.NbtException;
import net.za.slyfox.minecraft.nbt.NbtReader;
import net.za.slyfox.minecraft.nbt.NbtReaderFactory;
import net.za.slyfox.minecraft.nbt.NbtWriter;
import net.za.slyfox.minecraft.nbt.NbtWriterFactory;

public abstract class NbtProvider {

	private static final String DEFAULT_PROVIDER
			= "net.za.slyfox.minecraft.nbt.spi.DefaultNbtProvider";

	protected NbtProvider() { }

	public static NbtProvider provider() {

		ServiceLoader<NbtProvider> loader = ServiceLoader.load(NbtProvider.class);
		Iterator<NbtProvider> iterator = loader.iterator();
		if(iterator.hasNext()) {
			return iterator.next();
		}

		try {
			Class<?> clazz = Class.forName(DEFAULT_PROVIDER);
			return (NbtProvider)clazz.newInstance();
		} catch(ClassNotFoundException e) {
			throw new NbtException("Provider " + DEFAULT_PROVIDER + " not found", e);
		} catch(Exception e) {
			throw new NbtException("Provider " + DEFAULT_PROVIDER + " could not be instantiated: "
					+ e, e);
		}
	}

	public abstract NbtReader createReader(InputStream inputStream);
	public abstract NbtReaderFactory createReaderFactory(Map<String, ?> configuration);
	public abstract NbtWriter createWriter(OutputStream outputStream);
	public abstract NbtWriterFactory createWriterFactory(Map<String, ?> configuration);
}
