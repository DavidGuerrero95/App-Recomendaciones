package com.app.recomendaciones.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.app.recomendaciones.clients.MuroFeignClient;
import com.app.recomendaciones.models.Recomendaciones;
import com.app.recomendaciones.repository.RecomendacionesRepository;
import com.app.recomendaciones.response.Muro;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MuroServices implements IMuroServices {

	@Autowired
	RecomendacionesRepository rRepository;

	@Autowired
	MuroFeignClient mClient;

	@Autowired
	IRecomendacionService rService;

	@CircuitBreaker(name = "muro", fallbackMethod = "obtenerListaMuros")
	@Override
	public List<Muro> obtenerMuros(String username) {
		List<Muro> muro = mClient.getMuros();
		Recomendaciones usuario = rRepository.findByUsername(username);
		List<Double> posDistancia = new ArrayList<Double>();
		List<Integer> posMenor = new ArrayList<Integer>();
		List<Muro> retorno = new ArrayList<Muro>();
		if (usuario.getUbicacion().size() > 1) {
			for (int i = 0; i < muro.size(); i++) {
				posDistancia.add(rService.distanciaCoord(usuario.getUbicacion(), muro.get(i).getLocalizacion()));
			}
			for (int i = 0; i < posDistancia.size(); i++) {
				Double Menor = posDistancia.stream().min(Comparator.naturalOrder()).get();
				posMenor.add(posDistancia.indexOf(Menor));
				posDistancia.set(posDistancia.indexOf(Menor), 100000.0);
			}

			for (int i = 0; i < posMenor.size(); i++) {
				retorno.add(muro.get(posMenor.get(i)));
			}
			return retorno;
		} else {
			return muro;
		}
	}

//  ****************************	FUNCIONES TOLERANCIA A FALLOS	***********************************  //

	@SuppressWarnings("unused")
	private List<Muro> obtenerListaMuros(String username, Throwable e) {
		log.info(e.getMessage());
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Servicio de muro no esta disponible");
	}

}
