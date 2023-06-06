package org.example.webserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.example.model.Lugar;
import org.example.model.LugarSolicitacao;
import org.example.model.Solicitacao;
import org.example.model.Lugar.Situacao;
import org.example.threads.ProdutorConsumidor;
import org.example.threads.ProcessaRequisicao;

public class Main {
    private static final Integer LUGARES_TEATRO = 60;

    /**
     * na inicialização do servidor adiciona no teatro os lugares
     * depois cria o objeto que tem o produtor e consumidor e a lista com as solicitações
     * todas as threads que processam requisições usam o mesmo objeto do produtor/consumidor
     * e inicia o consumidor que começa dormindo pois a lista de solicitações está vazia
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        int porta = 8090;

        ServerSocket serverSocket = new ServerSocket(porta);
        System.out.println("servidor rodando na porta " + porta + " http://localhost:8090\n");

        List<Lugar> lugaresTeatro = new ArrayList<>();
        for(int i=1; i<=LUGARES_TEATRO; i++){
            lugaresTeatro.add(new Lugar(i, Situacao.LIVRE));
        }

        List<Solicitacao> solicitacoes = new ArrayList<>();

        LugarSolicitacao lugarSolicitacao = new LugarSolicitacao(lugaresTeatro, solicitacoes);

        ProdutorConsumidor log = new ProdutorConsumidor(lugarSolicitacao);

        Thread consumir = new Thread(new Runnable() {
            @Override
            public void run(){
                try{
                    log.consumir();
                }catch(InterruptedException ex){
                    ex.printStackTrace();
                }catch(IOException ex){
                    ex.printStackTrace();
                }
            }
        });

        //produzir.start();
        consumir.start();
        // produzir.join();
        // consumir.join();

        while (true) {
            Socket socket = serverSocket.accept();
            ProcessaRequisicao pro = new ProcessaRequisicao(socket, lugarSolicitacao, log);
            new Thread(pro).start();
        }
    }
}