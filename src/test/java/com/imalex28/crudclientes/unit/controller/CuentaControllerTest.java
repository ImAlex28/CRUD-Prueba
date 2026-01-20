
package com.imalex28.crudclientes.unit.controller;

import com.imalex28.crudclientes.controller.CuentaController;
import com.imalex28.crudclientes.dto.account.CuentaRequestDTO;
import com.imalex28.crudclientes.dto.account.CuentaResponseDTO;
import com.imalex28.crudclientes.dto.account.CuentaUpdateDTO;
import com.imalex28.crudclientes.mapper.BankAccountRequestMapper;
import com.imalex28.crudclientes.mapper.BankAccountResponseMapper;
import com.imalex28.crudclientes.mapper.BankAccountUpdateMapper;
import com.imalex28.crudclientes.mapper.ClientRequestMapper;
import com.imalex28.crudclientes.mapper.ClientResponseMapper;
import com.imalex28.crudclientes.model.BankAccount;
import com.imalex28.crudclientes.service.BankAccountService;
import com.imalex28.crudclientes.service.ClientService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CuentaControllerUnitTest {

    @Mock BankAccountService cuentaService;
    @Mock ClientService clienteService;
    @Mock BankAccountRequestMapper cuentaRequestMapper;
    @Mock BankAccountResponseMapper cuentaResponseMapper;
    @Mock BankAccountUpdateMapper cuentaUpdateMapper;
    @Mock ClientRequestMapper clienteRequestMapper;
    @Mock ClientResponseMapper clienteResponseMapper;

    @InjectMocks
    CuentaController controller;

    @Test
    void listAll_devuelve_lista_dtos() {
    	BankAccount c1 = new BankAccount(); c1.setBankAccountId(1L);
    	BankAccount c2 = new BankAccount(); c2.setBankAccountId(2L);
        when(cuentaService.findAll()).thenReturn(List.of(c1, c2));

        CuentaResponseDTO dto1 = new CuentaResponseDTO(); dto1.setIdCuenta(1L);
        CuentaResponseDTO dto2 = new CuentaResponseDTO(); dto2.setIdCuenta(2L);
        when(cuentaResponseMapper.toCuentaResponseDTOList(List.of(c1, c2)))
                .thenReturn(List.of(dto1, dto2));

        List<CuentaResponseDTO> result = controller.listAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getIdCuenta());
        assertEquals(2L, result.get(1).getIdCuenta());
        verify(cuentaService).findAll();
        verify(cuentaResponseMapper).toCuentaResponseDTOList(List.of(c1, c2));
    }

    @Test
    void findById_mapea_corretamente() {
        Long id = 99L;
        BankAccount cuenta = new BankAccount(); cuenta.setBankAccountId(id);
        when(cuentaService.findById(id)).thenReturn(cuenta);

        CuentaResponseDTO dto = new CuentaResponseDTO(); dto.setIdCuenta(id);
        when(cuentaResponseMapper.toCuentaResponseDTO(cuenta)).thenReturn(dto);

        CuentaResponseDTO result = controller.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getIdCuenta());
        verify(cuentaService).findById(id);
        verify(cuentaResponseMapper).toCuentaResponseDTO(cuenta);
    }
    
    @Test
    void findById_si_service_lanza_excepcion_se_propaga() {
        Long id = 123L;
        when(cuentaService.findById(id)).thenThrow(new RuntimeException("no encontrado"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.findById(id));
        assertEquals("no encontrado", ex.getMessage());
    }

    @Test
    void create_usa_mapper_y_service_devuelve_201() {
        CuentaRequestDTO req = new CuentaRequestDTO();
        BankAccount cuenta = new BankAccount();
        when(cuentaRequestMapper.toCuenta(req, clienteService)).thenReturn(cuenta);

        Response resp = controller.create(req);

        assertNotNull(resp);
        assertEquals(201, resp.getStatus());
        verify(cuentaRequestMapper).toCuenta(req, clienteService);
        verify(cuentaService).save(cuenta);
    }

    @Test
    void update_usa_mapper_y_service_devuelve_200() {
        CuentaUpdateDTO req = new CuentaUpdateDTO();
        BankAccount cuenta = new BankAccount();
        Mockito.when(cuentaUpdateMapper.toCuenta(req, clienteService)).thenReturn(cuenta);

        Response resp = controller.update(req);

        assertNotNull(resp);
        assertEquals(200, resp.getStatus());
        verify(cuentaUpdateMapper).toCuenta(req, clienteService);
        verify(cuentaService).update(cuenta);
    }

    @Test
    void delete_llama_service_y_devuelve_204() {
        Response resp = controller.delete(5L);

        assertNotNull(resp);
        assertEquals(204, resp.getStatus());
        verify(cuentaService).delete(5L);
    }

}
