package com.app.recomendaciones.services;

import java.util.List;

import com.app.recomendaciones.response.Muro;

public interface IMuroServices {

	List<Muro> obtenerMuros(String username);

}
