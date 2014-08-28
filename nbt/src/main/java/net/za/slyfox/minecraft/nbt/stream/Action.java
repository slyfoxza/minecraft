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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class Action {

	protected final DataInputStream inputStream;
	protected boolean isComplete = false;
	protected final Logger log = LoggerFactory.getLogger(getClass());

	public Action(DataInputStream inputStream) {

		this.inputStream = inputStream;
	}

	public abstract ActionResult execute() throws IOException;

	public final boolean isComplete() {

		return isComplete;
	}

	protected final void setComplete() {

		isComplete = true;
	}

	private void setComplete(boolean isComplete) {

		this.isComplete = isComplete;
	}

	public void restart() {

		log.trace("Restarting action");
		setComplete(false);
	}
}
