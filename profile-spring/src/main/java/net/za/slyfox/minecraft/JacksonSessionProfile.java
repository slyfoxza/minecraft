/*
 * Copyright 2014 Philip Cronje
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.za.slyfox.minecraft;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;

class JacksonSessionProfile {

	public String id;
	public String name;
	public List<Property> properties;

	@JsonTypeInfo(property = "name", use = Id.NAME)
	@JsonSubTypes(@Type(TexturesProperty.class))
	interface Property {

		public String getName();
		public void setName(String name);
	}

	@JsonTypeName("textures")
	static class TexturesProperty implements Property {

		private String name;
		private byte[] value;

		@Override
		public String getName() {

			return name;
		}

		@Override
		public void setName(String name) {

			this.name = name;
		}

		public byte[] getValue() {

			return value;
		}

		public void setValue(byte[] value) {

			this.value = value;
		}
	}
}
