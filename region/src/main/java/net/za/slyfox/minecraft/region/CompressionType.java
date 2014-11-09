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
package net.za.slyfox.minecraft.region;

public enum CompressionType {

	GZIP(1),
	ZLIB(2),
	UNKNOWN;

	private Byte value;

	private CompressionType() {

		this.value = null;
	}

	private CompressionType(int value) {

		this.value = (byte)value;
	}

	public byte getValue() {

		return value;
	}

	public static CompressionType valueOf(byte value) {

		for(CompressionType enumValue: values()) {
			if(enumValue.value == value) {
				return enumValue;
			}
		}

		return UNKNOWN;
	}
}
