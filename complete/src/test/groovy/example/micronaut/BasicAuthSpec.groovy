package example.micronaut

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject

@MicronautTest // <1>
class BasicAuthSpec extends Specification {

    @Inject
    EmbeddedServer embeddedServer // <2>

    @Inject
    @Client("/")
    RxHttpClient client // <3>

    def "Verify HTTP Basic Auth works"() {
        when: 'Accessing a secured URL without authenticating'
        client.toBlocking().exchange(HttpRequest.GET('/').accept(MediaType.TEXT_PLAIN)) // <4>

        then: 'returns unauthorized'
        HttpClientResponseException e = thrown(HttpClientResponseException) // <5>
        e.status == HttpStatus.UNAUTHORIZED

        when: 'A secured URL is accessed with Basic Auth'
        HttpRequest request = HttpRequest.GET('/')
                .accept(MediaType.TEXT_PLAIN)
                .basicAuth("sherlock", "password") // <6>
        HttpResponse<String> rsp = client.toBlocking().exchange(request, String) // <7>

        then: 'the endpoint can be accessed'
        noExceptionThrown()
        rsp.status == HttpStatus.OK
        rsp.body() == 'sherlock' // <8>
    }
}
