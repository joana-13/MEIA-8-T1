/* (C)2023 */
package isep.ipp.pt.service;

import isep.ipp.pt.model.PrologCreateFactsDto;
import isep.ipp.pt.model.PrologHowDto;
import isep.ipp.pt.model.PrologStartEngineDto;
import isep.ipp.pt.model.PrologWhyNotDto;
import isep.ipp.pt.model.PrologWhyNotResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;

@Service
@Slf4j
public class PrologService {
    protected WebClient client = WebClient.create("http://localhost:4000");

    public Mono<String> createFacts(PrologCreateFactsDto prologCreateFactsDto) {
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = client.method(HttpMethod.POST);
        WebClient.RequestBodySpec bodySpec = uriSpec.uri("/cria_factos");
        WebClient.RequestHeadersSpec<?> headersSpec = bodySpec.bodyValue(prologCreateFactsDto);
        headersSpec.header(
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                .acceptCharset(StandardCharsets.UTF_8)
                .ifNoneMatch("*")
                .ifModifiedSince(ZonedDateTime.now())
                .retrieve();

        Mono<String> response = headersSpec.retrieve()
                .bodyToMono(String.class);

        return response;
    }

    public Mono<PrologStartEngineDto> startEngine() {
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = client.method(HttpMethod.GET);
        WebClient.RequestBodySpec bodySpec = uriSpec.uri("/arranca_motor");
        bodySpec.header(
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                .acceptCharset(StandardCharsets.UTF_8)
                .ifNoneMatch("*")
                .ifModifiedSince(ZonedDateTime.now())
                .retrieve();

        Mono<PrologStartEngineDto> response = bodySpec.retrieve()
                .bodyToMono(PrologStartEngineDto.class);

        return response;
    }

    public Mono<PrologHowDto> getHow(Integer number) {
        WebClient.RequestHeadersSpec<?> uri = client.get()
                .uri(uriBuilder -> uriBuilder
                .path("/como")
                .queryParam("nFacto",number)
                .build());

        uri.header(
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                .acceptCharset(StandardCharsets.UTF_8)
                .ifNoneMatch("*")
                .ifModifiedSince(ZonedDateTime.now())
                .retrieve();

        Mono<PrologHowDto> response = uri.retrieve()
                .bodyToMono(PrologHowDto.class);

        return response;
    }

    public Mono<PrologWhyNotResponseDto> whyNot(PrologWhyNotDto prologWhyNotDto) {
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = client.method(HttpMethod.POST);
        WebClient.RequestBodySpec bodySpec = uriSpec.uri("/whynot");
        WebClient.RequestHeadersSpec<?> headersSpec = bodySpec.bodyValue(prologWhyNotDto);
        headersSpec.header(
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                .acceptCharset(StandardCharsets.UTF_8)
                .ifNoneMatch("*")
                .ifModifiedSince(ZonedDateTime.now())
                .retrieve();

        Mono<PrologWhyNotResponseDto> response = headersSpec.retrieve()
                .bodyToMono(PrologWhyNotResponseDto.class);

        return response;
    }
}
