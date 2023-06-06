package org.example.threads;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.example.model.Lugar;
import org.example.model.LugarSolicitacao;
import org.example.model.Solicitacao;
import org.example.model.Lugar.Situacao;
import org.example.utils.DateFormatter;

public class ProcessaRequisicao implements Runnable {
    private static final Integer TAMANHO_BYTES = 5120;
    private Socket requisicao;
    private LugarSolicitacao lugarSolicitacao;
    private ProdutorConsumidor log;

    public ProcessaRequisicao(Socket requisicao, LugarSolicitacao lugarSolicitacao, ProdutorConsumidor log){
        this.requisicao = requisicao;
        this.lugarSolicitacao = lugarSolicitacao;
        this.log = log;
    }

    /**
     * recebe conexão, decodifica os bytes pra buscar os dados da requisição
     * depois verifica se estão tentando agendar um lugar, se sim verifica se o lugar é valido
     * depois processa o arquivo css ou html do recurso
     * se for uma requisição pra agendamento, retorna no html a mensagem se deu certo ou não 
     */
    @Override
    public void run() {
        try{
            System.out.println(
                "\n----------------------------------------------\n\nconexao recebida "
                +DateFormatter.dateTimeFormatter(LocalDateTime.now())
            );

            //busca dados requisicao
            InputStream in = requisicao.getInputStream();
            OutputStream out = requisicao.getOutputStream();

            byte[] buffer = new byte[TAMANHO_BYTES];
            Integer numBytes = in.read(buffer);//tamanho da request

            String str = new String(buffer, 0, numBytes);

            /*String[] linhas = str.split("\n");
            int i = 1;
            for (String linha : linhas) {
                System.out.println("[LINHA " + i + "] " + linha);
                i++;
            }*/

            String[] primeiraLinha = str.split("\n")[0].split(" ");
            String recurso = primeiraLinha[1];

            String header = "HTTP/1.1 200 OK\nContent-Type: "+getContentType(recurso)+" charset=utf-8\n\n";

            Boolean success = null;
            String mensagem = null;

            System.out.println("recurso: "+recurso+"\n");

            if (recurso.equals("/") || recurso.equals("/index.html")) {
                recurso = "/index.html";
            }
            else if(recurso.contains("/reservar-lugar")){

                String params = URLDecoder.decode(recurso.split("\\?")[1], StandardCharsets.UTF_8);
                String numLugar = params.split("&")[0];
                String nome = params.split("&")[1];

                /*
                 * procura lugar no teatro pelo numero passado na requisição
                 * se o lugar existir faz o produtor produzir passando os dados da requisição
                */
                Optional<Lugar> lugar = this.lugarSolicitacao.getLugares().stream().filter(l -> l.getNumero() == Integer.parseInt(numLugar.split("=")[1])).findFirst();

                if(lugar.isPresent()){
                    recurso = "/index.html";

                    Map<String, String> dadosRequisicao = new HashMap<>();
                    dadosRequisicao.put("nome", nome.split("=")[1]);
                    dadosRequisicao.put("ip", requisicao.getLocalAddress().toString());

                    Solicitacao solicitacao = new Solicitacao(dadosRequisicao, lugar.get());
                    System.out.println("\nchamando produtor\n");
                    success = this.log.produzir(solicitacao);

                    if(success == Boolean.TRUE){
                        mensagem = "<h3>Assento "+solicitacao.getLugar().getNumero()+" reservado com sucesso.</h3>"
                                +"<p>Nome: "+solicitacao.getNomeSolicitante()+"</p>"
                                +"<p>Data: "+DateFormatter.dateTimeFormatter(solicitacao.getData())+"</p>"
                                +"<p>ip: "+solicitacao.getIp()+"</p>";
                    }
                    else{
                        mensagem = "<h3>Assento "+solicitacao.getLugar().getNumero()+" já reservado.</h3>";
                    }
                }
                else{
                    success = Boolean.FALSE;
                    mensagem = "<h3>Assento inválido.</h3>";
                }
            }
            else if(!recurso.endsWith(".css") && !recurso.endsWith(".ico")){
                recurso = "/error.html";
            }

            recurso = recurso.replace('/', File.separatorChar);
            File f = new File("arquivos" + recurso);
            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            if (!f.exists()) {
                bout.write("404 NOT FOUND\n\n".getBytes(StandardCharsets.UTF_8));
            }
            else{
                InputStream fileIn = new FileInputStream(f);
                bout.write(header.getBytes(StandardCharsets.UTF_8));
                numBytes = fileIn.read(buffer);
                do {
                    if (numBytes > 0) {
                        bout.write(buffer, 0, numBytes);
                        numBytes = fileIn.read(buffer);
                    }
                } while (numBytes == TAMANHO_BYTES);
                fileIn.close();
            }

            //InputStream fileIn = new FileInputStream("arquivos" + recurso);
            //out.write(header.getBytes(StandardCharsets.UTF_8));
//System.out.println(recurso);
            //escreve arquivo
            
            String saida = geraConteudoDinamico(bout,success,mensagem,this.lugarSolicitacao.getLugares());
            out.write(saida.getBytes(StandardCharsets.UTF_8));
            out.flush();
            out.close();
            requisicao.close();
        
        }catch(IOException ex){
            ex.printStackTrace();
        }catch(InterruptedException ex){
            ex.printStackTrace();
        }catch(Exception ex){
            ex.printStackTrace();
            System.out.println("Erro interno no servidor: "+ex.getMessage()+", tipo exceção: "+ex.getClass().getSimpleName());
        }
    }

    private static String geraConteudoDinamico(ByteArrayOutputStream b, Boolean success, String mensagem, List<Lugar> lugares){
        String str = new String(b.toByteArray());
        StringBuilder conteudo = new StringBuilder();

        if(mensagem != null){
            conteudo.append("<div class='message");
            conteudo.append((success == true) ? " success'>" : " error'>");
            conteudo.append(mensagem);      
            conteudo.append("</div>");
        }

        conteudo.append("<div class='box-container'>");
        lugares.stream().forEach(r -> {
            conteudo.append("<div class='box ");
            conteudo.append(r.getSituacao().equals(Situacao.LIVRE) ? "disponivel'>" : "ocupado'>");
            conteudo.append(r.getNumero());
            conteudo.append("</div>");
        });
        conteudo.append("</div>");

        str = str.replace("${conteudo}", conteudo.toString());
        return str;
    }

    private static String getContentType(String recurso){
        if(recurso.toLowerCase().endsWith(".js"))
            return "text/javascript;";
        else if(recurso.toLowerCase().endsWith(".css"))
            return "text/css;";
        else if(recurso.toLowerCase().endsWith(".ico"))
            return "image/x-icon;";
        else
            return "text/html;";
    }
    
}
