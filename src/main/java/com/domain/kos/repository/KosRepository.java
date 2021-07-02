package com.domain.kos.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.domain.kos.entity.Kos;

@Repository
public interface KosRepository extends CrudRepository<Kos, String>{
	
	public List<Kos> findByStatus(String status);
	

}
