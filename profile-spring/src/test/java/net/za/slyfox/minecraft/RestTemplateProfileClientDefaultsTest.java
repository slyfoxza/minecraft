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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Tests the initialisation of {@link RestTemplateProfileClient} instances when created using the no-arg constructor.
 */
public class RestTemplateProfileClientDefaultsTest {

	private RestTemplateProfileClient profileClient;

	@Before
	public void createProfileClient() {

		profileClient = new RestTemplateProfileClient();
	}

	@Test
	public void restTemplateIsNotNull() {

		assertThat(profileClient.getRestTemplate(), is(notNullValue(RestTemplate.class)));
	}

	@Test
	public void sessionProfileMessageConverterIsFirst() {

		List<HttpMessageConverter<?>> messageConverters = profileClient.getRestTemplate().getMessageConverters();
		assertThat(messageConverters.get(0), is(instanceOf(Jackson2SessionProfileHttpMessageConverter.class)));
	}
}
