package com.app.recomendaciones.response;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Muro {

	@Id
	@JsonIgnore
	private String id;

	@Indexed(unique = true)
	private Integer codigoMuro;

	@NotBlank(message = "ubicacion cannot be null")
	private List<Double> localizacion;

	private List<Integer> idProyectos;

}