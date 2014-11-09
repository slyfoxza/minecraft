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

/**
 * Contains the information stored in a region file's header.
 */
public class RegionHeader {

	private final long offsets[] = new long[1024];
	private final int sizes[] = new int[1024];
	private final int timestamps[] = new int[1024];

	public long getOffset(int x, int z) {

		ensureBounds(x, z);
		return offsets[getChunkIndex(x, z)];
	}

	public void setOffset(int i, long offset) {

		offsets[i] = offset;
	}

	public int getSize(int x, int z) {

		ensureBounds(x, z);
		return sizes[getChunkIndex(x, z)];
	}

	public void setSize(int i, int size) {

		sizes[i] = size;
	}

	public int getTimestamp(int x, int z) {

		ensureBounds(x, z);
		return timestamps[getChunkIndex(x, z)];
	}

	public void setTimestamp(int i, int timestamp) {

		timestamps[i] = timestamp;
	}

	private void ensureBounds(int x, int z) {

		if((x < 0) || (x >= 32)) {
			throw new IndexOutOfBoundsException("x value " + x + " is out of range [0,32)");
		} else if((z < 0) || (z >= 32)) {
			throw new IndexOutOfBoundsException("z value " + z + " is out of range [0,32)");
		}
	}

	private int getChunkIndex(int x, int z) {

		final int rx = x >> 5;
		final int rz = z >> 5;

		final int cx = x - (rx << 5);
		final int cz = z - (rz << 5);

		return cx + (cz << 5);
	}
}
