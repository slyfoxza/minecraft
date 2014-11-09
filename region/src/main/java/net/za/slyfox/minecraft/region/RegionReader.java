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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.za.slyfox.minecraft.nbt.NbtCompound;
import net.za.slyfox.minecraft.nbt.NbtReader;
import net.za.slyfox.minecraft.nbt.spi.NbtProvider;

public class RegionReader {

	private final ChunkDataHeaderReader chunkDataHeaderReader;
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final RegionHeaderReader regionHeaderReader;

	@Inject
	public RegionReader(ChunkDataHeaderReader chunkDataHeaderReader, RegionHeaderReader regionHeaderReader) {

		this.chunkDataHeaderReader = chunkDataHeaderReader;
		this.regionHeaderReader = regionHeaderReader;
	}

	public Region read(ByteBuffer buffer) {

		Region region = new Region();
		RegionHeader regionHeader = regionHeaderReader.read(buffer);
		region.setRegionHeader(regionHeader);
		return region;
	}

	public NbtCompound readChunk(ByteBuffer buffer, Region region, int x, int z) throws IOException {

		buffer.position((int)region.getRegionHeader().getOffset(x, z));
		ChunkDataHeader chunkDataHeader = chunkDataHeaderReader.read(buffer);

		ByteBuffer chunkBuffer = buffer.duplicate();
		chunkBuffer.limit(chunkBuffer.position() + chunkDataHeader.getSize());
		InputStream inputStream = new ByteBufferInputStream(chunkBuffer);
		switch(chunkDataHeader.getCompressionType()) {
			case GZIP:
				log.debug("Using GZIP compression");
				inputStream = new GZIPInputStream(inputStream);
				break;

			case ZLIB:
				log.debug("Using ZLIB compression");
				inputStream = new InflaterInputStream(inputStream);
				break;

			default:
				log.debug("Unknown compression type");
				break;
		}

		NbtReader nbtReader = NbtProvider.provider().createReader(inputStream);
		return nbtReader.read();
	}

	private static class ByteBufferInputStream extends InputStream {

		private final ByteBuffer buffer;

		public ByteBufferInputStream(ByteBuffer buffer) {

			this.buffer = buffer.duplicate();
		}

		@Override
		public int read() throws IOException {

			if(!buffer.hasRemaining()) {
				return -1;
			}

			return buffer.get();
		}

		@Override
		public int read(byte[] b) throws IOException {

			if(b.length == 0) {
				return 0;
			} else if(!buffer.hasRemaining()) {
				return -1;
			}

			int length = Math.min(b.length, buffer.remaining());
			buffer.get(b, 0, length);
			return length;
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {

			if(len == 0) {
				return 0;
			} else if(!buffer.hasRemaining()) {
				return -1;
			}

			int length = Math.min(len, buffer.remaining());
			buffer.get(b, off, length);
			return length;
		}
	}
}
