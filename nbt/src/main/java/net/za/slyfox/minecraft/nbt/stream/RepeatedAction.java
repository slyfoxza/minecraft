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

import java.io.IOException;

final class RepeatedAction extends Action implements DelegatingAction {

	private final Action action;
	private int count;

	public RepeatedAction(int count, Action action) {

		super(null);
		this.action = action;
		this.count = count;
	}

	@Override
	public ActionResult execute() throws IOException {

		log.trace("Executing repeated action: {} ({} remaining, inclusive)", action, count);
		if(count == 0) {
			throw new IllegalStateException("Repeated action called with 0 count remaining");
		}
		ActionResult result = action.execute();
		if(action.isComplete()) {
			if(--count == 0) {
				setComplete();
			} else {
				action.restart();
			}
		}
		return result;
	}

	@Override
	public int getDepth() {

		if(action instanceof DelegatingAction) {
			return ((DelegatingAction)action).getDepth();
		} else {
			return 0;
		}
	}

	@Override
	public String toString() {

		return "repeated action: " + action + " (" + count + " remaining)";
	}
}
