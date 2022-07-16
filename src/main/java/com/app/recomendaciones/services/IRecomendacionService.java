package com.app.recomendaciones.services;

import java.util.List;

import com.app.recomendaciones.models.Recomendaciones;
import com.app.recomendaciones.response.Proyectos;

public interface IRecomendacionService {

	public List<Proyectos> obtenerUbicacionProyecto(List<Proyectos> proyectos, Recomendaciones usuario);

	public Double distanciaCoord(List<Double> ubicacion, List<Double> localizacion);

	public List<Recomendaciones> listar();

	public void crear(String username, List<String> listInterests, List<Double> listLocation);

	public void eliminar(String username);

	public void editar(String username, String newUsername, List<String> listaInterests);

	public void editarUbicaciom(String username, List<Double> listaLocation);

	public List<Proyectos> verProyectosUbicacion(String username);

}
