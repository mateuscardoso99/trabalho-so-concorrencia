package org.example.model;

import java.time.LocalDateTime;
import java.util.Map;

public class Solicitacao {
    private String nomeSolicitante;
    private Lugar lugar;
    private LocalDateTime data;
    private String ip;

    public Solicitacao(Map<String, String> request, Lugar lugar){
        this.nomeSolicitante = request.get("nome");
        this.lugar = lugar;
        this.data = LocalDateTime.now();
        this.ip = request.get("ip");
    }

    public String getNomeSolicitante(){
        return this.nomeSolicitante;
    }
    public void setNomeSolicitante(String n){
        nomeSolicitante = n;
    }
    public Lugar getLugar(){
        return this.lugar;
    }
    public void setLugar(Lugar l){
        lugar = l;
    }
    public LocalDateTime getData(){
        return this.data;
    }
    public void setData(LocalDateTime data){
        this.data = data;
    }
    public String getIp(){
        return this.ip;
    }
    public void setIp(String ip){
        this.ip = ip;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nomeSolicitante == null) ? 0 : nomeSolicitante.hashCode());
        result = prime * result + ((lugar == null) ? 0 : lugar.hashCode());
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
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
        Solicitacao other = (Solicitacao) obj;
        if (nomeSolicitante == null) {
            if (other.nomeSolicitante != null)
                return false;
        } else if (!nomeSolicitante.equals(other.nomeSolicitante))
            return false;
        if (lugar == null) {
            if (other.lugar != null)
                return false;
        } else if (!lugar.equals(other.lugar))
            return false;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        if (ip == null) {
            if (other.ip != null)
                return false;
        } else if (!ip.equals(other.ip))
            return false;
        return true;
    }

    
}
