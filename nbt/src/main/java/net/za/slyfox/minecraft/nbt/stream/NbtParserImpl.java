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
import java.io.InputStream;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.za.slyfox.minecraft.nbt.NbtException;

public class NbtParserImpl implements NbtParser {

	private final Queue<Action> actions = new LinkedList<>();
	private ActionResult actionResult;
	private final NbtLocationImpl location = new NbtLocationImpl();
	private final Logger log = LoggerFactory.getLogger(getClass());

	public NbtParserImpl(InputStream inputStream) {

		DataInputStream dataInputStream = new DataInputStream(inputStream);
		actions.add(new ReadTagTypeAction(dataInputStream));
		actions.add(new ReadStringAction(dataInputStream, true));
		actions.add(new ParseCompoundAction(dataInputStream));
	}

	@Override
	public void close() throws IOException {

	}

	@Override
	public NbtLocation getLocation() {

		Action action = actions.peek();
		if(action instanceof ParseCompoundAction) {
			location.setDepth(((ParseCompoundAction)action).getDepth());
		} else {
			location.setDepth(0);
		}
		return location;
	}

	@Override
	public Number getNumber() {

		return actionResult.getNumber();
	}

	@Override
	public String getString() {

		return actionResult.getString();
	}

	@Override
	public NbtTagType getTagType() {

		return actionResult.getTagType();
	}

	@Override
	public boolean hasNext() {

		return !actions.isEmpty() && !actions.peek().isComplete();
	}

	@Override
	public Event next() {

		if(!hasNext()) {
			throw new NoSuchElementException();
		}
		Action action = actions.peek();
		try {
			actionResult = action.execute();
		} catch(IOException e) {
			throw new NbtException("I/O error", e);
		}
		if(action.isComplete()) {
			actions.remove();
		}
		return actionResult.getEvent();
	}
}
