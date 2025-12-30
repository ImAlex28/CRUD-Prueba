
package com.imalex28.crudclientes.controller;

import com.imalex28.crudclientes.dto.CuentaRequestDTO;
import com.imalex28.crudclientes.dto.CuentaResponseDTO;
import com.imalex28.crudclientes.dto.CuentaUpdateDTO;
import com.imalex28.crudclientes.mapper.ClienteRequestMapper;
import com.imalex28.crudclientes.mapper.ClienteResponseMapper;
import com.imalex28.crudclientes.mapper.CuentaRequestMapper;
import com.imalex28.crudclientes.mapper.CuentaResponseMapper;
import com.imalex28.crudclientes.mapper.CuentaUpdateMapper;
import com.imalex28.crudclientes.model.Cuenta;
import com.imalex28.crudclientes.service.ClienteService;
import com.imalex28.crudclientes.service.CuentaService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CuentaControllerUnitTest {

    @Mock CuentaService cuentaService;
    @Mock ClienteService clienteService;
    @Mock CuentaRequestMapper cuentaRequestMapper;
    @Mock CuentaResponseMapper cuentaResponseMapper;
    @Mock CuentaUpdateMapper cuentaUpdateMapper;
    @Mock ClienteRequestMapper clienteRequestMapper;
    @Mock ClienteResponseMapper clienteResponseMapper;

    @InjectMocks
    CuentaController controller;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listAll_devuelve_lista_dtos() {
        // given
        Cuenta c1 = new Cuenta(); c1.setIdCuenta(1L);
        Cuenta c2 = new Cuenta(); c2.setIdCuenta(2L);
        Mockito.when(cuentaService.findAll()).thenReturn(List.of(c1, c2));

        CuentaResponseDTO dto1 = new CuentaResponseDTO(); dto1.setIdCuenta(1L);
        CuentaResponseDTO dto2 = new CuentaResponseDTO(); dto2.setIdCuenta(2L);
        Mockito.when(cuentaResponseMapper.toCuentaResponseDTOList(List.of(c1, c2)))
                .thenReturn(List.of(dto1, dto2));

        // when
        List<CuentaResponseDTO> result = controller.listAll();

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getIdCuenta());
        assertEquals(2L, result.get(1).getIdCuenta());
        Mockito.verify(cuentaService).findAll();
        Mockito.verify(cuentaResponseMapper).toCuentaResponseDTOList(List.of(c1, c2));
    }

    @Test
    void findById_mapea_corretamente() {
        Long id = 99L;
        Cuenta cuenta = new Cuenta(); cuenta.setIdCuenta(id);
        Mockito.when(cuentaService.findById(id)).thenReturn(cuenta);

        CuentaResponseDTO dto = new CuentaResponseDTO(); dto.setIdCuenta(id);
        Mockito.when(cuentaResponseMapper.toCuentaResponseDTO(cuenta)).thenReturn(dto);

        CuentaResponseDTO result = controller.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getIdCuenta());
        Mockito.verify(cuentaService).findById(id);
        Mockito.verify(cuentaResponseMapper).toCuentaResponseDTO(cuenta);
    }

    @Test
    void create_usa_mapper_y_service_devuelve_201() {
        CuentaRequestDTO req = new CuentaRequestDTO();
        Cuenta cuenta = new Cuenta();
        Mockito.when(cuentaRequestMapper.toCuenta(req, clienteService)).thenReturn(cuenta);

        Response resp = controller.create(req);

        assertNotNull(resp);
        assertEquals(201, resp.getStatus());
        Mockito.verify(cuentaRequestMapper).toCuenta(req, clienteService);
        Mockito.verify(cuentaService).save(cuenta);
    }

    @Test
    void update_usa_mapper_y_service_devuelve_200() {
        CuentaUpdateDTO req = new CuentaUpdateDTO();
        Cuenta cuenta = new Cuenta();
        Mockito.when(cuentaUpdateMapper.toCuenta(req, clienteService)).thenReturn(cuenta);

        Response resp = controller.update(req);

        assertNotNull(resp);
        assertEquals(200, resp.getStatus());
        Mockito.verify(cuentaUpdateMapper).toCuenta(req, clienteService);
        Mockito.verify(cuentaService).update(cuenta);
    }

    @Test
    void delete_llama_service_y_devuelve_204() {
        Response resp = controller.delete(5L);

        assertNotNull(resp);
        assertEquals(204, resp.getStatus());
        Mockito.verify(cuentaService).delete(5L);
    }

    @Test
    void findById_si_service_lanza_excepcion_se_propaga() {
        Long id = 123L;
        Mockito.when(cuentaService.findById(id)).thenThrow(new RuntimeException("no encontrado"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.findById(id));
        assertEquals("no encontrado", ex.getMessage());
    }
}
