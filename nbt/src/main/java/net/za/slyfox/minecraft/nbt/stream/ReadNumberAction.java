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

import java.io.DataInputStream;
import java.io.IOException;

import net.za.slyfox.minecraft.nbt.stream.NbtParser.Event;

class ReadNumberAction extends Action {

	private final Event event;
	private final NbtTagType tagType;

	public ReadNumberAction(DataInputStream inputStream, NbtTagType tagType) {

		this(inputStream, tagType, Event.VALUE_NUMBER);
	}

	public ReadNumberAction(DataInputStream inputStream, NbtTagType tagType, Event event) {

		super(inputStream);
		this.event = event;
		this.tagType = tagType;
	}

	@Override
	public ActionResult execute() throws IOException {

		try {
			final Number number;
			switch(tagType) {
				case BYTE: number = inputStream.readByte(); break;
				case DOUBLE: number = inputStream.readDouble(); break;
				case FLOAT: number = inputStream.readFloat(); break;
				case INT: number = inputStream.readInt(); break;
				case LONG: number = inputStream.readLong(); break;
				case SHORT: number = inputStream.readShort(); break;

				default:
					throw new UnsupportedOperationException("Unknown tag type " + tagType);
			}
			return new ActionResult(event) {

				@Override
				public Number getNumber() {

					return number;
				}
			};
		} finally {
			setComplete();
		}
	}

	public NbtTagType getTagType() {

		return tagType;
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder("read number action");
		if(event == Event.ARRAY_SIZE) {
			builder.append(" (array size)");
		}
		return builder.toString();
	}
}
