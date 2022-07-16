package com.app.recomendaciones.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.app.recomendaciones.models.Recomendaciones;
import com.app.recomendaciones.repository.RecomendacionesRepository;
import com.app.recomendaciones.response.Muro;
import com.app.recomendaciones.response.Proyectos;
import com.app.recomendaciones.services.IMuroServices;
import com.app.recomendaciones.services.IRecomendacionService;

@RestController
public class RecomendacionesController {

	@Autowired
	RecomendacionesRepository rRepository;

	@Autowired
	IRecomendacionService rService;

	@Autowired
	IMuroServices mServices;

//  ****************************	RECOMENDACIONES 	***********************************  //

	// MICROSERVICIO USUARIOS -> CREAR RECOMENDACION
	@PostMapping("/recomendaciones/crear/")
	public Boolean crearRecomendacion(@RequestParam("username") String username,
			@RequestParam("listInterests") List<String> listInterests,
			@RequestParam("listLocation") List<Double> listLocation) throws IOException {
		try {
			rService.crear(username, listInterests, listLocation);
			return true;
		} catch (Exception e) {
			throw new IOException("Error en creacion, recomendacion: " + e.getMessage());
		}
	}

	// MICROSERVICIO USUARIOS -> EDITAR USERNAME RECOMENDACION
	@PutMapping("/recomendaciones/editar/{username}")
	public Boolean editUser(@PathVariable("username") String username, @RequestParam("newUsername") String newUsername,
			@RequestParam("listaInterests") List<String> listaInterests) throws IOException {
		try {
			rService.editar(username, newUsername, listaInterests);
			return true;
		} catch (Exception e) {
			throw new IOException("Error en editar usuario, estadistica: " + e.getMessage());
		}
	}

	// MICROSERVICIO USUARIOS -> EDITAR UBICACION RECOMENDACION
	@PutMapping("/recomendaciones/usuario/editar/ubicacion/{username}")
	public Boolean editarUbicacion(@PathVariable("username") String username,
			@RequestParam("listaLocation") List<Double> listaLocation) throws IOException {
		try {
			rService.editarUbicaciom(username, listaLocation);
			return true;
		} catch (Exception e) {
			throw new IOException("Error en editar ubicacion, recomendaciones: " + e.getMessage());
		}
	}

	// LISTAR RECOMENDACIONES
	@GetMapping("/recomendaciones/listar/")
	@ResponseStatus(code = HttpStatus.OK)
	public List<Recomendaciones> listarRecomendaciones() {
		return rService.listar();
	}

	// MOSTRAR MUROS POR CERCANIA
	@GetMapping("/recomendaciones/muro/{username}")
	@ResponseStatus(code = HttpStatus.OK)
	public List<Muro> ubicacionMuro(@PathVariable("username") String username) {
		return mServices.obtenerMuros(username);
	}

	// MOSTRAR PROYECTOS POR CERCANIA
	@GetMapping("/recomendaciones/proyectos/{username}")
	@ResponseStatus(code = HttpStatus.OK)
	public List<Proyectos> ubicacionProyectos(@PathVariable("username") String username) {
		return rService.verProyectosUbicacion(username);
	}

	// MICROSERVICIO USUARIOS -> ELIMINAR RECOMENDACION
	@DeleteMapping("/recomendaciones/eliminar/{username}")
	public Boolean eliminarRecomendacion(@PathVariable("username") String username) throws IOException {
		try {
			rService.eliminar(username);
			return true;
		} catch (Exception e) {
			throw new IOException("Error en eliminacion, recomendacion: " + e.getMessage());
		}
	}

	// MICROSERVICIO USUARIOS -> ELIMINAR TODA RECOMENDACION
	@DeleteMapping("/recomendaciones/eliminar/all/usuarios/")
	public Boolean eliminarAllUsuario() throws IOException {
		try {
			rRepository.deleteAll();
			return true;
		} catch (Exception e) {
			throw new IOException("Error: " + e.getMessage());
		}
	}

}
