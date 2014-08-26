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
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

/**
 * JAX-RS {@link MessageBodyWriter} that writes a JSON array of profile names, given an array of
 * strings.
 */
@Produces(MediaType.APPLICATION_JSON)
public class StringListMessageBodyWriter implements MessageBodyWriter<String[]> {

	private JsonGeneratorFactory jsonGeneratorFactory;

	/**
	 * Constructs an uninitialised {@code StringListMessageBodyWriter}. This constructor is
	 * primarily for use in dependency injection frameworks that use setters to inject dependencies.
	 *
	 * @see #StringListMessageBodyWriter(JsonGeneratorFactory)
	 */
	public StringListMessageBodyWriter() { }

	/**
	 * Constructs a {@code StringListMessageBodyWriter}, initialising it with the given {@code
	 * jsonGeneratorFactory}.
	 *
	 * @see #setJsonGeneratorFactory
	 */
	public StringListMessageBodyWriter(JsonGeneratorFactory jsonGeneratorFactory) {

		setJsonGeneratorFactory(jsonGeneratorFactory);
	}

	/**
	 * @return -1, in accordance with the JAX-RS 2.0 recommendation
	 */
	@Override
	public long getSize(String[] strings, Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType) {

		return -1;
	}

	/**
	 * @return {@code true} only if {@code type} is {@code String[]}, otherwise {@code false}
	 */
	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType) {

		return type == String[].class;
	}

	/**
	 * Writes a JSON array to {@code entityStream} containing a JSON string value for each string in
	 * the {@code strings} array.
	 */
	@Override
	public void writeTo(String[] strings, Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException, WebApplicationException {

		JsonGenerator jsonGenerator = jsonGeneratorFactory.createGenerator(entityStream);
		try {
			jsonGenerator.writeStartArray();
			for(String string: strings) {
				jsonGenerator.write(string);
			}
			jsonGenerator.writeEnd();

		} finally {
			jsonGenerator.flush();
		}
	}

	/**
	 * Sets the factory instance that will be used in {@link #writeTo} to obtain a {@link
	 * JsonGenerator} to write JSON content.
	 */
	public void setJsonGeneratorFactory(JsonGeneratorFactory jsonGeneratorFactory) {

		this.jsonGeneratorFactory = jsonGeneratorFactory;
	}
}
