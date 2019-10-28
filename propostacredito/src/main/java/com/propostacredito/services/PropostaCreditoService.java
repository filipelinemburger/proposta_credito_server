package com.propostacredito.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.propostacredito.dto.RetornoAnalisePropostaDTO;
import com.propostacredito.entities.PropostaCredito;
import com.propostacredito.enums.EstadoCivil;
import com.propostacredito.enums.Limite;
import com.propostacredito.enums.PadraoRenda;
import com.propostacredito.enums.Resultado;
import com.propostacredito.repositories.PropostaCreditoRepository;

@Service
public class PropostaCreditoService {

	@Autowired
	private PropostaCreditoRepository repository;

	public List<PropostaCredito> listarPropostas() {
		return (List<PropostaCredito>) repository.findAll();
	}

	public PropostaCredito buscarPropostaPorCpf(String cpf) {
		cpf = removeMascaraCpf(cpf);
		return repository.findByCpf(cpf);
	}

	@SuppressWarnings("static-access")
	public RetornoAnalisePropostaDTO analisarResultadoELimiteProposta(PropostaCredito proposta) {
		PadraoRenda padraoRenda = retornaPadraoRenda(proposta);
		if (padraoRenda == padraoRenda.BAIXA) {
			setNegadoRendaBaixa(proposta);
		} else {

			switch (proposta.estadoCivil) {
			case EstadoCivil.SOLTEIRO:
				validarClienteSolteiro(proposta, padraoRenda);
				break;
			case EstadoCivil.CASADO:
				validarClienteCasado(proposta, padraoRenda);
				break;
			default:
				if (padraoRenda == PadraoRenda.MEDIA_BAIXA) {
					setNegadoPolitica(proposta);
				} else if (padraoRenda == PadraoRenda.MEDIA) {
					if (proposta.getQtdDependentes() > 0) {
						setNegadoPolitica(proposta);
					} else {
						setAprovado100a500(proposta);
					}
				} else if (padraoRenda == PadraoRenda.MEDIA_ALTA) {
					if (proposta.getQtdDependentes() > 1) {
						setAprovado1000a1500(proposta);
					} else {
						setAprovado1500a2000(proposta);
					}
				} else {
					setAprovadoSuperior2000(proposta);
				}
			}			
		}
		return salvarProposta(proposta);
	}

	private RetornoAnalisePropostaDTO salvarProposta(PropostaCredito proposta) {
		try {			
			proposta.setCpf(removeMascaraCpf(proposta.getCpf()));
			repository.save(proposta);
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			return new RetornoAnalisePropostaDTO("CPF já cadastrado ", null);
		}
		String mensagemRetorno = proposta.resultado == "Aprovado" ?
				"Proposta aprovada. Limite liberado: " + proposta.limite : "Proposta negada. Motivo: " + proposta.limite;
		return new RetornoAnalisePropostaDTO(mensagemRetorno, proposta);
	}

	@SuppressWarnings("static-access")
	private void validarClienteSolteiro(PropostaCredito proposta, PadraoRenda padraoRenda) {
		if (padraoRenda == padraoRenda.MEDIA_BAIXA) {
			setAprovado100a500(proposta);
		} else if (padraoRenda == padraoRenda.MEDIA) {
			if (proposta.getQtdDependentes() == 0) {
				setAprovado500a1000(proposta);
			} else {
				setAprovado100a500(proposta);
			}
		} else {
			if (proposta.getQtdDependentes() <= 2) {
				setAprovadoSuperior2000(proposta);
			} else {
				setAprovado1500a2000(proposta);
			}
		}
	}

	@SuppressWarnings("static-access")
	private void validarClienteCasado(PropostaCredito proposta, PadraoRenda padraoRenda) {
		if (padraoRenda == padraoRenda.MEDIA_BAIXA) {
			if (proposta.getQtdDependentes() > 0) {
				setNegadoPolitica(proposta);
			} else {
				setAprovado100a500(proposta);
			}
		} else if (padraoRenda == padraoRenda.MEDIA) {
			if (proposta.getQtdDependentes() > 0) {
				setAprovado500a1000(proposta);
			} else {
				setAprovado100a500(proposta);
			}
		} else if (padraoRenda == padraoRenda.MEDIA_ALTA) {
			if (proposta.getQtdDependentes() >= 2) {
				setAprovado1500a2000(proposta);
			} else {
				setAprovadoSuperior2000(proposta);
			}
		} else {
			setAprovadoSuperior2000(proposta);
		}
	}

	private void setAprovadoSuperior2000(PropostaCredito proposta) {
		proposta.resultado = Resultado.APROVADO;
		proposta.limite = Limite.SUPERIOR_2000;
	}

	private void setAprovado1500a2000(PropostaCredito proposta) {
		proposta.resultado = Resultado.APROVADO;
		proposta.limite = Limite.ENTRE_1500_2000;
	}

	private void setAprovado1000a1500(PropostaCredito proposta) {
		proposta.resultado = Resultado.APROVADO;
		proposta.limite = Limite.ENTRE_1000_1500;
	}

	private void setAprovado500a1000(PropostaCredito proposta) {
		proposta.resultado = Resultado.APROVADO;
		proposta.limite = Limite.ENTRE_500_1000;
	}

	private void setAprovado100a500(PropostaCredito proposta) {
		proposta.resultado = Resultado.APROVADO;
		proposta.limite = Limite.ENTRE_100_500;
	}

	private void setNegadoPolitica(PropostaCredito proposta) {
		proposta.resultado = Resultado.NEGADO;
		proposta.limite = Limite.REPROVADO_POLITICA_CREDITO;
	}

	private void setNegadoRendaBaixa(PropostaCredito proposta) {
		proposta.resultado = Resultado.NEGADO;
		proposta.limite = Limite.RENDA_BAIXA;
	}

	private PadraoRenda retornaPadraoRenda(PropostaCredito proposta) {
		if (proposta.getRenda() < 1000) {
			return PadraoRenda.BAIXA;			
		} else if (proposta.getRenda() >= 1000 && proposta.getRenda() <= 2000) {
			return PadraoRenda.MEDIA_BAIXA;			
		} else if (proposta.getRenda() > 2000 && proposta.getRenda() <= 5000) {
			return PadraoRenda.MEDIA;			
		} else if (proposta.getRenda() > 5000 && proposta.getRenda() <= 8000) {
			return PadraoRenda.MEDIA_ALTA;			
		}
		return PadraoRenda.ALTA;
	}
	
	public String removeMascaraCpf(String cpf) {
		return cpf.replaceAll("[^0-9]","");
	}
}
