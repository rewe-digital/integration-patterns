package com.rewedigital.examples.msintegration.composer.composing;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.spotify.apollo.Client;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;

import okio.ByteString;

public class IncludedService {

	public class WithResponse {
		private final Response<ByteString> response;

		private WithResponse(Response<ByteString> futureServiceResponse) {
			this.response = futureServiceResponse;
		}

		public Response<ByteString> content() {
			return response;
		}

		// FIXME add parser etc as parameter
		public WithContent extractContent() {
			return new WithContent(response.payload().get().utf8());
		}
	}

	public class WithContent {

		private final String content;

		public WithContent(String content) {
			this.content = content;
		}

		public int startOffset() {
			return startOffset;
		}

		public int endOffset() {
			return endOffset;
		}

		public String content() {
			return content;
		}
	}

	private final Map<String, String> attributes = new HashMap<>();
	private int startOffset;
	private int endOffset;

	public void put(final String attribute, final String value) {
		attributes.put(attribute, value);
	}

	public void startOffset(final int startOffset) {
		this.startOffset = startOffset;
	}

	public void endOffset(final int endOffset) {
		this.endOffset = endOffset;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public String path() {
		return attributes.get("path");
	}

	public int startOffset() {
		return startOffset;
	}

	public int endOffset() {
		return endOffset;
	}

	public CompletableFuture<WithResponse> fetchContent(final Client client) {
		return client.send(Request.forUri(this.path(), "GET"))
				.thenApply(response -> new IncludedService.WithResponse(response)).toCompletableFuture();
	}

}
