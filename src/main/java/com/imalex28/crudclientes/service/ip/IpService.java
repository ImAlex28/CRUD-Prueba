package com.imalex28.crudclientes.service.ip;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.imalex28.crudclientes.apiclient.IPApiClient;
import com.imalex28.crudclientes.dto.ip.ExternalIPDTO;
import com.imalex28.crudclientes.dto.ip.IPResponseDTO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class IpService {
	@Inject 
	@RestClient 
	IPApiClient client;
	
	public IPResponseDTO getIP(String ip, String token){
						
			ExternalIPDTO externalResult = client.getIP(ip,token);
	
			IPResponseDTO ipResponse = new IPResponseDTO();
			ipResponse.setIp(externalResult.getIp());
			ipResponse.setCountry(externalResult.getCountry());
			ipResponse.setAs_name(externalResult.getAs_name());
			ipResponse.setAs_domain(externalResult.getAs_domain());

			return ipResponse;
		}
}
