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
import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Minecraft query client implemented using standard Java {@link DatagramSocket} APIs.
 */
public class DatagramMinecraftQueryClient implements MinecraftQueryClient {

	protected static final byte CHALLENGE_TYPE = 0x09;
	protected static final byte QUERY_TYPE = 0x00;

	private final DatagramPacketFactory<QueryContext> basicQueryPacketFactory;
	private final DatagramPacketFactory<ChallengeContext> challengePacketFactory;
	private final DatagramPacketFactory<QueryContext> fullQueryPacketFactory;
	private final ResponseHandler<BasicStatus> basicQueryResponseHandler;
	private final ResponseHandler<FullStatus> fullQueryResponseHandler;

	private final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Constructs a new {@code DatagramMinecraftQueryClient}.
	 */
	public DatagramMinecraftQueryClient() {

		basicQueryPacketFactory = new BasicQueryDatagramPacketFactory();
		challengePacketFactory = new ChallengeDatagramPacketFactory();
		fullQueryPacketFactory = new FullQueryDatagramPacketFactory();
		basicQueryResponseHandler = new BasicQueryResponseHandler();
		fullQueryResponseHandler = new FullQueryResponseHandler();
	}

	/**
	 * @see #doQuery
	 */
	@Override
	public BasicStatus queryBasicStatus(SocketAddress address) throws IOException {

		log.debug("Querying basic status");
		return doQuery(address, basicQueryPacketFactory, basicQueryResponseHandler);
	}

	/**
	 * @see #doQuery
	 */
	@Override
	public FullStatus queryFullStatus(SocketAddress address) throws IOException {

		log.debug("Querying full status");
		return doQuery(address, fullQueryPacketFactory, fullQueryResponseHandler);
	}

	/**
	 * Implements the conversation between the client and the server for a single query request.
	 * <p>When the query request datagram must be constructed, this method delegates to {@code
	 * queryPacketFactory}. To handle the response received from the query request, the method
	 * delegates to the {@code responseHandler}, and returns the result to the caller.</p>
	 * <p>The default implementations of {@link #queryBasicStatus} and {@link #queryFullStatus}
	 * simply delegate to this method with the appropriate factory and handler objects.</p>
	 *
	 * @param address the address of the query service
	 * @param queryPacketFactory the factory that will construct the request datagram
	 * @param responseHandler the handler that will parse the query response datagram into a status
	 *                        data structure of type {@code T}
	 * @param <T> the status data structure type to return (one of {@link BasicStatus} or
	 *            {@link FullStatus})
	 * @return the status data received from the server, as returned by the {@code responseHandler}
	 * @throws IOException when an I/O error occurs
	 */
	protected <T extends BasicStatus> T doQuery(SocketAddress address,
			DatagramPacketFactory<QueryContext> queryPacketFactory,
			ResponseHandler<T> responseHandler) throws IOException {

		DatagramSocket socket = new DatagramSocket();
		try {
			socket.setSoTimeout(2000);
			socket.connect(address);

			DatagramPacket packet = challengePacketFactory.construct(new ChallengeContext(1));
			log.debug("Sending challenge packet ({} bytes)", packet.getLength());
			socket.send(packet);
			packet = receive(socket, CHALLENGE_TYPE);
			AtomicInteger cursor = new AtomicInteger(5);
			String challengeTokenString = readNullTerminatedString(packet.getData(),
					packet.getLength(), cursor);
			log.debug("Extracted challenge token from response: {}", challengeTokenString);

			QueryContext queryContext = new QueryContext(Integer.parseInt(challengeTokenString), 1);
			packet = queryPacketFactory.construct(queryContext);
			log.debug("Sending query packet ({} bytes)", packet.getLength());
			socket.send(packet);
			packet = receive(socket, QUERY_TYPE);
			return responseHandler.handle(packet);
		} finally {
			socket.close();
		}
	}

	/**
	 * Receives a datagram from the {@code socket}, asserting that the response type contained in
	 * the first byte of the response is the {@code expectedType}.
	 *
	 * @param socket the socket to receive from
	 * @param expectedType the expected response type (one of {@link #CHALLENGE_TYPE} or {@link
	 *                     #QUERY_TYPE})
	 * @return the received datagram packet
	 * @throws IOException when an I/O error occurs
	 */
	protected DatagramPacket receive(DatagramSocket socket, byte expectedType) throws IOException {

		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		socket.receive(packet);
		log.debug("Received response packet ({} bytes)", packet.getLength());
		if(data[0] != expectedType) {
			throw new RuntimeException("Expected response type 0x"
					+ Integer.toHexString(expectedType & 0xFF) + ", received "
					+ Integer.toHexString(data[0] & 0xFF) + " instead");
		}
		return packet;
	}

	/**
	 * Reads a null-terminated string from the given {@code data} buffer.
	 *
	 * @param data the array containing the null-terminated string
	 * @param length the length of the buffer
	 * @param cursor the cursor indicating the current position in the buffer
	 * @return the null-terminated string value, without the null-terminator
	 * @throws RuntimeException when a buffer overflow occurs
	 */
	protected static String readNullTerminatedString(byte[] data, int length, AtomicInteger cursor) {

		final int i0 = cursor.get();
		int i = i0;
		while((i < length) && (data[i] != 0x00)) {
			i = cursor.incrementAndGet();
		}

		if(i == length) {
			throw new RuntimeException("Buffer overflow: no null terminator found while reading "
					+ "string");
		}

		return new String(Arrays.copyOfRange(data, i0, cursor.getAndIncrement()));
	}

	private static class ChallengeContext {

		public final int sessionID;

		public ChallengeContext(int sessionID) {

			this.sessionID = sessionID & 0x0F0F0F0F;
		}
	}

	private static class QueryContext {

		public final int challengeToken;
		public final int sessionID;

		public QueryContext(int challengeToken, int sessionID) {

			this.challengeToken = challengeToken;
			this.sessionID = sessionID;
		}
	}

	/**
	 * Interface for {@link DatagramPacket} factories.
	 * @param <C> context type
	 */
	public interface DatagramPacketFactory<C> {

		byte[] MAGIC_BYTES = new byte[] { (byte)0xFE, (byte)0xFD };

		/**
		 * Constructs a new {@link DatagramPacket}
		 * @param context context object used during construction
		 * @return a new datagram packet
		 */
		DatagramPacket construct(C context);
	}

	private static class ChallengeDatagramPacketFactory
			implements DatagramPacketFactory<ChallengeContext> {

		@Override
		public DatagramPacket construct(ChallengeContext context) {

			ByteBuffer buffer = ByteBuffer.allocate(MAGIC_BYTES.length + 1 + 4);
			buffer.put(MAGIC_BYTES).put(CHALLENGE_TYPE).putInt(context.sessionID).flip();
			return new DatagramPacket(buffer.array(), buffer.limit());
		}
	}

	private static class BasicQueryDatagramPacketFactory
		implements DatagramPacketFactory<QueryContext> {

		@Override
		public DatagramPacket construct(QueryContext context) {

			ByteBuffer buffer = ByteBuffer.allocate(MAGIC_BYTES.length + 1 + 4 + 4);
			buffer.put(MAGIC_BYTES).put(QUERY_TYPE).putInt(context.sessionID)
					.putInt(context.challengeToken).flip();
			return new DatagramPacket(buffer.array(), buffer.limit());
		}
	}

	private static class FullQueryDatagramPacketFactory
			implements DatagramPacketFactory<QueryContext> {

		@Override
		public DatagramPacket construct(QueryContext context) {

			ByteBuffer buffer = ByteBuffer.allocate(MAGIC_BYTES.length + 1 + 4 + 4 + 4);
			buffer.put(MAGIC_BYTES).put(QUERY_TYPE).putInt(context.sessionID)
					.putInt(context.challengeToken).putInt(0).flip();
			return new DatagramPacket(buffer.array(), buffer.limit());
		}
	}

	/**
	 * Query response handler interface.
	 * @param <T> output data structure type
	 */
	public interface ResponseHandler<T extends BasicStatus> {

		/**
		 * Parses the {@code response} into a status data structure
		 * @param response the datagram packet containing the query response
		 * @return the query data
		 */
		T handle(DatagramPacket response);
	}

	private static class BasicQueryResponseHandler implements ResponseHandler<BasicStatus> {

		@Override
		public BasicStatus handle(DatagramPacket response) {

			final byte[] data = response.getData();
			final int length = response.getLength();

			BasicStatus status = new BasicStatus();
			AtomicInteger cursor = new AtomicInteger(5);

			status.motd = readNullTerminatedString(data, length, cursor);
			status.gameType = readNullTerminatedString(data, length, cursor);
			status.worldName = readNullTerminatedString(data, length, cursor);
			status.onlinePlayerCount = Integer.parseInt(readNullTerminatedString(data, length,
					cursor));
			status.maximumPlayerCount = Integer.parseInt(readNullTerminatedString(data, length,
					cursor));
			status.serverPort = bytesToShort(data, cursor);
			status.serverHost = readNullTerminatedString(data, length, cursor);
			return status;
		}

		private short bytesToShort(byte[] data, AtomicInteger cursor) {

			try {
				final int p = cursor.get();
				int msb = (data[p + 1] << 8) & 0xFF00;
				int lsb = data[p] & 0xFF;
				return (short)(msb | lsb);
			} finally {
				cursor.addAndGet(2);
			}
		}
	}

	private static class FullQueryResponseHandler implements ResponseHandler<FullStatus> {

		private static final Map<String, Field> mapping;

		static {
			mapping = new HashMap<String, Field>();
			try {
				mapping.put("game_id", FullStatus.class.getField("gameID"));
				mapping.put("gametype", FullStatus.class.getField("gameType"));
				mapping.put("hostip", FullStatus.class.getField("serverHost"));
				mapping.put("hostname", FullStatus.class.getField("motd"));
				mapping.put("hostport", FullStatus.class.getField("serverPort"));
				mapping.put("map", FullStatus.class.getField("worldName"));
				mapping.put("maxplayers", FullStatus.class.getField("maximumPlayerCount"));
				mapping.put("numplayers", FullStatus.class.getField("onlinePlayerCount"));
				mapping.put("plugins", FullStatus.class.getField("plugins"));
				mapping.put("version", FullStatus.class.getField("version"));
			} catch(NoSuchFieldException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public FullStatus handle(DatagramPacket response) {

			final byte[] data = response.getData();
			final int length = response.getLength();

			FullStatus status = new FullStatus();
			AtomicInteger cursor = new AtomicInteger(16);

			while(true) {
				String key = readNullTerminatedString(data, length, cursor);
				if(key.isEmpty()) {
					break;
				}

				String value = readNullTerminatedString(data, length, cursor);
				Field field = mapping.get(key);
				if(field != null) {
					try {
						if(field.getType().isPrimitive()) {
							if(field.getType().equals(Integer.TYPE)) {
								field.setInt(status, Integer.parseInt(value));
							} else {
								field.setShort(status, Short.parseShort(value));
							}
						} else {
							field.set(status, value);
						}
					} catch(IllegalAccessException e) {
						status.additionalStatus.put(key, value);
					}
				} else {
					status.additionalStatus.put(key, value);
				}
			}

			cursor.addAndGet(10);
			while(true) {
				String value = readNullTerminatedString(data, length, cursor);
				if(value.isEmpty()) {
					break;
				}

				status.onlinePlayers.add(value);
			}

			return status;
		}
	}
}
