package com.rewedigital.examples.msintegration.composer.composing;

import java.io.StringWriter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.NotImplementedException;
import org.attoparser.IMarkupParser;
import org.attoparser.MarkupParser;
import org.attoparser.ParseException;
import org.attoparser.config.ParseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.rewedigital.examples.msintegration.composer.composing.parser.ContentContributorSelectorHandler;
import com.rewedigital.examples.msintegration.composer.composing.parser.ContentExtractor;
import com.rewedigital.examples.msintegration.composer.composing.parser.IncludedService;
import com.rewedigital.examples.msintegration.composer.composing.parser.IncludedService.WithContent;
import com.spotify.apollo.Environment;

public class Composer {

	private static final Logger LOGGER = LoggerFactory.getLogger(Composer.class);

	private final IMarkupParser parser = new MarkupParser(ParseConfiguration.htmlConfiguration());
	private final ContentExtractor contentExtractor = new ContentExtractor();
	private final Environment environment;

	public Composer(final Environment environment) {
		this.environment = environment;
	}

	public String compose(final String baseTemplate) {

		// Parse
		final ContentContributorSelectorHandler handler = new ContentContributorSelectorHandler();
		try {
			parser.parse(baseTemplate, handler);
		} catch (final ParseException e) {
			Throwables.propagate(e);
		}

		final List<IncludedService> includedServices = handler.includedServices();
		final CompletableFuture<Stream<IncludedService.WithContent>> futureContentStream = flatten(
				fetchContent(includedServices));

		try {
			return futureContentStream.thenApply(contentStream -> replaceInTemplate(baseTemplate, contentStream)).get();
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error("execution error while composing template with content", e);
			return baseTemplate;
		}

	}

	private Stream<CompletableFuture<WithContent>> fetchContent(final List<IncludedService> includedServices) {
		return includedServices.stream().map(service -> service.fetchContent(environment.client()))
				.map(futureResponse -> futureResponse.thenApply(response -> response.extractContent(contentExtractor)));
	}

	private String replaceInTemplate(String template, Stream<IncludedService.WithContent> contentStream) {
		return contentStream
				.collect(() -> new OngoingReplacement(template), (r, c) -> r.replace(c), (a, b) -> a.combine(b))
				.result();
	}

	private static <T> CompletableFuture<Stream<T>> flatten(Stream<CompletableFuture<T>> com) {
		List<CompletableFuture<T>> list = com.collect(Collectors.toList());
		return CompletableFuture.allOf(list.toArray(new CompletableFuture[list.size()]))
				.thenApply(v -> list.stream().map(CompletableFuture::join));
	}

	private static class OngoingReplacement {
		private int startIndex;
		private StringWriter writer;
		private String baseTemplate;

		public OngoingReplacement(String baseTemplate) {
			this.baseTemplate = baseTemplate;
			this.writer = new StringWriter(baseTemplate.length());
			this.startIndex = 0;
		}

		public void replace(IncludedService.WithContent content) {
			writer.write(baseTemplate, startIndex, content.startOffset() - startIndex);
			writer.write(content.content());
			startIndex = content.endOffset();
		}

		public void combine(OngoingReplacement other) {
			throw new NotImplementedException("can not be used with parallel streams");
		}

		public String result() {
			writer.write(baseTemplate, startIndex, baseTemplate.length() - startIndex);
			return writer.toString();
		}
	}
}
