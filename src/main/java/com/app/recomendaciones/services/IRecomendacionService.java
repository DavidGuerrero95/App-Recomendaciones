package com.app.recomendaciones.services;

import java.util.List;

import com.app.recomendaciones.models.Proyectos;
import com.app.recomendaciones.models.Recomendaciones;

public interface IRecomendacionService {

	public List<Proyectos> obtenerUbicacionProyecto(List<Proyectos> proyectos, Recomendaciones usuario);

	public Double distanciaCoord(List<Double> ubicacion, List<Double> localizacion);

}
