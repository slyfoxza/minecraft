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

/**
 * Thrown when the client has been rate limited.
 */
public class RateLimitedException extends Exception {

	/**
	 * Constructs a new {@code RateLimitedException} without a message or cause.
	 */
	public RateLimitedException() {

		super();
	}

	/**
	 * Constructs a new {@code RateLimitedException} with the specified message.
	 *
	 * @param message the detail message
	 */
	public RateLimitedException(String message) {

		super(message);
	}

	/**
	 * Constructs a new {@code RateLimitedException} with the specified cause.
	 *
	 * @param cause the cause
	 */
	public RateLimitedException(Throwable cause) {

		super(cause);
	}

	/**
	 * Constructs a new {@code RateLimitedException} with the specified message and cause.
	 *
	 * @param message the detail message
	 * @param cause the cause
	 */
	public RateLimitedException(String message, Throwable cause) {

		super(message, cause);
	}
}
