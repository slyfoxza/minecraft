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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;

public class Jackson2SessionProfileHttpMessageConverterTest {

	private Jackson2SessionProfileHttpMessageConverter messageConverter;

	@Before
	public void createMessageConverter() {

		messageConverter = new Jackson2SessionProfileHttpMessageConverter();
	}

	@Test
	public void readReturnsValidSessionProfile() throws Exception {

		HttpInputMessage inputMessage = mock(HttpInputMessage.class);
		when(inputMessage.getBody()).thenReturn(getClass().getResourceAsStream("slyfoxza-uuid.json"));

		SessionProfile result = messageConverter.read(SessionProfile.class, inputMessage);

		assertThat(result.name, is("slyfoxza"));
		assertThat(result.uuid, is("90447c78d23d4877903a1ce4b7063951"));
		assertThat(result.capeTextureUri, is(nullValue(URI.class)));
		assertThat(result.skinTextureUri, is(URI.create("http://textures.minecraft.net/texture/"
				+ "66fe51766517f3d01cfdb7242eb5f34aea9628a166e3e40faf4c1321696")));
	}

	@Test
	public void jsonIsDefaultSupportedMediaType() {

		assertThat(messageConverter.getSupportedMediaTypes(), contains(MediaType.APPLICATION_JSON));
	}

	@Test
	public void canReadSessionProfile() {

		boolean result = messageConverter.canRead(SessionProfile.class, null);
		assertThat(result, is(true));
	}

	@Test
	public void cannotWrite() {

		boolean result = messageConverter.canWrite(SessionProfile.class, null);
		assertThat(result, is(false));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void writingIsUnsupported() throws Exception {

		HttpOutputMessage outputMessage = mock(HttpOutputMessage.class);
		HttpHeaders headers = new HttpHeaders();
		when(outputMessage.getHeaders()).thenReturn(headers);

		messageConverter.write(new SessionProfile(), null, outputMessage);
	}
}
