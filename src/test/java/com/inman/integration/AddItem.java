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
import com.inman.model.Item;
import com.inman.model.rest.ItemResponse;
import com.inman.model.rest.SearchItemRequest;
import com.inman.model.rest.ItemAddRequest;




@RunWith( SpringRunner.class)
@SpringBootTest(classes = AddItem.class )
public class AddItem {

	private int port= 8080;

	private RestTemplate restTemplate = new RestTemplate();

	
	@Test
	public void addItem() throws Exception {
		

		String url = "http://localhost:" + this.port + "/"+ ItemAddRequest.addUrl + "?summaryId=W-666&description=Devils_Hearse&unitCost=6.66";
		
		ResponseEntity<String> addEntity 
		   = this.restTemplate.getForEntity( url, String.class );
		
		assertEquals( addEntity.getStatusCode(), HttpStatus.OK );
		//  assertEquals( addEntity.getBody(), "OK" );
		
		
		url = "http://localhost:" + this.port + "/"+ SearchItemRequest.queryUrl + "?id=&summaryId=W-666&description=";
		
		ResponseEntity<ItemResponse>entity = this.restTemplate.getForEntity( url, ItemResponse.class );
		
		assertEquals(entity.getStatusCode(), HttpStatus.OK );
		assertEquals( entity.getBody().getErrors().size(), 0 );
		
		assertEquals( entity.getBody().getData().length, 1 );
		assertEquals( entity.getBody().getData()[0].getSummaryId(), "W-666");
		assertEquals( entity.getBody().getData()[0].getDescription(), "Devils_Hearse");
		assertEquals( entity.getBody().getData()[0].getUnitCost(), 6.66, 0.1 );
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
