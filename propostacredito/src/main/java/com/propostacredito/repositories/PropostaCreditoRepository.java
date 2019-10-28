package com.propostacredito.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.propostacredito.entities.PropostaCredito;

@Repository
public interface PropostaCreditoRepository extends JpaRepository<PropostaCredito, Long> {
	PropostaCredito findByCpf(@Param("cpf") String cpf);
}
