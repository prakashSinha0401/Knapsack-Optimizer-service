package com.domain.kos.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.domain.kos.entity.Kos;
import com.domain.kos.model.KnapsackSolutionDTO;
import com.domain.kos.model.ProblemDTO;
import com.domain.kos.repository.KosRepository;
import com.domain.kos.utility.KosUtils;


@Service
public class KosService {
	
	private KosRepository kosRepository;
	
	private static final String SUBMITTED = "submitted";
	private static final String STARTED = "started";
	private static final String COMPLETED = "completed";
	private static final String SUCCESS = "SUCCESS";
	private static final String FAILED = "FAILED";
	public static final Logger LOGGER = LoggerFactory.getLogger(KosService.class);
	
	public KosService(KosRepository kosRepository) {
		this.kosRepository = kosRepository;
	}
	
	@Scheduled(fixedRateString =  "${schedule.task}")
	public void scheduleJobToRun() {
		LOGGER.info("Inside schedule job");
		this.triggerSequentialEvents();
	}
	
	public KnapsackSolutionDTO persistKnapsackDetails(ProblemDTO problemObj) {
		KnapsackSolutionDTO solution = new KnapsackSolutionDTO();
		Map<String, Long> timestampMap = new LinkedHashMap<>();
		try {
			String task = KosUtils.generateRandomString();
			timestampMap.put(SUBMITTED, KosUtils.getCurrentUtcEpochTime());
			timestampMap.put(STARTED, null);
			timestampMap.put(COMPLETED, null);
			solution.setTask(task);
			solution.setProblem(problemObj.getProblem());
			solution.setStatus(SUBMITTED);
			solution.setTimestamps(timestampMap);
			solution.setSolution(new HashMap<>());
			this.saveToDb(solution);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return solution;
	}

	private void triggerSequentialEvents() {
		List<Kos> kosSubmittedList = this.getAllSubmittedTask();
		if (!CollectionUtils.isEmpty(kosSubmittedList)) {
			List<Kos> kosChangedStatusList = this.setStatusToStarted(kosSubmittedList);
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				LOGGER.error(e.getMessage(), e);
			}
			if (!CollectionUtils.isEmpty(kosChangedStatusList)) {
				kosChangedStatusList.stream().parallel().forEach(this::knapsackSolver);
			}
		}
	}
	
	private void knapsackSolver(Kos kosObj) {
		KnapsackSolutionDTO knapsackSolutionDTO = null;
		int capacity = 0;
		int [] values = null;
		int [] weights = null;
		try {
			knapsackSolutionDTO = KosUtils.getMapperObject().readValue(KosUtils.convertClobToString(kosObj.getSolution()), KnapsackSolutionDTO.class);
			if(null != knapsackSolutionDTO) {
				capacity = knapsackSolutionDTO.getProblem().getCapacity();
				values = knapsackSolutionDTO.getProblem().getValues();
				weights = knapsackSolutionDTO.getProblem().getWeights();
				Map<String, Object> resultMap = this.getKnapsackSolution(capacity, values, weights, values.length);
				if(!resultMap.isEmpty()) {
					knapsackSolutionDTO.setSolution(resultMap);
					knapsackSolutionDTO.setStatus(COMPLETED);
					knapsackSolutionDTO.getTimestamps().put(COMPLETED, KosUtils.getCurrentUtcEpochTime());
					this.saveToDb(knapsackSolutionDTO);
				}
			}
		}catch(Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public Map<String, Object> getKnapsackSolution(int W, int[] val, int[] wt, int n) {
		Map<String, Object> resultMap = new HashMap<>();
		List<Integer> integerList = new ArrayList<>();
		int i, w;
		int K[][] = new int[n + 1][W + 1];
		try {
			// Build table K[][] in bottom up manner
			for (i = 0; i <= n; i++) {
				for (w = 0; w <= W; w++) {
					if (i == 0 || w == 0)
						K[i][w] = 0;
					else if (wt[i - 1] <= w)
						K[i][w] = Math.max(val[i - 1] + K[i - 1][w - wt[i - 1]], K[i - 1][w]);
					else
						K[i][w] = K[i - 1][w];
				}
			}

			// stores the result of Knapsack
			int res = K[n][W];
			int finalRes = res;
			w = W;
			for (i = n; i > 0 && res > 0; i--) {

				// either the result comes from the top
				// (K[i-1][w]) or from (val[i-1] + K[i-1]
				// [w-wt[i-1]]) as in Knapsack table. If
				// it comes from the latter one/ it means
				// the item is included.
				if (res == K[i - 1][w])
					continue;
				else {

					// This item is included.
					integerList.add(i - 1);

					// Since this weight is included its
					// value is deducted
					res = res - val[i - 1];
					w = w - wt[i - 1];
				}
			}
			resultMap.put("packed_items", integerList);
			resultMap.put("total_value", finalRes);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return resultMap;
	}
	
	public List<Kos> getAllSubmittedTask() {
		List<Kos> kosSubmittedList = null;
		try {
			kosSubmittedList = kosRepository.findByStatus(SUBMITTED);
		} catch (Exception e) {
			kosSubmittedList = new ArrayList<>();
			LOGGER.error(e.getMessage(), e);
		}
		return kosSubmittedList;
	}
  
	public List<Kos> setStatusToStarted(List<Kos> kosSubmittedList) {
		List<Kos> kosListToPersist = new ArrayList<>();
		KnapsackSolutionDTO knapsackSolutionDTO = null;
		try {
			for (Kos kosObj : kosSubmittedList) {
				knapsackSolutionDTO = KosUtils.getMapperObject().readValue(KosUtils.convertClobToString(kosObj.getSolution()),
						KnapsackSolutionDTO.class);
				if (null != knapsackSolutionDTO) {
					kosObj.setStatus(STARTED);
					knapsackSolutionDTO.setStatus(STARTED);
					knapsackSolutionDTO.getTimestamps().put(STARTED, KosUtils.getCurrentUtcEpochTime());
					kosObj.setSolution(KosUtils.convertStringToClob(KosUtils.getMapperObject().writeValueAsString(knapsackSolutionDTO)));
					kosListToPersist.add(kosObj);
				}
			}
			if (!CollectionUtils.isEmpty(kosListToPersist)) {
				kosRepository.saveAll(kosListToPersist);
				LOGGER.info("Status changed to Started for list of size : {}", kosListToPersist.size());
			} else
				LOGGER.info("No data to persist");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return kosListToPersist;
	}
	
	public KnapsackSolutionDTO fetchFromDbOnTask(String task){
		KnapsackSolutionDTO knapsackSolutionDTO = null;
		try {
		Optional<Kos> kos = kosRepository.findById(task);
		if (kos.isPresent()) {
			Kos kosObj = kos.get();
			knapsackSolutionDTO = KosUtils.getMapperObject().readValue(KosUtils.convertClobToString(kosObj.getSolution()), KnapsackSolutionDTO.class);
		}
		else LOGGER.info("No record found for taskId = {}", task);
		}catch(Exception e) {
			knapsackSolutionDTO = new KnapsackSolutionDTO();
			LOGGER.error(e.getMessage(), e);
		}
		return knapsackSolutionDTO;
	}
	
	private String saveToDb(KnapsackSolutionDTO solution) {
		String outputAsJson = null;
		String saveStatus = FAILED;
		Kos kos = new Kos();
		Kos kosObj = null;
		if (null != solution) {
			try {
				kos.setTask(solution.getTask());
				kos.setStatus(solution.getStatus());
				outputAsJson = KosUtils.getMapperObject().writeValueAsString(solution);
				kos.setSolution(KosUtils.convertStringToClob(outputAsJson));
				kosObj = kosRepository.save(kos);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		if (null != kosObj) {
			LOGGER.info("data persisted to Db for taskId : {}", solution.getTask());
			saveStatus = SUCCESS;
		}
		else LOGGER.info("No data persisted to Db for taskId : {}", solution.getTask());
		return saveStatus;
	}
	
}
