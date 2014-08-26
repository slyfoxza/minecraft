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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

/**
 * Mojang Profile API client implementation based on the JAX-RS Client API.
 */
public class JaxRsProfileClient implements ProfileClient {

	private static final GenericType<List<Profile>> PROFILE_LIST_TYPE
			= new GenericType<List<Profile>>() { };
	private static final URI PROFILES_URI = URI.create("https://api.mojang.com/profiles/minecraft");
	private static final UriBuilder sessionProfileUriBuilder
			= UriBuilder.fromUri("https://sessionserver.mojang.com/session/minecraft/profile/{uuid}");

	private Client client;

	/**
	 * Constructs an uninitialised {@code JaxRsProfileClient}. This constructor is primarily for use
	 * in dependency injection frameworks that use setters to inject dependencies.
	 *
	 * @see #JaxRsProfileClient(Client)
	 */
	public JaxRsProfileClient() { }

	/**
	 * Constructs a {@code JaxRsProfileClient}, initialising it with the given JAX-RS client.

	 * @param client the JAX-RS client this instance must use
	 * @see #setClient
	 */
	public JaxRsProfileClient(Client client) {

		setClient(client);
	}

	@Override
	public List<Profile> retrieveProfilesForNames(String... profileNames) throws IOException {

		if(profileNames.length == 0) {
			return Collections.emptyList();
		}

		List<Profile> profiles = new ArrayList<>(profileNames.length);
		// Execute requests in blocks of up to 100 to adhere to the server-side per-request limit
		int from = 0;
		while(from < profileNames.length) {
			int to = Math.min(profileNames.length, from + 100);
			String[] requestProfileNames = Arrays.copyOfRange(profileNames, from, to);
			List<Profile> responseProfiles = client.target(PROFILES_URI)
					.request(MediaType.APPLICATION_JSON_TYPE)
					.post(Entity.json(requestProfileNames), PROFILE_LIST_TYPE);
			// Add all results for this iteration to the final result list
			profiles.addAll(responseProfiles);
			from = to;
		}
		return profiles;
	}

	@Override
	public SessionProfile retrieveProfileForUuid(String uuid) throws IOException {

		return client.target(sessionProfileUriBuilder.build(uuid))
				.request(MediaType.APPLICATION_JSON_TYPE)
				.get(SessionProfile.class);
	}

	/**
	 * Sets the JAX-RS client this instance must use when executing remote requests.
	 */
	public void setClient(Client client) {

		this.client = client;
	}
}
