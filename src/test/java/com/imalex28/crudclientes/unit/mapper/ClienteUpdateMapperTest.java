package com.imalex28.crudclientes.unit.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.imalex28.crudclientes.dto.client.ClienteUpdateDTO;
import com.imalex28.crudclientes.mapper.ClientUpdateMapper;
import com.imalex28.crudclientes.model.Client;

public class ClienteUpdateMapperTest {
	
	  // Instanciamos el mapper
	private final ClientUpdateMapper mapper = Mappers.getMapper(ClientUpdateMapper.class);
	
	@Test
	void toCliente_mapeaCamposBasicos() {
	    // Arrange
	    ClienteUpdateDTO dto = new ClienteUpdateDTO();
	    dto.setId(101L);
	    dto.setNombre("Alejandro");
	    dto.setApellidos("Fernandez");
	    dto.setEmail("alejandro@example.com");
	    dto.setDni("74587458N");

	    // Act
	    Client entity = mapper.toCliente(dto);

	    // Assert
	    assertNotNull(entity);
	    assertEquals("Alejandro", entity.getName());
	    assertEquals("Fernandez", entity.getSurname());
	    assertEquals("alejandro@example.com", entity.getEmail());
	    assertEquals("74587458N", entity.getDni());
	    assertEquals(101L , entity.getClientId());

	  }
	
	@Test
	void toCliente_manejaNull() {
		ClienteUpdateDTO dto = null;
		
		Client entity = mapper.toCliente(dto);
		
		assertNull(entity);
	}
	

}
