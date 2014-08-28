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
import java.util.LinkedList;
import java.util.Queue;

import net.za.slyfox.minecraft.nbt.stream.NbtParser.Event;

class ParseArrayAction extends AbstractContainerAction {

	private final Queue<Action> actions = new LinkedList<>();
	private final Action numberAction;

	public ParseArrayAction(DataInputStream inputStream, NbtTagType tagType) {

		super(inputStream);
		restart();
		switch(tagType) {
			case BYTE_ARRAY:
				numberAction = createContentAction(inputStream, NbtTagType.BYTE);
				break;

			case INT_ARRAY:
				numberAction = createContentAction(inputStream, NbtTagType.INT);
				break;

			default:
				throw new UnsupportedOperationException("Unsupported tag type in array: "
						+ tagType);
		}
	}

	@Override
	public ActionResult execute() throws IOException {

		Action action = actions.peek();
		ActionResult result = action.execute();
		if(action.isComplete()) {
			actions.remove();
		}
		if(action instanceof ReadNumberAction) {
			int count = result.getNumber().intValue();
			if(count != 0) {
				actions.add(new RepeatedAction(count, numberAction));
			} else {
				setComplete();
			}
		} else if(action.isComplete()) {
			setComplete();
		}
		return result;
	}

	@Override
	public int getDepth() {

		return 0;
	}

	@Override
	public void restart() {

		super.restart();
		actions.add(new ReadNumberAction(inputStream, NbtTagType.INT, Event.ARRAY_SIZE));
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder("parse ");
		NbtTagType tagType = ((ReadNumberAction)numberAction).getTagType();
		if(tagType == NbtTagType.BYTE) {
			builder.append("byte ");
		} else if(tagType == NbtTagType.INT) {
			builder.append("integer ");
		}
		return builder.append("array action").toString();
	}
}
