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
package net.za.slyfox.minecraft

import net.za.slyfox.minecraft.nbt.NbtReader
import net.za.slyfox.minecraft.nbt.spi.NbtProvider
import org.slf4j.LoggerFactory

import java.util.zip.GZIPInputStream

class Application {

	static void main(arguments) {

		def log = LoggerFactory.getLogger(Application.class)
		CliBuilder cliBuilder = new CliBuilder(
				usage: 'lsnbt [options] [files...]'
		)
		cliBuilder.u('read uncompressed NBTs')
		def options = cliBuilder.parse(arguments)

		options.arguments().each { argument ->
			log.info('Reading {}', argument)
			new File(argument).withInputStream { inputStream ->
				if(!options.u) {
					inputStream = new GZIPInputStream(inputStream)
				}
				NbtReader reader = NbtProvider.provider().createReader(inputStream)
				def root = reader.read()
				def nbtPrinter = new NbtPrinter()
				try {
					nbtPrinter.print(root)
				} finally {
					nbtPrinter.flush()
				}
			}
		}
	}
}
