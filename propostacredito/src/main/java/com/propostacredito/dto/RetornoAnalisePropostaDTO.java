package com.propostacredito.dto;

import com.propostacredito.entities.PropostaCredito;

public class RetornoAnalisePropostaDTO {
	
	public PropostaCredito propostaCredito;
	public String mensagemRetorno;
	
	public RetornoAnalisePropostaDTO(String mensagem, PropostaCredito proposta) {
		this.propostaCredito = proposta;
		this.mensagemRetorno = mensagem;
	}

}
