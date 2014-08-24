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

import java.io.IOException;
import java.net.SocketAddress;

/**
 * Minecraft query protocol client interface. Implementations of this interface are synchronous.
 */
public interface MinecraftQueryClient {

	/**
	 * Requests the basic status from a Minecraft server.
	 *
	 * @param address the address of the query service
	 * @return the basic status received from the server
	 * @throws IOException when an I/O error occurs
	 */
	BasicStatus queryBasicStatus(SocketAddress address) throws IOException;

	/**
	 * Requests the full status from a Minecraft server.
	 *
	 * @param address the address of the query service
	 * @return the full status received from the server
	 * @throws IOException when an I/O error occurs
	 */
	FullStatus queryFullStatus(SocketAddress address) throws IOException;
}
