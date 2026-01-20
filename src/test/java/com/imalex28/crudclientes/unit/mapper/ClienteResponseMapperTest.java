package com.imalex28.crudclientes.unit.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.imalex28.crudclientes.dto.client.ClienteResponseDTO;
import com.imalex28.crudclientes.mapper.ClientResponseMapper;
import com.imalex28.crudclientes.model.Client;

public class ClienteResponseMapperTest {
	  // Instanciamos el mapper
	  private final ClientResponseMapper mapper = Mappers.getMapper(ClientResponseMapper.class);

	  @Test
	  void toClienteResponseDTO_mapeaTodosLosCampos() {
	    // Arrange
	    Client entity = new Client();
	    entity.setClientId((long) 123);
	    entity.setName("Alejandro");
	    entity.setSurname("Fernandez");
	    entity.setEmail("alejandro@example.com");
	    entity.setDni("49875412T");

	    // Act
	    ClienteResponseDTO dto = mapper.toClienteResponseDTO(entity);

	    // Assert
	    assertNotNull(entity);
	    assertEquals(dto.getNombre(), entity.getName());
	    assertEquals(dto.getApellidos(), entity.getSurname());
	    assertEquals(dto.getEmail(), entity.getEmail());
	    assertEquals(dto.getDni(), entity.getDni());

	  }


	  @Test
	  void toClienteResponseDTO_manejaNulo() {
	    Client entity = null;
	
	    ClienteResponseDTO dto = mapper.toClienteResponseDTO(entity);
	
	    assertNull(dto);

	  }

	  @Test
	  void toClienteResponseDTOList_mapeaColeccion() {
	    Client a = new Client(); a.setClientId(1L); a.setName("A"); a.setSurname("Uno"); a.setEmail("a@correo.es");
	    Client b = new Client(); b.setClientId(2L); b.setName("B"); b.setSurname("Dos"); b.setEmail("b@correo.es");
	
	    List<ClienteResponseDTO> list = mapper.toClienteResponseDTOList(List.of(a, b));
	
	    assertNotNull(list);
	    assertEquals(2, list.size());
	
	    assertEquals(1L, list.get(0).getId());
	    assertEquals("A", list.get(0).getNombre());
	    assertEquals("Uno", list.get(0).getApellidos());
	    assertEquals("a@correo.es", list.get(0).getEmail());
	
	    assertEquals(2L, list.get(1).getId());
	    assertEquals("B", list.get(1).getNombre());
	    assertEquals("Dos", list.get(1).getApellidos());
	    assertEquals("b@correo.es", list.get(1).getEmail());
	  }
	
	  @Test
	  void toClienteResponseDTOList_manejaListaVacia() {
	    List<ClienteResponseDTO> list = mapper.toClienteResponseDTOList(List.of());
	    assertNotNull(list);
	    assertTrue(list.isEmpty());
	  }
	  
	  @Test
	  void toClienteResponseDTOList_manejaNull() {
	    List<ClienteResponseDTO> list = mapper.toClienteResponseDTOList(null);
	    assertNull(list);
	  }
}
