package com.imalex28.crudclientes.unit.mapper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.imalex28.crudclientes.dto.client.ClienteRequestDTO;
import com.imalex28.crudclientes.mapper.ClientRequestMapper;
import com.imalex28.crudclientes.model.Client;

class ClienteRequestMapperTest {

  // Instanciamos el mapper
  private final ClientRequestMapper mapper = Mappers.getMapper(ClientRequestMapper.class);

  @Test
  void toCliente_mapeaCamposBasicos_yIgnoraIdAutogenerado() {
    // Arrange
    ClienteRequestDTO dto = new ClienteRequestDTO();
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

    // Debe ignorar idCliente
    assertNull(entity.getClientId(), "idCliente debe permanecer null");
  }

  @Test
  void toCliente_manejaCamposNulos() {
    // Arrange: DTO con algunos nulos
    ClienteRequestDTO dto = new ClienteRequestDTO();
    dto.setNombre(null);
    dto.setApellidos(null);
    dto.setEmail(null);
    dto.setDni(null);

    // Act
    Client entity = mapper.toCliente(dto);

    // Assert: simplemente refleja los nulos
    assertNotNull(entity);
    assertNull(entity.getName());
    assertNull(entity.getSurname());
    assertNull(entity.getEmail());
    assertNull(entity.getClientId());
    assertNull(entity.getDni());
  }
  
  @Test
  void toCliente_manejaDtoNulo() {
    // Arrange: DTO con algunos nulos
    ClienteRequestDTO dto = null;

    // Act
    Client entity = mapper.toCliente(dto);

    // Assert: simplemente refleja los nulos
    assertNull(entity);
  }

}
