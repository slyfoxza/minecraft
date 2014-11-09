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
package net.za.slyfox.minecraft.region

import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

class Application {

	static void main(String[] arguments) {

		Path path = Paths.get(arguments[0]);
		RegionReader regionReader = new RegionReader(new ChunkDataHeaderReader(), new RegionHeaderReader());
		FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ)
		ByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, Files.size(path))
		Region region = regionReader.read(buffer);
		for(int z = 0; z < 32; ++z) {
			for(int x = 0; x < 32; ++x) {
				println("${x},${z}: ${region.regionHeader.getOffset(x, z)} ${region.regionHeader.getSize(x, z)} "
						+ "${region.regionHeader.getTimestamp(x, z)}")
			}
		}

		regionReader.readChunk(buffer, region, 7, 16)
	}
}
