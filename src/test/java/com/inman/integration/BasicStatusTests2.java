package com.inman.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.inman.model.rest.StatusResponse;

import static org.assertj.core.api.BDDAssertions.then;

@RunWith( SpringRunner.class)
@SpringBootTest(classes = BasicStatusTests2.class )
public class BasicStatusTests2 {

	private int port= 8080;

	private RestTemplate restTemplate = new RestTemplate();

	@Test
	public void shouldReturnOkAndStatusString() throws Exception {
		ResponseEntity<StatusResponse> entity = this.restTemplate.getForEntity(
				"http://localhost:" + this.port + StatusResponse.rootUrl, StatusResponse.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(entity.getBody().getStatus().equals( StatusResponse.INMAN_OK ));
	}
}
