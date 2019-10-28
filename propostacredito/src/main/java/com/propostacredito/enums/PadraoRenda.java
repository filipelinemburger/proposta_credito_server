package com.propostacredito.enums;

public enum PadraoRenda {
	
	BAIXA(1),
	MEDIA_BAIXA(2),
	MEDIA(3),
	MEDIA_ALTA(4),
	ALTA(5);
	
	private int valor;
	
	PadraoRenda(int valor) {
		this.valor = valor;
	}

	public int getValor() {
		return valor;
	}
}
