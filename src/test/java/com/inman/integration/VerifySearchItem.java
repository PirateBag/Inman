package com.inman.integration;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.inman.business.Message;
import com.inman.entity.Item;
import com.inman.model.rest.ItemResponse;
import com.inman.model.rest.SearchItemRequest;



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
		
		String url = "http://localhost:" + this.port + "/"+ SearchItemRequest.queryUrl + "?id=2&summaryId=&description=";

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
		
		String url = "http://localhost:" + this.port + "/"+ SearchItemRequest.queryUrl + "?id=&summaryId=W-002&description=";
		
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
		

		String url = "http://localhost:" + this.port + "/"+ SearchItemRequest.queryUrl + "?id=&summaryId=W-01&description=";
		
		ResponseEntity<ItemResponse> entity 
		   = this.restTemplate.getForEntity( url, ItemResponse.class );
		
		assertEquals(entity.getStatusCode(), HttpStatus.OK );
		assertEquals( entity.getBody().getErrors().size(), 0 );
		
		assertEquals( entity.getBody().getData().length, 2 );
		assertEquals( entity.getBody().getData()[0].getId(), 6 );
		assertEquals( entity.getBody().getData()[0].getSummaryId(), "W-018");
		assertEquals( entity.getBody().getData()[0].getDescription(), "Unpainted Wagon Body");
		assertEquals( entity.getBody().getData()[1].getId(), 7 );
		assertEquals( entity.getBody().getData()[1].getSummaryId(), "W-019");
		assertEquals( entity.getBody().getData()[1].getDescription(), "Red Paint");
	}
	

	@Test
	public void searchForAll() throws Exception {
		String url = "http://localhost:" + this.port + "/"+ SearchItemRequest.queryUrl + "?id=&summaryId=&description=";
		
		ResponseEntity<ItemResponse> entity 
		   = this.restTemplate.getForEntity( url, ItemResponse.class );
		
		assertEquals(entity.getStatusCode(), HttpStatus.OK );
		assertEquals( 0, entity.getBody().getErrors().size() );
		assertEquals( 7, entity.getBody().getData().length );
		
		assertEquals( entity.getBody().getData()[0].getSummaryId(), "W-001");
		assertEquals( entity.getBody().getData()[1].getSummaryId(), "W-002");
		assertEquals( entity.getBody().getData()[2].getSummaryId(), "W-003");
		assertEquals( entity.getBody().getData()[3].getSummaryId(), "W-004");
		assertEquals( entity.getBody().getData()[4].getSummaryId(), "W-005");
		assertEquals( entity.getBody().getData()[5].getSummaryId(), "W-018");
		assertEquals( entity.getBody().getData()[6].getSummaryId(), "W-019");
	}
	
	@Test
	public void searchByDescription() throws Exception {
		

		String url = "http://localhost:" + this.port + "/"+ SearchItemRequest.queryUrl + "?id=&summaryId=&description=Wagon";
		
		ResponseEntity<ItemResponse> entity 
		   = this.restTemplate.getForEntity( url, ItemResponse.class );
		
		assertEquals(entity.getStatusCode(), HttpStatus.OK );
		assertEquals( entity.getBody().getErrors().size(), 0 );
		
		assertEquals( entity.getBody().getData().length, 3 );
		
		assertEquals( entity.getBody().getData()[0].getId(), 1 );
		assertEquals( entity.getBody().getData()[0].getSummaryId(), "W-001");
		assertEquals( entity.getBody().getData()[0].getDescription(), "36 In Red Wagon" );

		assertEquals( entity.getBody().getData()[1].getId(), 2 );
		assertEquals( entity.getBody().getData()[1].getSummaryId(), "W-002");
		assertEquals( entity.getBody().getData()[1].getDescription(), "Painted Wagon Body");

		assertEquals( entity.getBody().getData()[2].getId(), 6 );
		assertEquals( entity.getBody().getData()[2].getSummaryId(), "W-018");
		assertEquals( entity.getBody().getData()[2].getDescription(), "Unpainted Wagon Body");
	}
	
	@Test
	public void addItem() throws Exception {
		

		String url = "http://localhost:" + this.port + "/"+ SearchItemRequest.queryUrl + "?id=&summaryId=&description=Wagon";
		
		ResponseEntity<ItemResponse> entity 
		   = this.restTemplate.getForEntity( url, ItemResponse.class );
		
		assertEquals(entity.getStatusCode(), HttpStatus.OK );
		assertEquals( entity.getBody().getErrors().size(), 0 );
		
		assertEquals( entity.getBody().getData().length, 3 );
		
		assertEquals( entity.getBody().getData()[0].getId(), 1 );
		assertEquals( entity.getBody().getData()[0].getSummaryId(), "W-001");
		assertEquals( entity.getBody().getData()[0].getDescription(), "36 In Red Wagon" );

		assertEquals( entity.getBody().getData()[1].getId(), 2 );
		assertEquals( entity.getBody().getData()[1].getSummaryId(), "W-002");
		assertEquals( entity.getBody().getData()[1].getDescription(), "Painted Wagon Body");

		assertEquals( entity.getBody().getData()[2].getId(), 6 );
		assertEquals( entity.getBody().getData()[2].getSummaryId(), "W-018");
		assertEquals( entity.getBody().getData()[2].getDescription(), "Unpainted Wagon Body");
	}

	
	@Test
	public void tooManyParameters() {
		

		String url = "http://localhost:" + this.port + "/"+ SearchItemRequest.queryUrl + "?id=2&summaryId=&description=Wagon";
		
		ResponseEntity<ItemResponse> entity 
		   = this.restTemplate.getForEntity( url, ItemResponse.class );
		
		assertEquals(entity.getStatusCode(), HttpStatus.OK );
		assertEquals( entity.getBody().getErrors().size(), 1 );
		
		assertEquals( entity.getBody().getData().length, 0 );
		assertEquals( entity.getBody().getErrors().get(0).getMessage(), Message.ITEM_SEARCH_PARAMETERS );
		
	}


}
