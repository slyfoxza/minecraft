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
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Abstract Mojang Profile API client implementation. Specifically, this implementation provides a default
 * implementation of {@link #retrieveProfilesForNames} that ensures requests are broken up into blocks of no more than
 * 100 profile names.
 */
public abstract class AbstractProfileClient implements ProfileClient {

	/** The URI used by {@link #retrieveProfilesForNames}. */
	protected static final URI PROFILES_URI = URI.create("https://api.mojang.com/profiles/minecraft");
	/**
	 * URI template to generate a final URI in {@link #retrieveProfileForUuid}. The {@code uuid} variable must be
	 * replaced with the UUID being requested, without any dash characters.
	 */
	protected static final String SESSION_PROFILE_URI_TEMPLATE
			= "https://sessionserver.mojang.com/session/minecraft/profile/{uuid}";

	/**
	 * Retrieves profile information for each requested profile name.
	 *
	 * <p>Every 100 profile names will result in a call to {@link #doRetrieveProfilesForNames} to execute the HTTP
	 * request for that subset.</p>
	 */
	@Override
	public List<Profile> retrieveProfilesForNames(String... profileNames) throws IOException {

		final List<Profile> profiles;
		if(profileNames.length == 0) {
			profiles = Collections.emptyList();
		} else {
			profiles = new ArrayList<Profile>(profileNames.length);
		}

		// Execute requests in block of up to 100 to adhere to the server-side per-request limit
		int from = 0;
		while(from < profileNames.length) {
			final int to = Math.min(profileNames.length, from + 100);
			// Add all results for this iteration to the final result list
			profiles.addAll(doRetrieveProfilesForNames(Arrays.copyOfRange(profileNames, from, to)));
			from = to;
		}
		return profiles;
	}

	/**
	 * Subclasses must implement this method to execute the HTTP request to retrieve a list of {@link Profile} objects
	 * for the given list of profile names.
	 *
	 * @param profileNames the list of profile names to map to profile data structures
	 * @return a list of profile data structures, as returned by the remote service
	 * @throws IOException when an I/O error occurs
	 */
	protected abstract List<Profile> doRetrieveProfilesForNames(String[] profileNames) throws IOException;
}
