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

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.za.slyfox.minecraft.nbt.stream.NbtParser;
import net.za.slyfox.minecraft.nbt.stream.NbtParser.Event;
import net.za.slyfox.minecraft.nbt.stream.NbtParserImpl;
import net.za.slyfox.minecraft.nbt.stream.NbtTagType;

public class NbtReaderImpl implements NbtReader {

	private final Logger log = LoggerFactory.getLogger(NbtReaderImpl.class);
	private NbtParser parser;

	public NbtReaderImpl(InputStream inputStream) {

		parser = new NbtParserImpl(inputStream);
	}

	@Override
	public void close() throws IOException {

		parser.close();
	}

	@Override
	public NbtCompound read() {

		Event event = parser.next();
		if(event != Event.TAG_ID) {
			throw new NbtException("Expected TAG_ID");
		}
		if(parser.getTagType() != NbtTagType.COMPOUND) {
			throw new NbtException("Expected COMPOUND");
		}
		event = parser.next();
		if(event != Event.TAG_NAME) {
			throw new NbtException("Expected TAG_NAME");
		}
		NbtCompoundImpl compound = new NbtCompoundImpl(parser.getString());
		readCompound(compound);
		return compound;
	}

	private void readCompound(NbtCompoundImpl compound) {

		log.debug("Reading compound <{}> children", compound.getName());
		final int depth = parser.getLocation().getDepth();
		while(parser.getLocation().getDepth() >= depth) {
			parser.next();
			NbtTagType tagType = parser.getTagType();
			if(tagType == NbtTagType.END) {
				break;
			}
			parser.next();
			String name = parser.getString();
			if(tagType == NbtTagType.COMPOUND) {
				NbtCompoundImpl childCompound = new NbtCompoundImpl(name);
				compound.setCompound(name, childCompound);
				readCompound(childCompound);
			} else if(tagType == NbtTagType.LIST) {
				if(parser.next() != Event.LIST_TAG_ID) {
					throw new NbtException("Expected LIST_TAG_ID");
				}
				NbtTagType elementTagType = parser.getTagType();
				if(parser.next() != Event.ARRAY_SIZE) {
					throw new NbtException("Expected ARRAY_SIZE");
				}
				int size = parser.getNumber().intValue();
				NbtListImpl<NbtValue> list = new NbtListImpl<>(name);
				log.debug("Reading {} {} elements for list <{}>", size, elementTagType, list.getName());
				for(int i = 0; i < size; ++i) {
					switch(elementTagType) {
						case BYTE:
						case DOUBLE:
						case FLOAT:
						case INT:
						case LONG:
						case SHORT:
							parser.next();
							list.add(new NbtNumberImpl(parser.getNumber()));
							break;

						case INT_ARRAY:
							NbtIntArrayImpl array = readIntArray(null);
							list.add(array);
							break;

						case COMPOUND:
							NbtCompoundImpl childCompound = new NbtCompoundImpl();
							readCompound(childCompound);
							list.add(childCompound);
							break;

						default:
							throw new IllegalStateException("Fucked: " + elementTagType);
					}
				}
				compound.set(name, list);
			} else if(tagType == NbtTagType.BYTE_ARRAY) {
				if(parser.next() != Event.ARRAY_SIZE) {
					throw new NbtException("Expected ARRAY_SIZE");
				}
				int size = parser.getNumber().intValue();
				byte[] array = new byte[size];
				log.debug("Reading {} elements for byte array <{}>", size, name);
				for(int i = 0; i < size; ++i) {
					parser.next();
					array[i] = parser.getNumber().byteValue();
				}
				compound.set(name, new NbtByteArrayImpl(name, array));
			} else if(tagType == NbtTagType.INT_ARRAY) {
				NbtIntArrayImpl array = readIntArray(name);
				compound.set(name, array);
			} else {
				Event event = parser.next();
				if(event == Event.VALUE_STRING) {
					log.trace("Reading string value for <{}>", name);
					compound.setString(name, parser.getString());
				} else if(event == Event.VALUE_NUMBER) {
					log.debug("Reading numeric value for <{}>", name);
					compound.set(name, new NbtNumberImpl(name, parser.getNumber()));
				}
			}
		}
	}

	private NbtIntArrayImpl readIntArray(String name) {

		Event event = parser.next();
		if(event != Event.ARRAY_SIZE) {
			throw new NbtException("Expected ARRAY_SIZE, was " + event);
		}
		int size = parser.getNumber().intValue();
		int[] array = new int[size];
		log.debug("Reading {} elements for integer array <{}>", size, name);
		for(int i = 0; i < size; ++i) {
			parser.next();
			array[i] = parser.getNumber().intValue();
		}
		return new NbtIntArrayImpl(name, array);
	}
}
