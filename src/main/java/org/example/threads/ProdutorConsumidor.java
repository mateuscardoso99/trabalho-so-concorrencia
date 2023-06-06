package org.example.threads;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.example.model.LugarSolicitacao;
import org.example.model.Solicitacao;
import org.example.model.Lugar.Situacao;
import org.example.utils.DateFormatter;

public class ProdutorConsumidor{
    private LugarSolicitacao lugarSolicitacao;
    private Logger logger = Logger.getLogger("org.example.threads");

    public ProdutorConsumidor(LugarSolicitacao lugarSolicitacao){
        this.lugarSolicitacao = lugarSolicitacao;
    }

    /**
     * antes de produzir, verifica na coleção de lugares do teatro se o lugar desejado está livre
     * se já tiver reservado não produz, se não, marca o assento como ocupado e insere na lista de solicitações
     * @param solicitacao
     * @return boolean
     * @throws InterruptedException
     */
    public Boolean produzir(Solicitacao solicitacao) throws InterruptedException{
        logger.log(Level.INFO,"Thread Produtor ID: "+Thread.currentThread().getId()+" INICIADA.");

        //sincroniza no objeto, se tiver 2 requisições simultaneas para lugares diferentes
        //ambas as threads entrarão ao mesmo tempo nesse bloco pois possuem referencias para objetos Lugar diferentes.
        //se for para o mesmo assento então as requisições possuem a mesma referencia do objeto Lugar
        //o que fará com que apenas uma entre no bloco abaixo por vez
        synchronized(solicitacao.getLugar()){
            if(solicitacao.getLugar().getSituacao().equals(Situacao.OCUPADO)){
                System.out.println("Erro ao produzir, lugar "+solicitacao.getLugar().getNumero()+" já reservado.\n\n");
                return Boolean.FALSE;
            }
            
            lugarSolicitacao.getLugares().forEach(lugar -> {
                if(lugar.getNumero() == solicitacao.getLugar().getNumero()){
                    lugar.setSituacao(Situacao.OCUPADO);
                }
            });
        }

        //somente uma thread vai inserir no buffer(ArrayList de solicitações lugarSolicitacao.getSolicitacoes()) por vez
        //por causa do bloqueio no buffer (lugarSolicitacao.getSolicitacoes())
        synchronized(lugarSolicitacao.getSolicitacoes()){
            lugarSolicitacao.getSolicitacoes().add(solicitacao);
            logger.log(Level.INFO,"Produtor produziu solicitação para o assento "+solicitacao.getLugar().getNumero());
            lugarSolicitacao.getSolicitacoes().notify();
            logger.log(Level.INFO,"Thread Produtor ID: "+Thread.currentThread().getId()+" ENCERRADA.\n\n");
            return Boolean.TRUE;
        }

    }
    
    /**
     * consumidor é acordado apartir do momento que o produtor produz uma solicitação na lista
     * então ele grava no arquivo cada valor da lista, depois limpa a lista e volta a dormir
     * é acordada através do notify(), se tiver vários notify() em sequência só o primeiro acorda essa thread e os outros não vão ter efeito nenhum
     * @throws InterruptedException
     * @throws IOException
     */
    public void consumir() throws InterruptedException, IOException{
        logger.log(Level.INFO,"Thread Consumidor "+Thread.currentThread().getId()+" INICIADA.");

        while(true){
            Solicitacao s;

            //somente uma thread por vez entra nesse bloco e remove a posição [0] do buffer
            //e depois grava no arquivo, se outra thread entrar nesse bloco depois
            //esse elemento removido não estará mais no ArrayList de solicitações
            //pois as mudanças nas variaveis dentro de um bloco syncronized são comitadas
            //na MP quando o bloco é encerrado
            synchronized(lugarSolicitacao.getSolicitacoes()){
                while(lugarSolicitacao.getSolicitacoes().isEmpty()){
                    logger.log(Level.INFO,"\n\n------DORMINDO------\n\n\n");
                    lugarSolicitacao.getSolicitacoes().wait();
                }
            
                logger.log(Level.INFO,"\n\n------Consumidor Acordou------\n");

                s = lugarSolicitacao.getSolicitacoes().remove(0);
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter("log.txt",true));
            //lugarSolicitacao.getSolicitacoes().stream().forEach(s -> {
            try {
                writer.append(
                    "Acento: "+s.getLugar().getNumero()+
                    ", Nome: "+s.getNomeSolicitante()+
                    ", Data: "+DateFormatter.dateTimeFormatter(s.getData())+
                    ", IP: "+s.getIp()
                );
                writer.append(System.lineSeparator());
            } catch (IOException e) {
                e.printStackTrace();
            }

            logger.log(Level.INFO,"Assento "+s.getLugar().getNumero()+" Consumido");
            //});
                
            writer.close();
            //lugarSolicitacao.getSolicitacoes().clear();
            //Thread.sleep(100);
        }
        
    }
}
