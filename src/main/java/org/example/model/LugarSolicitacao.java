package org.example.model;

import java.util.List;

public class LugarSolicitacao {
    private List<Lugar> lugares;
	private List<Solicitacao> solicitacoes;
    
    public LugarSolicitacao(List<Lugar> lugares, List<Solicitacao> solicitacoes) {
        this.lugares = lugares;
        this.solicitacoes = solicitacoes;
    }

    public List<Lugar> getLugares() {
        return lugares;
    }

    public void setLugares(List<Lugar> lugares) {
        this.lugares = lugares;
    }

    public List<Solicitacao> getSolicitacoes() {
        return solicitacoes;
    }

    public void setSolicitacoes(List<Solicitacao> solicitacoes) {
        this.solicitacoes = solicitacoes;
    }
   
    @Override
    public int hashCode() {
        final int prime = 31; 
        int result = 1;
        result = prime * result + ((lugares == null) ? 0 : lugares.hashCode());
        result = prime * result + ((solicitacoes == null) ? 0 : solicitacoes.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LugarSolicitacao other = (LugarSolicitacao) obj;
        if (lugares == null) {
            if (other.lugares != null)
                return false;
        } else if (!lugares.equals(other.lugares))
            return false;
        if (solicitacoes == null) {
            if (other.solicitacoes != null)
                return false;
        } else if (!solicitacoes.equals(other.solicitacoes))
            return false;
        return true;
    }
}
