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
import java.util.List;

/**
 * Mojang Profile API client interface. Implementations of this interface are synchronous.
 */
public interface ProfileClient {

	/**
	 * Retrieves profile information for each requested profile name.
	 *
	 * <p>Calling this method may cause multiple API requests to be issued to circumvent the
	 * per-request profile limit.</p>
	 *
	 * @param profileNames the list of profile names to map to UUIDs
	 * @return a list of profile data structures. Invalid or non-existent profile names will not have a corresponding
	 *         entry in this list
	 * @throws IOException when an I/O error occurs
	 * @throws RuntimeException when an error occurs that the implementation cannot meaningfully map to an {@code
	 *         IOException}
	 */
	List<Profile> retrieveProfilesForNames(String... profileNames) throws IOException;

	/**
	 * Retrieves profile information associated with the given UUID
	 *
	 * @param uuid the UUID to retrieve profile information for
	 * @return profile information for the given UUID. If an empty response is received, {@code
	 *         null} will be returned
	 * @throws IOException when an I/O error occurs
	 * @throws RateLimitedException when the remote service indicates that the client has been rate limited
	 */
	SessionProfile retrieveProfileForUuid(String uuid) throws IOException, RateLimitedException;
}
