package com.app.recomendaciones.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.app.recomendaciones.models.Recomendaciones;

public interface RecomendacionesRepository extends MongoRepository<Recomendaciones, String> {

	@RestResource(path = "buscar")
	public Recomendaciones findByUsername(@Param("nombre") String nombre);

	@RestResource(path = "exist-user")
	public Boolean existsByUsername(@Param("username") String username);
}
