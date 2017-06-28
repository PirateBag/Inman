package com.inman.integration;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.inman.model.Item;
import com.inman.model.rest.ErrorLine;
import com.inman.model.rest.ItemResponse;
import com.inman.model.rest.ResponsePackage;
import com.inman.model.rest.SearchItemRequest;
import com.inman.model.rest.StatusResponse;
import com.inman.model.rest.VerifyCredentialsRequest;
import com.inman.model.rest.VerifyCredentialsResponse;



@RunWith( SpringRunner.class)
@SpringBootTest(classes = VerifySearchItem.class )
public class VerifySearchItem {

	private int port= 8080;

	private RestTemplate restTemplate = new RestTemplate();

	@Test
	public void singleItemSuccessfulSearch() throws Exception {
		
		String url = "http://localhost:" + this.port + "/"+ SearchItemRequest.singleUrl;
		url = url.replace( "{itemId}", "2");
				
		assertEquals( "http://localhost:8080/item/search/2", url );
		
		ResponseEntity<Item[]> entity = this.restTemplate.getForEntity( url, Item[].class );				
				
		assertEquals( entity.getBody().length, 1 );
		assertEquals( entity.getBody()[0].getId(), 2 );
		assertEquals( entity.getBody()[0].getSummaryId(), "W-002");
		assertEquals( entity.getBody()[0].getDescription(), "Painted Wagon Body");
	}
	
	
	@Test
	public void singleItemFailedSearch() throws Exception {
		
		String url = "http://localhost:" + this.port + "/"+ SearchItemRequest.singleUrl;
		url = url.replace( "{itemId}", "9999");
				
		assertEquals( "http://localhost:8080/item/search/9999", url );
		
		ResponseEntity<Item[]> entity = this.restTemplate.getForEntity( url, Item[].class );				
				
		assertEquals( entity.getBody().length, 0 );
	}


	@Test
	public void singleSearchParam() throws Exception {
		
		String url = "http://localhost:" + this.port + "/"+ SearchItemRequest.expUrl + "?id=2&summaryId=&description=";

		ResponseEntity<ItemResponse> entity 
		   = this.restTemplate.getForEntity( url, ItemResponse.class );
				
		assertEquals(entity.getStatusCode(), HttpStatus.OK );
		assertEquals( entity.getBody().getErrors().size(), 0 );
		
		assertEquals( entity.getBody().getData().length, 1 );
		assertEquals( entity.getBody().getData()[0].getId(), 2 );
		assertEquals( entity.getBody().getData()[0].getSummaryId(), "W-002");
		assertEquals( entity.getBody().getData()[0].getDescription(), "Painted Wagon Body");
	}
	
	@Test
	public void searchBySummaryIdgetOneResult() throws Exception {
		
		String url = "http://localhost:" + this.port + "/"+ SearchItemRequest.expUrl + "?id=&summaryId=W-002&description=";
		
		ResponseEntity<ItemResponse> entity 
		   = this.restTemplate.getForEntity( url, ItemResponse.class );
				

		assertEquals(entity.getStatusCode(), HttpStatus.OK );
		assertEquals( entity.getBody().getErrors().size(), 0 );
		
		assertEquals( entity.getBody().getData().length, 1 );
		assertEquals( entity.getBody().getData()[0].getId(), 2 );
		assertEquals( entity.getBody().getData()[0].getSummaryId(), "W-002");
		assertEquals( entity.getBody().getData()[0].getDescription(), "Painted Wagon Body");
	}
	
	@Test
	public void searchBySummaryIdgetTwoResults() throws Exception {
		

		String url = "http://localhost:" + this.port + "/"+ SearchItemRequest.expUrl + "?id=&summaryId=W-01&description=";
		
		ResponseEntity<ItemResponse> entity 
		   = this.restTemplate.getForEntity( url, ItemResponse.class );
		
		assertEquals(entity.getStatusCode(), HttpStatus.OK );
		assertEquals( entity.getBody().getErrors().size(), 0 );
		
		assertEquals( entity.getBody().getData().length, 2 );
		assertEquals( entity.getBody().getData()[0].getId(), 4 );
		assertEquals( entity.getBody().getData()[0].getSummaryId(), "W-018");
		assertEquals( entity.getBody().getData()[0].getDescription(), "Unpainted Wagon Body");
		assertEquals( entity.getBody().getData()[1].getId(), 5 );
		assertEquals( entity.getBody().getData()[1].getSummaryId(), "W-019");
		assertEquals( entity.getBody().getData()[1].getDescription(), "Red Paint");
	}
	

	@Test
	public void noParametersCheck() throws Exception {
		String url = "http://localhost:" + this.port + "/"+ SearchItemRequest.expUrl + "?id=&summaryId=&description=";
		
		ResponseEntity<ItemResponse> entity 
		   = this.restTemplate.getForEntity( url, ItemResponse.class );
		
		assertEquals(entity.getStatusCode(), HttpStatus.OK );
		assertEquals( entity.getBody().getErrors().size(), 1 );
		assertEquals( entity.getBody().getData().length, 0 );
		assertEquals( entity.getBody().getErrors().get(0).getKey(), ErrorLine.NO_KEY );
		assertEquals( entity.getBody().getErrors().get(0).getCode(), "0" );
		assertEquals( entity.getBody().getErrors().get(0).getMessage(), "Must provide exactly one search parameter" );
	}

}
