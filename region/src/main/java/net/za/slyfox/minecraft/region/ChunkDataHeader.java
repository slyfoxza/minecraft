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

public class ChunkDataHeader {

	private final CompressionType compressionType;
	private final int size;

	public ChunkDataHeader(int size, CompressionType compressionType) {

		this.compressionType = compressionType;
		this.size = size;
	}

	public CompressionType getCompressionType() {

		return compressionType;
	}

	public int getSize() {

		return size;
	}
}
