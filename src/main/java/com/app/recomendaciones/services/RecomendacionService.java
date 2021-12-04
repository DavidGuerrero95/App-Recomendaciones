package com.app.recomendaciones.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.app.recomendaciones.models.Proyectos;
import com.app.recomendaciones.models.Recomendaciones;

@Service
public class RecomendacionService implements IRecomendacionService {

	@Override
	public List<Proyectos> obtenerUbicacionProyecto(List<Proyectos> proyectos, Recomendaciones usuario) {
		List<Proyectos> retorno = new ArrayList<Proyectos>();
		List<Double> posDistancia = new ArrayList<Double>();
		List<Integer> posMenor = new ArrayList<Integer>();
		if (usuario.getUbicacion().size() > 1) {
			for (int i = 0; i < proyectos.size(); i++) {
				posDistancia
						.add(distanciaCoord(usuario.getUbicacion(), proyectos.get(i).getLocalizacion()));
				}
			for (int i = 0; i < posDistancia.size(); i++) {
				Double Menor = posDistancia.stream().min(Comparator.naturalOrder()).get();
				posMenor.add(posDistancia.indexOf(Menor));
				posDistancia.set(posDistancia.indexOf(Menor), 100000.0);
			}
			for (int i = 0; i < posMenor.size(); i++) {
				retorno.add(proyectos.get(posMenor.get(i)));
			}
			return retorno;
		} else {
			return proyectos;
		}
	}
	

	public Double distanciaCoord(List<Double> pos1, List<Double> pos2) {  
		Double lat1 = pos1.get(0);
		Double lat2 = pos2.get(0);
		Double lon1 = pos1.get(1);
		Double lon2 = pos2.get(1);
		if ((lat1 == lat2) && (lon1 == lon2)) {
			return (double) 0;
		}
		else {
			Double theta = lon1 - lon2;
			Double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
			dist = Math.acos(dist);
			dist = Math.toDegrees(dist);
			dist = dist * 60 * 1.1515;
			dist = dist * 1.609344;
			return dist;
		} 
    }

}
