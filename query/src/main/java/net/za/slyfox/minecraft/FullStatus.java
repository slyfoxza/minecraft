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
package net.za.slyfox.minecraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FullStatus extends BasicStatus {

	public String gameID;
	public String plugins;
	public String version;

	public final Map<String, String> additionalStatus = new HashMap<String, String>();
	public final List<String> onlinePlayers = new ArrayList<String>();

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder(super.toString());
		builder.setLength(builder.length() - 1);
		builder.append(", gameID=").append(gameID);
		builder.append(", plugins=").append(plugins);
		builder.append(", version=").append(version);
		builder.append(", onlinePlayers={");
		for(Iterator<String> iterator = onlinePlayers.iterator(); iterator.hasNext();) {
			builder.append(iterator.next());
			if(iterator.hasNext()) {
				builder.append(", ");
			}
		}
		builder.append('}');
		for(Map.Entry<String, String> entry: additionalStatus.entrySet()) {
			builder.append(", ").append(entry.getKey()).append('=').append(entry.getValue());
		}
		return builder.append(']').toString();
	}
}
