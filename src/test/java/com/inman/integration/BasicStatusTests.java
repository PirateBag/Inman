package com.inman.integration;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.inman.model.rest.StatusResponse;

import static org.assertj.core.api.BDDAssertions.then;

@RunWith( SpringRunner.class)
@SpringBootTest(classes = BasicStatusTests.class )
public class BasicStatusTests {

	private int port= 8080;

	private TestRestTemplate testRestTemplate = new TestRestTemplate();

	@Test
	public void shouldReturnOkAndStatusString() throws Exception {
		ResponseEntity<StatusResponse> entity = this.testRestTemplate.getForEntity(
				"http://localhost:" + this.port + StatusResponse.rootUrl, StatusResponse.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(entity.getBody().getStatus().equals( StatusResponse.INMAN_OK ));
	}
}
