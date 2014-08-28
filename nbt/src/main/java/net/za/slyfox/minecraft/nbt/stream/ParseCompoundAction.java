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

class ParseCompoundAction extends AbstractContainerAction {

	private final Queue<Action> actions = new LinkedList<>();

	public ParseCompoundAction(DataInputStream inputStream) {

		super(inputStream);
		restart();
	}

	@Override
	public ActionResult execute() throws IOException {

		Action action = actions.peek();
		ActionResult result = action.execute();
		if(action.isComplete()) {
			actions.remove();
		}
		if(action instanceof ReadTagTypeAction) {
			NbtTagType tagType = result.getTagType();
			if(tagType == NbtTagType.END) {
				log.trace("Reached end of compound; marking action complete");
				setComplete();
			} else {
				actions.add(new ReadStringAction(inputStream, true));
				actions.add(createContentAction(inputStream, tagType));
				actions.add(new ReadTagTypeAction(inputStream));
			}
		}
		return result;
	}

	@Override
	public int getDepth() {

		int depth = 1;
		Action action = actions.peek();
		if((action != null) && (action instanceof DelegatingAction)) {
			depth += ((DelegatingAction)action).getDepth();
		}
		return depth;
	}

	@Override
	public void restart() {

		super.restart();
		actions.add(new ReadTagTypeAction(inputStream));
	}

	@Override
	public String toString() {

		return "parse compound action";
	}
}
