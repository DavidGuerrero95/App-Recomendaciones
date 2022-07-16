package com.app.recomendaciones.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.app.recomendaciones.clients.ProyectosFeignClient;
import com.app.recomendaciones.models.Recomendaciones;
import com.app.recomendaciones.repository.RecomendacionesRepository;
import com.app.recomendaciones.response.Proyectos;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RecomendacionService implements IRecomendacionService {

	@SuppressWarnings("rawtypes")
	@Autowired
	private CircuitBreakerFactory cbFactory;

	@Autowired
	RecomendacionesRepository rRepository;

	@Autowired
	ProyectosFeignClient pClient;

	@Override
	public List<Proyectos> obtenerUbicacionProyecto(List<Proyectos> proyectos, Recomendaciones usuario) {
		List<Proyectos> retorno = new ArrayList<Proyectos>();
		List<Double> posDistancia = new ArrayList<Double>();
		List<Integer> posMenor = new ArrayList<Integer>();
		if (usuario.getUbicacion().size() > 1) {
			for (int i = 0; i < proyectos.size(); i++) {
				posDistancia.add(distanciaCoord(usuario.getUbicacion(), proyectos.get(i).getUbicacion()));
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
		} else {
			Double theta = lon1 - lon2;
			Double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2))
					+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
			dist = Math.acos(dist);
			dist = Math.toDegrees(dist);
			dist = dist * 60 * 1.1515;
			dist = dist * 1.609344;
			return dist;
		}
	}

	@Override
	public List<Recomendaciones> listar() {
		return rRepository.findAll();
	}

	@Override
	public void crear(String username, List<String> listInterests, List<Double> listLocation) {
		Recomendaciones r = new Recomendaciones();
		r.setUsername(username);
		r.setIntereses(listInterests);
		r.setUbicacion(listLocation);
		rRepository.save(r);
	}

	@Override
	public void eliminar(String username) {
		rRepository.deleteByUsername(username);
	}

	@Override
	public void editar(String username, String newUsername, List<String> listaInterests) {
		Recomendaciones r = rRepository.findByUsername(username);
		if (!newUsername.isEmpty() && listaInterests.isEmpty())
			r.setUsername(newUsername);
		else if (newUsername.isEmpty() && !listaInterests.isEmpty())
			r.setIntereses(listaInterests);
		rRepository.save(r);
	}

	@Override
	public void editarUbicaciom(String username, List<Double> listaLocation) {
		Recomendaciones r = rRepository.findByUsername(username);
		r.setUbicacion(listaLocation);
		rRepository.save(r);
	}

	@Override
	public List<Proyectos> verProyectosUbicacion(String username) {
		List<Proyectos> proyectos = cbFactory.create("proyecto").run(() -> pClient.getProyectos(),
				e -> buscarProyectoAlternativo(e));
		Recomendaciones usuario = rRepository.findByUsername(username);
		return obtenerUbicacionProyecto(proyectos, usuario);
	}

//  ****************************	FUNCIONES TOLERANCIA A FALLOS	***********************************  //

	private List<Proyectos> buscarProyectoAlternativo(Throwable e) {
		log.info(e.getMessage());
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Servicio de proyectos no esta disponible");
	}

}
