package com.domain.kos.contoller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.domain.kos.model.KnapsackSolutionDTO;
import com.domain.kos.model.ProblemDTO;
import com.domain.kos.service.KosService;

@RestController
public class KosController {
	
	private KosService kosService;
	
	public KosController(KosService kosService) {
		this.kosService = kosService;
	}
	
	@PostMapping(value = "/knapsack")
	public KnapsackSolutionDTO persistKnapsackDetails(@RequestBody ProblemDTO problemObj) {
		 return kosService.persistKnapsackDetails(problemObj);
	}
	
	@GetMapping(value = "/knapsack/{taskId}", produces = "application/json")
	public Object getSolutionOnTask(@PathVariable("taskId") String task) {
		KnapsackSolutionDTO obj = kosService.fetchFromDbOnTask(task);
		if(null == obj) {
			return "No record found for taskId = "+task;
		}
		return obj;
		
	}

}
