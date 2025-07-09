import java.io.*;
import java.util.*;

public class ProjetoLP {

    public static String clientes = "Clientes.txt";
    public static void main(String[] args) {

            //Opcões disponíveis no sistema
            Scanner ler = new Scanner(System.in);
            System.out.println("--- BEM-VINDO(A) A OPERADORA DE COMUNICACAO DE CABO VERDE - OCV! ---");

            while (true) {
                System.out.println("\n--- MENU ---");
                System.out.println("1. Inserir registo");
                System.out.println("2. Consultar cliente");
                System.out.println("3. Gerar fatura");
                System.out.println("4. Eliminar registos");
                System.out.println("5. Sair");
                System.out.print("Escolha uma opcao: ");
                String opcao = ler.nextLine();

            switch (opcao) {
                //Inserir Registo
                case "1":
                //inserir informações do cliente
                System.out.print("Numero Cliente: ");
                String cliente = ler.nextLine();
                System.out.print("Numero Destino: ");
                String destino = ler.nextLine();
                System.out.print("Tempo (s): ");
                String tempo = ler.nextLine();
                
                //validação dos campos de informações
                if (cliente == ""  || destino == ""  || tempo == "" ) {
                    System.out.println("Todos os campos sao obrigatórios.");
                }else{
                    System.out.println("Registo inserido com sucesso.");
                }
                ////verificação de erro no ficheiro
                try (PrintWriter out = new PrintWriter(new FileWriter(clientes, true))) {
                    out.println(cliente + "," + destino + "," + tempo);
                }
                catch (IOException e) {
                    System.out.println("Erro ao escrever no ficheiro: " + e.getMessage());
        }break;
    
                //Consultar cliente
                case "2":
                System.out.print("Numero do cliente a consultar: ");
                String numero = ler.nextLine();

                try (BufferedReader br = new BufferedReader(new FileReader(clientes))){
                String linha;
                boolean encontrou = false;
                while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(",");
                    if (partes.length >= 3 && partes[0].equalsIgnoreCase(numero)) {
                        String regiao = detectarRegiao(partes[1]);
                        String valor = calcularValorTotal(partes[1], partes[2]);
                        System.out.printf("Cliente: "+partes[0]+" | Destino: "+partes[1]+ " | Tempo: "+partes[2]+ " | Regiao: "+regiao+ "| Valor: "+valor);
                            encontrou = true;
                }
            }
                    if (!encontrou) {
                        System.out.println("Nenhum registo encontrado.");
                }
                    } catch (IOException e) {
                        System.out.println("Erro ao ler o ficheiro: " + e.getMessage());
                    }break;

                //Gerar Fatura
                case "3":
                    System.out.println("Numero do cliente: ");
                    String numeroFatura = ler.nextLine();
                    File fatura = new File("Fatura_" + numeroFatura + ".txt");
                    try (BufferedReader br = new BufferedReader(new FileReader(clientes));
                            PrintWriter pw = new PrintWriter(new FileWriter(fatura))) {
                            pw.println("---------------------FATURA-----------------------------\n");
                            pw.println("EMPRESA: OPERADORA DE COMUNICACAO DE CABO VERDE - OCV\n");
                            pw.println("CLIENTE: " + numeroFatura + "\n");
                            pw.printf("%-20s %-10s %-15s %-10s\n", "DESTINO", "TEMPO", "REGIAO", "VALOR");


                            double total = 0;
                            String linha;
                            while ((linha = br.readLine()) != null) {
                            String[] partes = linha.split(",");
                            if (partes.length >= 3 && partes[0].equalsIgnoreCase(numeroFatura)) {
                                String regiao = detectarRegiao(partes[1]);
                                String valorStr = calcularValorTotal(partes[1], partes[2]);
                                double valor = Double.parseDouble(valorStr.replace("CVE", "").trim().replace(",", "."));
                                pw.printf("%-20s %-10s %-15s %-10.2f%n", partes[1], partes[2], regiao, valor);
                                total += valor;
                }
            }
                                pw.printf("\nTOTAL PAGO: %.2f ECV\n", total);
                                System.out.println("Fatura gerada: " + fatura.getAbsolutePath());

                            } catch (IOException e) {
                                System.out.println("Erro: " + e.getMessage());
                            }break;

                //Eliminar Registos
                case "4":
                    System.out.print("Numero do cliente: ");
                    String gerarFaturaCliente = ler.nextLine();
                    System.out.print("Eliminar todos os registos do cliente? (s/n): ");
                    String todos = ler.nextLine().toLowerCase();

                    File inputFile = new File(clientes);
                    File tempFile = new File("temp.txt");

                    boolean modificou = false;

                    // Etapa 1: copiar para o ficheiro temporário
                    try (
                        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                        PrintWriter writer = new PrintWriter(new FileWriter(tempFile))
                    ) {
                        String linha;
                        while ((linha = reader.readLine()) != null) {
                            String[] partes = linha.split(",");
                            if (partes.length >= 3) {
                                if (todos.equals("s")) {
                                    if (!partes[0].equalsIgnoreCase(gerarFaturaCliente)) {
                                        writer.println(linha);
                                    } else {
                                        modificou = true;
                                    }
                                } else {
                                    System.out.printf("Eliminar este registo? %s (s/n): ", linha);
                                    String resposta = ler.nextLine().trim().toLowerCase();
                                    if (!resposta.equals("s")) {
                                        writer.println(linha);
                                    } else {
                                        modificou = true;
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Erro ao processar ficheiros: " + e.getMessage());
                        break;
                    }

                    // Etapa 2: eliminar e renomear
                    if (modificou) {
                        if (inputFile.delete()) {
                            if (tempFile.renameTo(inputFile)) {
                                System.out.println("Registos eliminados com sucesso.");
                            } else {
                                System.out.println("Erro: não foi possível renomear o ficheiro temporário.");
                            }
                        } else {
                            System.out.println("Erro: não foi possível eliminar o ficheiro original.");
                        }
                    } else {
                        // Nada foi alterado, então elimina o ficheiro temporário
                        tempFile.delete();
                        System.out.println("Nenhum registo foi eliminado.");
                    }
                    break;

                //Encerrar
                case "5":
                    System.exit(0);
                break;

                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }

            public static String detectarRegiao(String destino) {
                if (destino == "+" || destino == "00"){
                    return "Internacional";
                }
                if (destino == "2" || destino == "3" || destino == "5" || destino == "9"){
                    return "Nacional";
                }
                return "Desconhecida";
    }

            public static String calcularValorTotal(String destino, String tempoStr) {
                try {
                    double tempo = Double.parseDouble(tempoStr);
                    double precoPorSegundo;

                if (destino == "+" || destino == "00") {
                precoPorSegundo = 1.05;
                } else if (destino == "2" || destino == "3") {
                precoPorSegundo = 0.24;
                } else if (destino == "8" || destino == "9") {
                precoPorSegundo = 0.43;
                } else {
                return destino;
            }
                double total = tempo * precoPorSegundo;
                return (total+" CVE");
                
                }catch (NumberFormatException e) {
                return "Erro";
        }
    }
}