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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.ws.rs.Consumes;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.ext.MessageBodyReader;

import org.apache.commons.codec.binary.Base64;

/**
 * JAX-RS {@link MessageBodyReader} that reads a JSON profile response originating from {@code
 * sessionserver.mojang.com} into a new {@link SessionProfile} instance.
 */
@Consumes(MediaType.APPLICATION_JSON)
public class SessionProfileMessageBodyReader implements MessageBodyReader<SessionProfile> {

	private JsonReaderFactory jsonReaderFactory;

	/**
	 * Constructs an uninitialised {@code SessionProfileMessageBodyReader}. This constructor is
	 * primarily for use in dependency injection frameworks that use setters to inject dependencies.
	 *
	 * @see #SessionProfileMessageBodyReader(JsonReaderFactory)
	 */
	public SessionProfileMessageBodyReader() { }

	/**
	 * Constructs a {@code SessionProfileMessageBodyReader}, initialising it with the given
	 * {@code jsonReaderFactory}.
	 *
	 * @see #setJsonReaderFactory
	 */
	public SessionProfileMessageBodyReader(JsonReaderFactory jsonReaderFactory) {

		setJsonReaderFactory(jsonReaderFactory);
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType) {

		return type == SessionProfile.class;
	}

	@Override
	public SessionProfile readFrom(Class<SessionProfile> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {

		JsonReader jsonReader;
		try {
			jsonReader = jsonReaderFactory.createReader(entityStream);
		} catch(JsonException e) {
			/* Assume that a JSON exception at this point means that we received a zero-length
			 * response */
			throw new NoContentException("Could not initialise JSON reader", e);
		}

		JsonObject sessionProfileJson = jsonReader.readObject();
		SessionProfile sessionProfile = new SessionProfile();
		try {
			sessionProfile.uuid = sessionProfileJson.getString("id");
			sessionProfile.name = sessionProfileJson.getString("name");
			JsonArray properties = sessionProfileJson.getJsonArray("properties");
			if(properties != null) {
				/* Attempt to find a JSON object in the properties array that has a name property
				 * with a value of "textures" */
				for(JsonValue value: properties) {
					if(value.getValueType() != ValueType.OBJECT) {
						continue;
					}

					JsonObject object = (JsonObject)value;
					String propertyName = object.getString("name", null);
					if((propertyName == null) || !propertyName.equals("textures")) {
						continue;
					}

					// Found the textures object, attempt to extract the value property
					String propertyValue = object.getString("value", null);
					if(propertyValue == null) {
						continue;
					}

					// Decode the Base64-encoded value, and parse the result as JSON
					byte[] decodedValue = Base64.decodeBase64(propertyValue);
					JsonReader valueJsonReader = jsonReaderFactory.createReader(
							new ByteArrayInputStream(decodedValue));
					JsonObject texturesPropertyJson = valueJsonReader.readObject();
					JsonObject textures = texturesPropertyJson.getJsonObject("textures");
					if(textures == null) {
						continue;
					}

					sessionProfile.capeTextureUri = readTextureUri(textures, "CAPE");
					sessionProfile.skinTextureUri = readTextureUri(textures, "SKIN");
				}
			}
			return sessionProfile;

		} catch(ClassCastException e) {
			throw new ProcessingException("Failed to read session profile from response", e);
		} catch(NullPointerException e) {
			throw new ProcessingException("Failed to read session profile from response", e);
		}
	}

	/**
	 * Reads the {@code url} property out of the JSON object, keyed by {@code textureName},
	 * contained in the {@code texturesJson} JSON object, parsing the value into a {@link URI}
	 * object.
	 *
	 * @param texturesJson the root JSON object
	 * @param textureName the name of the object contained in {@code texturesJson} containing the
	 *                    {@code url} property
	 * @return the value of the {@code url} property as a {@code URI}, or {@code null} if the
	 *         property could not be located, or if the URI was syntactically invalid
	 */
	private URI readTextureUri(JsonObject texturesJson, String textureName) {

		URI uri = null;
		JsonObject textureObject = texturesJson.getJsonObject(textureName);
		if(textureObject != null) {
			String uriString = textureObject.getString("url", null);
			if(uriString != null) {
				try {
					uri = new URI(uriString);
				} catch(URISyntaxException e) {
					// TODO
				}
			}
		}
		return uri;
	}

	/**
	 * Sets the factory instance that will be used in {@link #readFrom} to obtain {@link JsonReader}
	 * instances to read JSON content.
	 */
	public void setJsonReaderFactory(JsonReaderFactory jsonReaderFactory) {

		this.jsonReaderFactory = jsonReaderFactory;
	}
}
