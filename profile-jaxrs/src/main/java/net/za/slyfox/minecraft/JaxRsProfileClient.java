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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

/**
 * Mojang Profile API client implementation based on the JAX-RS Client API.
 */
public class JaxRsProfileClient extends AbstractProfileClient {

	private static final GenericType<List<Profile>> PROFILE_LIST_TYPE = new GenericType<List<Profile>>() { };
	private static final UriBuilder sessionProfileUriBuilder = UriBuilder.fromUri(SESSION_PROFILE_URI_TEMPLATE);

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
	protected List<Profile> doRetrieveProfilesForNames(String[] profileNames) {

		return client.target(PROFILES_URI)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.json(profileNames), PROFILE_LIST_TYPE);
	}

	@Override
	public SessionProfile retrieveProfileForUuid(String uuid) throws IOException, RateLimitedException {

		try {
			return client.target(sessionProfileUriBuilder.build(uuid))
					.request(MediaType.APPLICATION_JSON_TYPE)
					.get(SessionProfile.class);
		} catch(WebApplicationException e) {
			Response response = e.getResponse();
			if(response.getStatus() != 429) {
				throw e;
			}
			throw new RateLimitedException();
		}
	}

	/**
	 * Sets the JAX-RS client this instance must use when executing remote requests.
	 */
	public void setClient(Client client) {

		this.client = client;
	}
}
