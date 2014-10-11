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
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

/**
 * JAX-RS {@link MessageBodyReader} that reads a JSON array of profiles from the Mojang Profile API
 * into a new list of {@link Profile} instances.
 */
@Consumes(MediaType.APPLICATION_JSON)
public class ProfileListMessageBodyReader implements MessageBodyReader<List<Profile>> {

	private JsonReaderFactory jsonReaderFactory;

	/**
	 * Constructs an uninitialised {@code ProfileListMessageBodyReader}. This constructor is
	 * primarily for use in dependency injection frameworks that use setters to inject dependencies.
	 *
	 * @see #ProfileListMessageBodyReader(JsonReaderFactory))
	 */
	public ProfileListMessageBodyReader() { }

	/**
	 * Constructs a {@code ProfileListMessageBodyReader}, initialising it with the given {@code
	 * jsonReaderFactory}.
	 *
	 * @see #setJsonReaderFactory
	 */
	public ProfileListMessageBodyReader(JsonReaderFactory jsonReaderFactory) {

		setJsonReaderFactory(jsonReaderFactory);
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType) {

		if(type != List.class) {
			return false;
		}

		try {
			ParameterizedType parameterisedType = (ParameterizedType)genericType;
			Type[] types = parameterisedType.getActualTypeArguments();
			if((types.length == 1) && (types[0] == Profile.class)) {
				return true;
			}
		} catch(ClassCastException e) {
			// genericType was not a ParameterizedType; return false
		}

		return false;
	}

	@Override
	public List<Profile> readFrom(Class<List<Profile>> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {

		JsonReader jsonReader = jsonReaderFactory.createReader(entityStream);
		JsonArray jsonProfiles = jsonReader.readArray();
		List<Profile> profiles = new LinkedList<Profile>();
		for(JsonValue value: jsonProfiles) {
			if(value.getValueType() != ValueType.OBJECT) {
				continue;
			}

			JsonObject jsonProfile = (JsonObject)value;
			Profile profile = new Profile();
			profile.uuid = jsonProfile.getString("id");
			profile.name = jsonProfile.getString("name");
			profile.isLegacy = jsonProfile.getBoolean("legacy", false);
			profile.isDemo = jsonProfile.getBoolean("demo", false);
			profiles.add(profile);
		}

		return profiles;
	}

	/**
	 * Sets the factory instance that will be used in {@link #readFrom} to obtain a {@link
	 * JsonReader} to read JSON content.
	 */
	public void setJsonReaderFactory(JsonReaderFactory jsonReaderFactory) {

		this.jsonReaderFactory = jsonReaderFactory;
	}
}
