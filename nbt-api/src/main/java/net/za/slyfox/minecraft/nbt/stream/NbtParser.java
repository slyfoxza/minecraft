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

import java.io.Closeable;

public interface NbtParser extends Closeable {

	enum Event {

		/**
		 * Array size value. The position of the parser is after the integer value. The method
		 * {@link #getNumber} returns the size value.
		 */
		ARRAY_SIZE,

		/**
		 * List contents tag type/ID value. The position of the parser is after the ID byte. The
		 * method {@link #getTagType()} returns the type value.
		 */
		LIST_TAG_ID,

		/**
		 * Tag type/ID value. The position of the parser is after the ID byte. The method {@link
		 * #getTagType} returns the type value.
		 */
		TAG_ID,

		/**
		 * Tag name. The position of the parser is after the name string. The method {@link
		 * #getString} returns the string value.
		 */
		TAG_NAME,

		/**
		 * Number value in a numeric tag. The position of the parser is after the number value. The
		 * method {@link #getNumber} returns the number value.
		 */
		VALUE_NUMBER,

		/**
		 * String value in a string tag. The position of the parser is after the string value. The
		 * method {@link #getString} returns the string value.
		 */
		VALUE_STRING
	}

	NbtLocation getLocation();

	/**
	 * Returns a {@code Number} for an array size or numeric value. For number values, the related
	 * tag type indicates which {@code value} method should be called to obtain the value as a
	 * primitive type. Array size values will be returned as an {@link Integer}. This method should
	 * only be called when the parser state is {@link Event#ARRAY_SIZE} or {@link
	 * Event#VALUE_STRING}.
	 *
	 * @return an array size value when the parser state is {@code ARRAY_SIZE}, or a number value
	 *         when the parser state is {@code VALUE_NUMBER}.
	 * @throws IllegalStateException when the parser state is not {@code ARRAY_SIZE} or {@code
	 *         VALUE_NUMBER}
	 */
	Number getNumber();

	/**
	 * Returns a {@code String} for a tag name or string value. This method should only be called
	 * when the parser state is {@link Event#TAG_NAME} or {@link Event#VALUE_STRING}.
	 *
	 * @return a tag name when the parser state is {@link Event#TAG_NAME}
	 * @throws IllegalStateException when the parser state is not {@code TAG_NAME}
	 */
	String getString();

	/**
	 * Returns an NBT tag type as a {@code NbtTagType}. This method should only be called when the
	 * parser state is {@link Event#TAG_ID} or {@link Event#LIST_TAG_ID}.
	 *
	 * @return an {@code NbtTagType} value
	 * @throws IllegalStateException when the parser state is not {@code TAG_ID} or {@code
	 *         LIST_TAG_ID}
	 */
	NbtTagType getTagType();

	boolean hasNext();
	Event next();
}
