package ca.christophersaunders.demos.crest;

import org.codegist.crest.annotate.EndPoint;
import org.codegist.crest.annotate.ContextPath;
import org.codegist.crest.annotate.Path;
import org.codegist.crest.annotate.HttpMethod;
import org.codegist.crest.annotate.Destination;

import java.util.List;

// TODO: static imports of BODY, POST, PUT, etc
import static org.codegist.crest.HttpMethod.POST;
import static org.codegist.crest.HttpMethod.PUT;
import static org.codegist.crest.HttpMethod.DELETE;
import static org.codegist.crest.config.Destination.BODY;

@EndPoint("http://oacrestdemo.heroku.com")
@ContextPath("/api")
public interface PlantsService {
		
	@Path("/plants/count.json")
	int getCount();

	@Path("/plants.json")
	List<Plant> getPlants();

	@HttpMethod(POST)
	@Path("/plants.json")
	@Destination(BODY)
	Plant createPlant(Plant plant);

	@Path("/plants/{0}.json")
	Plant getPlant(int plantNumber);

	@HttpMethod(DELETE)
	@Path("/plants/{0}.json")
	boolean digUpPlant(int plantNumber);

	@HttpMethod(PUT)
	@Path("/plants/{0}.json")
	Plant updatePlant(
		int plantNumber, 
		@Destination(BODY)
		Plant plant);
}