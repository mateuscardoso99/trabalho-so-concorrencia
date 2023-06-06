package org.example.model;

public class Lugar {
    private Integer numero;
    private Situacao situacao;

    public enum Situacao{
        LIVRE,
        OCUPADO
    }

    public Lugar(Integer numero, Situacao situacao){
        this.numero = numero;
        this.situacao = situacao;
    }

    public Integer getNumero(){
        return this.numero;
    }
    public void setNumero(Integer numero){
        this.numero = numero;
    }
    public Situacao getSituacao(){
        return this.situacao;
    }
    public void setSituacao(Situacao situacao){
        this.situacao = situacao;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((numero == null) ? 0 : numero.hashCode());
        result = prime * result + ((situacao == null) ? 0 : situacao.hashCode());
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
        Lugar other = (Lugar) obj;
        if (numero == null) {
            if (other.numero != null)
                return false;
        } else if (!numero.equals(other.numero))
            return false;
        if (situacao != other.situacao)
            return false;
        return true;
    }
}
