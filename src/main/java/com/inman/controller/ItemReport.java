package com.inman.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.inman.business.*;
import com.inman.entity.ActivityState;
import com.inman.entity.BomPresent;
import com.inman.entity.Item;
import com.inman.model.request.ItemReportRequest;
import com.inman.model.response.ItemExplosionResponse;
import com.inman.model.response.ResponseType;
import com.inman.repository.BomPresentRepository;
import com.inman.repository.BomRepository;
import com.inman.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.lang.runtime.ObjectMethods;
import java.util.LinkedList;
import java.util.List;


@Configuration
@RestController
public class ItemReport {

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
    private BomPresentRepository bomPresentRepository;

	static Logger logger = LoggerFactory.getLogger( "controller: " + ItemReport.class );

	@CrossOrigin
    @RequestMapping( value = ItemReportRequest.EXPLOSION_URL,method = RequestMethod.POST,
    		consumes = "application/json",
    		produces = "application/json" )
    private ItemExplosionResponse handleItemExplosionRequest( @RequestBody ItemReportRequest itemReportRequest ) {
		List<String> cummulativeResponse = generateItemExplosionReport( itemReportRequest.getParentId(), null, 0 );

		ItemExplosionResponse rValue = new ItemExplosionResponse( cummulativeResponse.toArray(new String[0]));
		rValue.setResponseType( ResponseType.QUERY );
        return rValue;
     }

	private List<String> generateItemExplosionReport(long itemId ,
															  List<String> cumulativeResponse,
															  long parentLevel )
	{
		String lineContents = "%10d  %-10s  %-30s  %10.2f  %3s  %10d";
		String lineHeader =   "%10s  %-10s  %-30s  %10s  %3s  %10sd";

		if ( cumulativeResponse == null ) {
			cumulativeResponse = new LinkedList<>();
			var message = String.format( lineHeader,
					"Id", "Summary", "Description", "Unit Cost", "Sourcing", "Depth" );
			cumulativeResponse.add( message );
			logger.info( message );
		}

Item item = itemRepository.findById( itemId);

		if ( item == null ) {
			var message =  "item not found:" + itemId;
			cumulativeResponse.add( message );
			logger.error( message );
			return null;
		}

		var message = Common.spacesForLevel( parentLevel ) + String.format( lineContents,
				item.getId(),
				item.getSummaryId(),
				item.getDescription(),
				item.getUnitCost(),
				item.getSourcing(),
				item.getMaxDepth() );

		logger.info( message );
		cumulativeResponse.add( message );

		BomPresent[] components = bomPresentRepository.findByParentId( itemId );

		for (BomPresent bom : components ) {
			generateItemExplosionReport( bom.getChildId(), cumulativeResponse, parentLevel+ 1 );
		}


		return cumulativeResponse;
	}

}

