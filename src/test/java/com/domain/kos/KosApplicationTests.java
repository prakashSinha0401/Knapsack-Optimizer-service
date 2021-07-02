package com.domain.kos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.domain.kos.model.ProblemDTO;
import com.domain.kos.service.KosService;
import com.domain.kos.utility.KosUtils;

@SpringBootTest
class KosApplicationTests {

	public static final Logger LOGGER = LoggerFactory.getLogger(KosService.class);
	@Autowired
	private KosService kosService;
	
	@Test
	void testKnapsackLogic() {
		String problem = "{\n"
				+ "\"problem\" : {\n"
				+ "  \"capacity\" : 100,\n"
				+ "  \"weights\" : [10, 50,20,33, 150],\n"
				+ "  \"values\" : [10,34,3,20, 78]\n"
				+ "}\n"
				+ "}";
		try {
			ProblemDTO problemObj = KosUtils.getMapperObject().readValue(problem, ProblemDTO.class);
			Map<String, Object> result = kosService.getKnapsackSolution(problemObj.getProblem().getCapacity(),
					problemObj.getProblem().getValues(), problemObj.getProblem().getWeights(),
					problemObj.getProblem().getValues().length);
			assertEquals(result.get("total_value"), 64);
		}catch(Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}
