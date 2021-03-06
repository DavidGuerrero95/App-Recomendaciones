package com.app.recomendaciones.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "recomendaciones")
@Data
@NoArgsConstructor
public class Recomendaciones {

	@Id
	@JsonIgnore
	private String id;

	@Indexed(unique = true)
	private String username;

	private List<String> intereses;
	private List<Double> ubicacion;

}
