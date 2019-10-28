package com.propostacredito.controllers;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.propostacredito.dto.RetornoAnalisePropostaDTO;
import com.propostacredito.entities.PropostaCredito;
import com.propostacredito.services.PropostaCreditoService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("proposta")
@Api(value = "API REST de Propostas")
public class PropostaCreditoController {
	
	@Autowired
	private PropostaCreditoService service;
	
	@GetMapping("/lista")
	@ApiOperation(value = "Retorna a listagem de todas as propostas cadastradas")
	@CrossOrigin(origins = "http://localhost:4200")
    public Collection<PropostaCredito> listarPropostas() {
		System.out.println("listar-propostas");
        return service.listarPropostas();
    }
	
	@GetMapping("/consulta-por-cpf")
	@ApiOperation(value = "Consulta uma proposta com base no CPF do cliente")
	@CrossOrigin(origins = "http://localhost:4200")
    public PropostaCredito consultaPorCpf(@RequestParam String cpf) {
		System.out.println("Consulta pelo CPF: " + cpf);
		PropostaCredito retorno = service.buscarPropostaPorCpf(cpf);
		return retorno;
    }
	
	@PostMapping("nova-proposta")
	@ApiOperation(value = "Envia para análise uma nova proposta de crédito")
	@CrossOrigin(origins = "http://localhost:4200")
	public RetornoAnalisePropostaDTO analisarCredito(@RequestBody PropostaCredito proposta) {
		return service.analisarResultadoELimiteProposta(proposta);
	}
	
	

}
