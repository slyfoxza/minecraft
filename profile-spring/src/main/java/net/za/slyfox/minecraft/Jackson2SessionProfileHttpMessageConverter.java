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
import java.net.URI;
import java.net.URISyntaxException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.Assert;

import net.za.slyfox.minecraft.JacksonSessionProfile.Property;
import net.za.slyfox.minecraft.JacksonSessionProfile.TexturesProperty;

/**
 * {@link org.springframework.http.converter.HttpMessageConverter} implementation that uses a
 * <a href="https://github.com/FasterXML/jackson">Jackson 2</a> {@link ObjectMapper} to deserialise the response from
 * the Mojang API when resolving a UUID into a {@link SessionProfile} object.
 */
public class Jackson2SessionProfileHttpMessageConverter extends AbstractHttpMessageConverter<SessionProfile> {

	private ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Constructs a new {@code Jackson2SessionProfileHttpMessageConverter}. The converter is initialised to support the
	 * {@code application/json} media type.
	 */
	protected Jackson2SessionProfileHttpMessageConverter() {

		super(MediaType.APPLICATION_JSON);
	}

	/**
	 * @return {@code true} if {@code clazz} is the {@link SessionProfile} class
	 */
	@Override
	protected boolean supports(Class<?> clazz) {

		return clazz == SessionProfile.class;
	}

	/**
	 * @return {@code false}, since this implementation does not support writing
	 */
	@Override
	protected boolean canWrite(MediaType mediaType) {

		return false;
	}

	/**
	 * Reads the {@code inputMessage}, converting it to a {@code SessionProfile} object.
	 *
	 * @throws IOException when a general I/O error occurs, or when an error occurs while deserialising the JSON message
	 */
	@Override
	protected SessionProfile readInternal(Class<? extends SessionProfile> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {

		JacksonSessionProfile input = objectMapper.readValue(inputMessage.getBody(), JacksonSessionProfile.class);
		SessionProfile sessionProfile = new SessionProfile();
		sessionProfile.name = input.name;
		sessionProfile.uuid = input.id;
		for(Property property: input.properties) {
			if(!(property instanceof TexturesProperty)) {
				continue;
			}

			TexturesProperty texturesProperty = (TexturesProperty)property;
			JsonNode node = objectMapper.readTree(texturesProperty.getValue());
			JsonNode texturesNode = node.get("textures");
			if(texturesNode == null) {
				continue;
			}

			sessionProfile.capeTextureUri = readTextureUri(texturesNode, "CAPE");
			sessionProfile.skinTextureUri = readTextureUri(texturesNode, "SKIN");
		}
		return sessionProfile;
	}

	/**
	 * Reads the {@code url} property out of the JSON node, keyed by {@code name}, parsing the value into a {@link URI}
	 * object.
	 *
	 * @param node the root JSON node
	 * @param name the name of the object contained in {@code node} containing the {@code url} property
	 * @return the value of the {@code url} property as a {@code URI}, or {@code null} if the property could not be
	 *         located, or if the URI was syntactically invalid
	 */
	private URI readTextureUri(JsonNode node, String name) {

		URI uri = null;
		JsonNode textureNode = node.get(name);
		if(textureNode != null) {
			JsonNode uriNode = textureNode.get("url");
			if(uriNode != null) {
				try {
					uri = new URI(uriNode.textValue());
				} catch(URISyntaxException e) {
					// TODO
				}
			}
		}
		return uri;
	}

	/**
	 * Always throws an exception, as this implementation does not support writing.
	 *
	 * @throws UnsupportedOperationException if this method is called
	 */
	@Override
	protected void writeInternal(SessionProfile sessionProfile, HttpOutputMessage outputMessage) throws IOException,
			HttpMessageNotWritableException {

		throw new UnsupportedOperationException("Message converter does not support writing");
	}

	/**
	 * Sets the {@link ObjectMapper} for this converter. If not set, a default {@link ObjectMapper#ObjectMapper()
	 * ObjectMapper} is used.
	 *
	 * @throws IllegalArgumentException when {@code null} is given as an argument
	 */
	public void setObjectMapper(ObjectMapper objectMapper) {

		Assert.notNull(objectMapper, "ObjectMapper must not be null");
		this.objectMapper = objectMapper;
	}
}
