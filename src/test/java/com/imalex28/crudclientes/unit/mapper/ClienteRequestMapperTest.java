package com.imalex28.crudclientes.unit.mapper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.imalex28.crudclientes.dto.client.ClienteRequestDTO;
import com.imalex28.crudclientes.mapper.ClienteRequestMapper;
import com.imalex28.crudclientes.model.Cliente;

class ClienteRequestMapperTest {

  // Instanciamos el mapper
  private final ClienteRequestMapper mapper = Mappers.getMapper(ClienteRequestMapper.class);

  @Test
  void toCliente_mapeaCamposBasicos_yIgnoraIdAutogenerado() {
    // Arrange
    ClienteRequestDTO dto = new ClienteRequestDTO();
    dto.setNombre("Alejandro");
    dto.setApellidos("Fernandez");
    dto.setEmail("alejandro@example.com");
    dto.setDni("74587458N");

    // Act
    Cliente entity = mapper.toCliente(dto);

    // Assert
    assertNotNull(entity);
    assertEquals("Alejandro", entity.getNombre());
    assertEquals("Fernandez", entity.getApellidos());
    assertEquals("alejandro@example.com", entity.getEmail());
    assertEquals("74587458N", entity.getDni());

    // Debe ignorar idCliente
    assertNull(entity.getIdCliente(), "idCliente debe permanecer null");
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
    Cliente entity = mapper.toCliente(dto);

    // Assert: simplemente refleja los nulos
    assertNotNull(entity);
    assertNull(entity.getNombre());
    assertNull(entity.getApellidos());
    assertNull(entity.getEmail());
    assertNull(entity.getIdCliente());
    assertNull(entity.getDni());
  }
  
  @Test
  void toCliente_manejaDtoNulo() {
    // Arrange: DTO con algunos nulos
    ClienteRequestDTO dto = null;

    // Act
    Cliente entity = mapper.toCliente(dto);

    // Assert: simplemente refleja los nulos
    assertNull(entity);
  }

}
