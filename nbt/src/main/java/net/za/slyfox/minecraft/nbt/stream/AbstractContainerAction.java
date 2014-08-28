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

abstract class AbstractContainerAction extends Action implements DelegatingAction {

	protected AbstractContainerAction(DataInputStream inputStream) {

		super(inputStream);
	}

	protected static Action createContentAction(DataInputStream inputStream, NbtTagType tagType) {

		switch(tagType) {
			case BYTE:
			case DOUBLE:
			case FLOAT:
			case SHORT:
			case INT:
			case LONG:
				return new ReadNumberAction(inputStream, tagType);

			case BYTE_ARRAY:
			case INT_ARRAY:
				return new ParseArrayAction(inputStream, tagType);

			case COMPOUND:
				return new ParseCompoundAction(inputStream);

			case LIST:
				return new ParseListAction(inputStream);

			case STRING:
				return new ReadStringAction(inputStream);

			default:
				throw new UnsupportedOperationException("Unsupported tag type in container: "
						+ tagType);
		}
	}
}
