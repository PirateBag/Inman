package com.inman.integration;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.inman.model.rest.StatusResponse;
import com.inman.model.rest.VerifyCredentialsRequest;
import com.inman.model.rest.VerifyCredentialsResponse;



@RunWith( SpringRunner.class)
@SpringBootTest(classes = VerifyCredentials.class )
public class VerifyCredentials {

	private int port= 8080;

	private RestTemplate restTemplate = new RestTemplate();

	@Test
	public void invalidCredentials() throws Exception {
		
		VerifyCredentialsRequest badCredentials = new VerifyCredentialsRequest();
		badCredentials.setUsername("donald");
		badCredentials.setPassword("trumped");
		
		ResponseEntity<VerifyCredentialsResponse> entity = this.restTemplate.postForEntity(
				"http://localhost:" + this.port + VerifyCredentialsRequest.rootUrl, badCredentials, VerifyCredentialsResponse.class );
		
		assertEquals(entity.getBody().getStatus(), StatusResponse.INMAN_FAIL );
		assertEquals( entity.getBody().getMessage(), VerifyCredentialsResponse.CREDENTIALS_NOT_VALID );
		assertEquals( entity.getBody().getToken(), VerifyCredentialsResponse.NO_TOKEN );
	}
	@Test
	public void validCredentials() throws Exception {
		
		VerifyCredentialsRequest badCredentials = new VerifyCredentialsRequest();
		badCredentials.setUsername("fred");
		badCredentials.setPassword("dilban");
		
		ResponseEntity<VerifyCredentialsResponse> entity = this.restTemplate.postForEntity(
				"http://localhost:" + this.port + VerifyCredentialsRequest.rootUrl, badCredentials, VerifyCredentialsResponse.class );
		
		assertEquals(entity.getBody().getStatus(), StatusResponse.INMAN_OK );
		assertEquals( entity.getBody().getMessage(), VerifyCredentialsResponse.CREDENTIALS_VALID );
		assertEquals( entity.getBody().getToken(), VerifyCredentialsResponse.DEFAULT_TOKEN );
	}


}
