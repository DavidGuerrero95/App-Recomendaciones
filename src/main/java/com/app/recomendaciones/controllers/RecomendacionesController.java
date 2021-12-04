package com.app.recomendaciones.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.app.recomendaciones.clients.MuroFeignClient;
import com.app.recomendaciones.clients.ProyectosFeignClient;
import com.app.recomendaciones.models.Muro;
import com.app.recomendaciones.models.Proyectos;
import com.app.recomendaciones.models.Recomendaciones;
import com.app.recomendaciones.repository.MuroRepository;
import com.app.recomendaciones.repository.ProyectosRepository;
import com.app.recomendaciones.repository.RecomendacionesRepository;
import com.app.recomendaciones.services.IRecomendacionService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@RestController
public class RecomendacionesController {

	private final Logger logger = LoggerFactory.getLogger(RecomendacionesController.class);

	@Autowired
	RecomendacionesRepository rRepository;

	@SuppressWarnings("rawtypes")
	@Autowired
	private CircuitBreakerFactory cbFactory;

	@Autowired
	ProyectosFeignClient pClient;

	@Autowired
	ProyectosRepository pRepository;

	@Autowired
	IRecomendacionService rService;

	@Autowired
	MuroFeignClient mClient;

	@Autowired
	MuroRepository mRepository;

	@GetMapping("/recomendaciones/listar/")
	@ResponseStatus(code = HttpStatus.OK)
	public List<Recomendaciones> listarRecomendaciones() {
		return rRepository.findAll();
	}

	@PostMapping("/recomendaciones/proyecto/crear/")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Boolean anadirProyectos(@RequestBody Proyectos proyecto) throws IOException {
		try {
			pRepository.save(proyecto);
			return true;
		} catch (Exception e) {
			throw new IOException("Error crear proyecto, recomendaciones: " + e.getMessage());
		}

	}

	@PostMapping("/recomendaciones/muro/crear/")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Boolean anadirMuro(@RequestBody Muro muro) throws IOException {
		try {
			mRepository.save(muro);
			return true;
		} catch (Exception e) {
			throw new IOException("Error crear muro, recomendaciones: " + e.getMessage());
		}
	}

	@DeleteMapping("/recomendaciones/proyectos/eliminar/")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Boolean deleteProyectos(@RequestParam("nombre") String nombre) throws IOException {
		try {
			Proyectos p = pRepository.findByNombre(nombre);
			pRepository.delete(p);
			return true;
		} catch (Exception e) {
			throw new IOException("Error eliminar proyectos, recomendaciones: " + e.getMessage());
		}
	}

	@DeleteMapping("/recomendaciones/muro/eliminar/")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Boolean deleteMuro(@RequestParam("nombre") Integer nombre) throws IOException {
		try {
			Muro m = mRepository.findByCodigoMuro(nombre);
			mRepository.delete(m);
			return true;
		} catch (Exception e) {
			throw new IOException("Error eliminar muro, recomendaciones: " + e.getMessage());
		}
	}

	@PostMapping("/recomendaciones/crear/")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Boolean crearRecomendacion(@RequestParam("username") String username,
			@RequestParam("listInterests") List<String> listInterests,
			@RequestParam("listLocation") List<Double> listLocation) throws IOException {
		Recomendaciones r = new Recomendaciones();
		r.setUsername(username);
		r.setBusquedas(new ArrayList<String>());
		r.setIntereses(listInterests);
		r.setUbicacion(listLocation);
		try {
			rRepository.save(r);
			return true;
		} catch (Exception e) {
			throw new IOException("Error en creacion, recomendacion: " + e.getMessage());
		}
	}

	@DeleteMapping("/recomendaciones/eliminar/{username}")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean eliminarRecomendacion(@PathVariable("username") String username) throws IOException {
		try {
			Recomendaciones r = rRepository.findByUsername(username);
			rRepository.delete(r);
			return true;
		} catch (Exception e) {
			throw new IOException("Error en eliminacion, recomendacion: " + e.getMessage());
		}
	}

	@PutMapping("/estadistica/usuario/editar/{username}")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Boolean editUser(@PathVariable("username") String username, @RequestParam("newUsername") String newUsername,
			@RequestParam("listaInterests") List<String> listaInterests) throws IOException {
		try {
			Recomendaciones r = rRepository.findByUsername(username);
			if (!newUsername.isEmpty() && listaInterests.isEmpty())
				r.setUsername(newUsername);
			else if (newUsername.isEmpty() && !listaInterests.isEmpty())
				r.setIntereses(listaInterests);
			rRepository.save(r);
			return true;
		} catch (Exception e) {
			throw new IOException("Error en editar usuario, estadistica: " + e.getMessage());
		}

	}

	@PutMapping("/recomendaciones/usuario/editar/ubicacion/{username}")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean editarUbicacion(@PathVariable("username") String username,
			@RequestParam("listaLocation") List<Double> listaLocation) throws IOException {
		try {
			Recomendaciones r = rRepository.findByUsername(username);
			r.setUbicacion(listaLocation);
			rRepository.save(r);
			return true;
		} catch (Exception e) {
			throw new IOException("Error en editar ubicacion, recomendaciones: " + e.getMessage());
		}
	}

	@GetMapping("/recomendaciones/muro/ubicacion/{username}")
	@ResponseStatus(code = HttpStatus.OK)
	@CircuitBreaker(name = "muro", fallbackMethod = "obtenerListaMuros")
	public List<Muro> ubicacionMuro(@PathVariable("username") String username) {
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

	@GetMapping("/recomendaciones/proyectos/ubicacion/{username}")
	@ResponseStatus(code = HttpStatus.OK)
	public List<Proyectos> ubicacionProyectos(@PathVariable("username") String username) {
		List<Proyectos> proyectos = cbFactory.create("proyecto").run(() -> pClient.getProyectos(),
				e -> buscarProyectoAlternativo(e));
		Recomendaciones usuario = rRepository.findByUsername(username);
		return rService.obtenerUbicacionProyecto(proyectos, usuario);
	}

	@PutMapping("/recomendaciones/busqueda/editar/{username}")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean editarBusqueda(@PathVariable("username") String username, @RequestParam String busqueda)
			throws IOException {
		try {
			Recomendaciones r = rRepository.findByUsername(username);
			List<String> busquedas = r.getBusquedas();
			if (!busquedas.contains(busqueda)) {
				busquedas.add(busqueda);
				r.setBusquedas(busquedas);
				rRepository.save(r);
			}
			return true;
		} catch (Exception e) {
			throw new IOException("error editar busqueda, recomendaciones: " + e.getMessage());
		}

	}

	private List<Proyectos> buscarProyectoAlternativo(Throwable e) {
		logger.info(e.getMessage());
		return pRepository.findAll();
	}

	@SuppressWarnings("unused")
	private List<Muro> obtenerListaMuros(String username, Throwable e) {
		logger.info(e.getMessage());
		return mRepository.findAll();
	}
}
