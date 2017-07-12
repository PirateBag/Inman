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
import com.inman.model.rest.ItemDeleteRequest;




@RunWith( SpringRunner.class)
@SpringBootTest(classes = ItemAddAndDelete.class )
public class ItemAddAndDelete {

	private int port= 8080;

	private RestTemplate restTemplate = new RestTemplate();

	
	@Test
	public void addItem() throws Exception {
		

		//  First insert the item and verify correct insertion.
		String url = "http://localhost:" + this.port + "/"+ ItemAddRequest.addUrl + "?summaryId=W-666&description=Devils_Hearse&unitCost=6.66";
		ResponseEntity<ItemResponse> entity 
		   = this.restTemplate.getForEntity( url, ItemResponse.class );
		
		assertEquals( entity.getStatusCode(), HttpStatus.OK );
		
		assertEquals( 1, entity.getBody().getData().length );
		//  Need the insert to provide the ID of the inserted record back...
		assertNotEquals( entity.getBody().getData()[0].getId(),  0 );
		assertEquals( entity.getBody().getData()[0].getSummaryId(), "W-666");
		assertEquals( entity.getBody().getData()[0].getDescription(), "Devils_Hearse");
		assertEquals( entity.getBody().getData()[0].getUnitCost(), 6.66, 0.1 );

		//  Now delete the inserted item.
		url = "http://localhost:" + this.port + "/"+ ItemDeleteRequest.deleteUrl + "?id=" + entity.getBody().getData()[ 0 ].getId();
		entity = this.restTemplate.getForEntity( url, ItemResponse.class );
		
		assertEquals(entity.getStatusCode(), HttpStatus.OK );
		assertEquals( entity.getBody().getErrors().size(), 0 );
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
