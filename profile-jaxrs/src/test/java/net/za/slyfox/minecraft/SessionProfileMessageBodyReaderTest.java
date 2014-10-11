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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.lang.annotation.Annotation;
import java.net.URI;
import javax.json.Json;
import javax.json.JsonReaderFactory;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.NoContentException;

import org.junit.Before;
import org.junit.Test;

public class SessionProfileMessageBodyReaderTest {

	private SessionProfileMessageBodyReader reader;

	@Before
	public void createReader() {

		JsonReaderFactory jsonReaderFactory = Json.createReaderFactory(null);
		reader = new SessionProfileMessageBodyReader();
		reader.setJsonReaderFactory(jsonReaderFactory);
	}

	@Test
	public void thinkofdeathTestCase() throws Exception {

		SessionProfile sessionProfile = reader.readFrom(SessionProfile.class, SessionProfile.class,
				new Annotation[0], MediaType.APPLICATION_JSON_TYPE,
				new MultivaluedHashMap<String, String>(),
				getClass().getResourceAsStream("thinkofdeath.json"));

		assertThat(sessionProfile, is(notNullValue()));
		assertThat(sessionProfile.uuid, is("4566e69fc90748ee8d71d7ba5aa00d20"));
		assertThat(sessionProfile.name, is("thinkofdeath"));
		assertThat(sessionProfile.skinTextureUri,
				is(URI.create("http://textures.minecraft.net/texture/"
						+ "13e81b9e19ab1ef17a90c0aa4e1085fc13cd47ced5a7a1a492803b3561e4a15b")));
		assertThat(sessionProfile.capeTextureUri, is(nullValue()));
	}

	@Test(expected = NoContentException.class)
	public void noContent() throws Exception {

		reader.readFrom(SessionProfile.class, SessionProfile.class, new Annotation[0],
				MediaType.APPLICATION_JSON_TYPE, new MultivaluedHashMap<String, String>(),
				new ByteArrayInputStream(new byte[0]));
	}
}
