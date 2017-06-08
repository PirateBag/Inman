package com.inman.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.inman.model.rest.StatusResponse;
import com.inman.model.rest.VerifyCredentialsRequest;
import com.inman.model.rest.VerifyCredentialsResponse;

import static org.assertj.core.api.BDDAssertions.then;

@RunWith( SpringRunner.class)
@SpringBootTest(classes = VerifyCredentials.class )
public class VerifyCredentials {

	private int port= 8080;

	private RestTemplate restTemplate = new RestTemplate();

	@Test
	public void validCredentials() throws Exception {
		
		VerifyCredentialsRequest badCredentials = new VerifyCredentialsRequest();
		badCredentials.setUsername("donald");
		badCredentials.setPassword("trumped");
		
		ResponseEntity<VerifyCredentialsResponse> entity = this.restTemplate.putForEntity(
				"http://localhost:" + this.port + VerifyCredentialsRequest.rootUrl, VerifyCredentialsResponse.class );

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(entity.getBody().getStatus().equals( StatusResponse.INMAN_OK ));
	}

}
