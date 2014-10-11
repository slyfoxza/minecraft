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
package net.za.slyfox.minecraft

import org.glassfish.jersey.filter.LoggingFilter

import javax.json.Json
import javax.ws.rs.client.ClientBuilder
import java.util.logging.Logger

class Application {

	static void main(arguments) {

		CliBuilder cliBuilder = new CliBuilder(
				usage: 'profile-cli [options] [uuid or profile names]'
		)
		cliBuilder.i(args: 1, 'implementation')
		cliBuilder.r('performs UUID to profile mapping')
		def options = cliBuilder.parse(arguments)

		ProfileClient client = null
		switch(options.i) {
			case 'jaxrs':
				client = createJaxRsProfileClient()
				break;
			case 'spring':
				client = createRestTemplateProfileClient()
				break;
		}

		if(options.r) {
			println client.retrieveProfileForUuid(options.arguments().first())
		} else {
			println client.retrieveProfilesForNames(options.arguments().toArray(
					new String[options.arguments().size()]))
		}
	}

	private static createJaxRsProfileClient() {

		return new JaxRsProfileClient().with {
			def jsonGeneratorFactory = Json.createGeneratorFactory(null)
			def jsonReaderFactory = Json.createReaderFactory(null)

			client = ClientBuilder.newBuilder()
					.register(new ProfileListMessageBodyReader(jsonReaderFactory))
					.register(new SessionProfileMessageBodyReader(jsonReaderFactory))
					.register(new StringListMessageBodyWriter(jsonGeneratorFactory))
					.register(new LoggingFilter(Logger.global, true))
					.build()
			return it
		}
	}

	private static createRestTemplateProfileClient() {

		return new RestTemplateProfileClient();
	}
}