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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ClassUtils;
import org.springframework.web.client.RestTemplate;

/**
 * Mojang Profile API client implementation based on Spring's {@link RestTemplate} class.
 */
public class RestTemplateProfileClient extends AbstractProfileClient {

	private static final boolean JACKSON2_PRESENT = ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper",
			RestTemplateProfileClient.class.getClassLoader())
			&& ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator",
			RestTemplateProfileClient.class.getClassLoader());
	private static final ParameterizedTypeReference<List<Profile>> PROFILE_LIST_TYPE
			= new ParameterizedTypeReference<List<Profile>>() { };

	private final HttpHeaders requestHeaders = new HttpHeaders();
	private RestTemplate restTemplate;

	/**
	 * Constructs a new {@code RestTemplateProfileClient}. The instance will be initialised with a default {@link
	 * RestTemplate#RestTemplate() RestTemplate}. If Jackson 2 is present on the classpath, a {@link
	 * Jackson2SessionProfileHttpMessageConverter} will be added at the beginning of the {@code RestTemplate}'s list of
	 * message converters.
	 */
	public RestTemplateProfileClient() {

		this(new RestTemplate());
		if(JACKSON2_PRESENT) {
			restTemplate.getMessageConverters().add(0, new Jackson2SessionProfileHttpMessageConverter());
		}
	}

	/**
	 * Constructs a new {@code RestTemplateProfileClient}, initialising it with the given {@code restTemplate}. Unlike
	 * {@link #RestTemplateProfileClient()}, it is assumed that {@code restTemplate} is fully initialised, and does not
	 * update its list of message converters.
	 */
	public RestTemplateProfileClient(RestTemplate restTemplate) {

		this.restTemplate = restTemplate;

		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
	}

	@Override
	protected List<Profile> doRetrieveProfilesForNames(String[] profileNames) throws IOException {

		HttpEntity<List<String>> requestEntity = new HttpEntity<List<String>>(Arrays.asList(profileNames),
				requestHeaders);
		ResponseEntity<List<Profile>> profiles = restTemplate.exchange(PROFILES_URI, HttpMethod.POST, requestEntity,
				PROFILE_LIST_TYPE);
		return profiles.getBody();
	}

	@Override
	public SessionProfile retrieveProfileForUuid(String uuid) throws IOException {

		return restTemplate.getForObject(SESSION_PROFILE_URI_TEMPLATE, SessionProfile.class, uuid);
	}

	/**
	 * Returns the {@code RestTemplate} this instance uses to execute remote requests.
	 */
	public RestTemplate getRestTemplate() {

		return restTemplate;
	}

	/**
	 * Sets the {@code RestTemplate} instance that must be used to execute remote requests.
	 */
	@Resource
	public void setRestTemplate(RestTemplate restTemplate) {

		this.restTemplate = restTemplate;
	}
}
