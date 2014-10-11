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
package net.za.slyfox.minecraft;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRule;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RestTemplateProfileClientTest {

	private static final String DEFAULT_NAME = "player";
	private static final UUID DEFAULT_UUID = new UUID(0L, 0L);
	private static final String DEFAULT_UUID_STRING = DEFAULT_UUID.toString().replace("-", "");

	private RestTemplateProfileClient profileClient;

	@Rule
	public MockitoJUnitRule mockito = new MockitoJUnitRule(this);

	@Mock
	private RestTemplate restTemplate;

	@Before
	public void createProfileClient() {

		profileClient = new RestTemplateProfileClient(restTemplate);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void retrieveProfilesForNames_requestsJsonContentType() throws Exception {

		ResponseEntity<List<Profile>> responseEntity = new ResponseEntity<List<Profile>>(
				Collections.<Profile>emptyList(), HttpStatus.OK);
		when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class),
				any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

		profileClient.retrieveProfilesForNames(DEFAULT_NAME);

		ArgumentCaptor<HttpEntity> argument = ArgumentCaptor.forClass(HttpEntity.class);
		verify(restTemplate).exchange(any(URI.class), any(HttpMethod.class), argument.capture(),
				any(ParameterizedTypeReference.class));
		assertThat(argument.getValue().getHeaders().getContentType(), is(MediaType.APPLICATION_JSON));
	}

	@Test
	public void retrieveProfileForUuid() throws Exception {

		SessionProfile sessionProfile = new SessionProfile();
		when(restTemplate.getForObject("https://sessionserver.mojang.com/session/minecraft/profile/{uuid}",
				SessionProfile.class, DEFAULT_UUID_STRING)).thenReturn(sessionProfile);

		SessionProfile result = profileClient.retrieveProfileForUuid(DEFAULT_UUID_STRING);

		assertThat(result, is(sameInstance(sessionProfile)));
	}
}
