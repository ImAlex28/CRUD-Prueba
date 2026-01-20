package com.imalex28.crudclientes.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.imalex28.crudclientes.model.Client;
import com.imalex28.crudclientes.model.BankAccount;
import com.imalex28.crudclientes.repository.ClientRepository;
import com.imalex28.crudclientes.repository.BankAccountRepository;
import com.imalex28.crudclientes.service.BankAccountService;

@ExtendWith(MockitoExtension.class)
public class CuentaServiceTest {


	  @Mock BankAccountRepository cuentaRepository;
	  @Mock ClientRepository clienteRepository;

	  @InjectMocks BankAccountService service;


	  

	  // ---------- findAll ----------
	  @Test
	  void findAll_devuelveListaDelRepositorio() {
		  BankAccount c1 = new BankAccount(); c1.setBankAccountId(1L);
		  BankAccount c2 = new BankAccount(); c2.setBankAccountId(2L);
		  when(cuentaRepository.findAll()).thenReturn(List.of(c1,c2));
		  
		  List<BankAccount> result = service.findAll();
		  
		  assertNotNull(result);
		  assertEquals(2,result.size());
		  assertEquals(1L,result.get(0).getBankAccountId());
		  assertEquals(2L,result.get(1).getBankAccountId());
		  verify(cuentaRepository, times(1)).findAll();
		  verifyNoMoreInteractions(cuentaRepository, clienteRepository);
		  
	  }
	  
	  // ---------- findById ----------
	  @Test
	  void findById_devuelveClienteDelRepositorio() {
		  BankAccount c1 = new BankAccount(); c1.setBankAccountId(1L);

		  when(cuentaRepository.findById(1L)).thenReturn(c1);
		  
		  BankAccount result = service.findById(1L);
		  
		  assertNotNull(result);
		  assertEquals(1L,result.getBankAccountId());
		  assertInstanceOf(BankAccount.class, result);
		  verify(cuentaRepository, times(1)).findById(1L);
		  verifyNoMoreInteractions(cuentaRepository, clienteRepository);
		  
	  }

	  // ---------- save (OK) ---------- 
	  @Test
	  void save_ok_validaClienteExiste_yGuarda() {
	    // Arrange
	    Client cliente = new Client(); cliente.setClientId(123L);
	    BankAccount cuenta = new BankAccount();
	    cuenta.setClient(cliente);
	    cuenta.setAccountNumber("ES12 3456 7890 1234 5678 9012");
	    cuenta.setAccountType("AHORRO");
	    cuenta.setBalance(1500.75);
	
	    when(clienteRepository.existsById(123L)).thenReturn(true);
	
	    // Act
	    service.save(cuenta);
	
	    // Assert
	    verify(clienteRepository, times(1)).existsById(123L);
	    verify(cuentaRepository, times(1)).save(cuenta);
	    verifyNoMoreInteractions(cuentaRepository, clienteRepository);
	  }
	  

	  // ---------- save (KO: cliente no existe) ----------
	  @Test
	  void save_clienteNoExiste_lanzaIAE_yNoGuarda() {
	    Client cliente = new Client(); cliente.setClientId(999L);
	    BankAccount cuenta = new BankAccount(); cuenta.setClient(cliente);

	    when(clienteRepository.existsById(999L)).thenReturn(false);
	    

	    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
	        () -> service.save(cuenta));

	    assertEquals("El cliente no existe", ex.getMessage());
	    verify(clienteRepository).existsById(999L);
	    verify(cuentaRepository, never()).save(any());
	    verifyNoMoreInteractions(cuentaRepository, clienteRepository);
	  }
	  

	  // ---------- update (OK) ----------
	  @Test
	  void update_ok_validaClienteYCuenta_Actualiza() {
		Client cliente = new Client(); cliente.setClientId(5L);
		BankAccount cuenta = new BankAccount(); cuenta.setClient(cliente); cuenta.setBankAccountId(7L);
		  
		when(clienteRepository.existsById(5L)).thenReturn(true);
		when(cuentaRepository.existsById(7L)).thenReturn(true);
	
	    service.update(cuenta);

		verify(clienteRepository).existsById(5L);
		verify(cuentaRepository).existsById(7L);
		verify(cuentaRepository).update(cuenta);
		verifyNoMoreInteractions(cuentaRepository, clienteRepository);
	  }
	  

	  // ---------- update (KO: cliente no existe) ----------

	  @Test
	  void update_clienteNoExiste() {
		Client cliente = new Client(); cliente.setClientId(999L);
		BankAccount cuenta = new BankAccount(); cuenta.setClient(cliente); cuenta.setBankAccountId(7L);
		  
	    when(clienteRepository.existsById(999L)).thenReturn(false);
	
	    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
		        () -> service.update(cuenta));
	    
	    assertEquals("El cliente no existe", ex.getMessage());
	    verify(clienteRepository).existsById(999L);
	    verify(cuentaRepository, never()).update(any());
	    verifyNoMoreInteractions(cuentaRepository, clienteRepository);
	  }
	  

	  // ---------- update (KO: cuenta no existe) ----------

	  @Test
	  void update_clienteExiste_CuentaNoExiste() {
		Client cliente = new Client(); cliente.setClientId(5L);
		BankAccount cuenta = new BankAccount(); cuenta.setClient(cliente); cuenta.setBankAccountId(999L);
		  
	    when(clienteRepository.existsById(5L)).thenReturn(true);
	    when(cuentaRepository.existsById(999L)).thenReturn(false);
	
	    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
		        () -> service.update(cuenta));
	    
	    assertEquals("La cuenta no existe", ex.getMessage());
	    verify(clienteRepository).existsById(5L);
	    verify(cuentaRepository).existsById(999L);
	    verify(cuentaRepository, never()).update(any());
	    verifyNoMoreInteractions(cuentaRepository, clienteRepository);
	  }
	  

	  // ---------- delete (OK) ----------

	  @Test
	  void delete_ok_cuentaExiste() {
	    when(cuentaRepository.existsById(77L)).thenReturn(true);

	    service.delete(77L);

	    verify(cuentaRepository).existsById(77L);
	    verify(cuentaRepository).delete(77L);
	    verifyNoMoreInteractions(cuentaRepository, clienteRepository);
 
	  }
	  

	  // ---------- delete (KO: cuenta no existe) ----------
	  @Test
	  void delete_cuentaNoExiste_lanzaIAE_yNoElimina() {
	    when(cuentaRepository.existsById(77L)).thenReturn(false);

	    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
	        () -> service.delete(77L));

	    assertEquals("La cuenta no existe", ex.getMessage());
	    verify(cuentaRepository).existsById(77L);
	    verify(cuentaRepository, never()).delete(anyLong());
	    verifyNoMoreInteractions(cuentaRepository, clienteRepository);
	  }


	  // ---------- findByIdCliente ----------
	  @Test
	  void findByIdCliente_devuelveListaDelRepositorio() {
		BankAccount c1 = new BankAccount(); c1.setBankAccountId(1L);
	    when(cuentaRepository.findByIdCliente(10L)).thenReturn(List.of(c1));

	    List<BankAccount> result = service.findByIdCliente(10L);

	    assertEquals(1, result.size());
	    assertEquals(1L, result.get(0).getBankAccountId());
	    verify(cuentaRepository).findByIdCliente(10L);
	    verifyNoMoreInteractions(cuentaRepository, clienteRepository);
	  }

	  // ---------- getSaldoTotalByCliente ----------
	  @Test
	  void getSaldoTotalByCliente_devuelveTotalDelRepositorio() {
	    when(cuentaRepository.getSaldoTotalByCliente(10L)).thenReturn(2500.0);

	    Double total = service.getSaldoTotalByCliente(10L);

	    assertEquals(2500.0, total);
	    verify(cuentaRepository).getSaldoTotalByCliente(10L);
	    verifyNoMoreInteractions(cuentaRepository, clienteRepository);
	  }

}
