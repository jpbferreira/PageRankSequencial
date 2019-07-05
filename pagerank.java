/**
* @author João Pedro Batista Ferreira e Marco Aurélio Ferreira de Sousa
*/
 
 
import java.util.*;
import java.io.*;


public class pagerank {

	/*'tabela_valores_rank' é o hashmap que armazena a página da Web (Numero de sequencia) como chave e seu valor de PageRank como valor. 
	* Na função seguinte, inicializamos esta tabela. A seguinte função define o valor inicial da classificação de página para todas 
	* as páginas da Web como 1 / N, em que N é o número total de páginas da Web de entrada no espaço fornecido */

	public static void init_rank_value_table(HashMap<Integer,Double> rank_values_table,int num_urls) {	
		
		double initpagerankvalue=1.0/(double)num_urls;
		for(int i=0;i<num_urls;i++) {
			rank_values_table.put(i,initpagerankvalue);
		}
	}


	/* Função para calcular o valor da classificação da página para a lista de todos os URLs de entrada
  	* Este é um processo iterativo até a convergência.
  	* Uma vez que a convergência ocorre, isto é, não há diferença entre a corrente
  	* e valores anteriores de page rank, este método não será mais chamado
  	*/
 
	public static void join_rvt_am(LinkedHashMap<Integer,Double> rank_values_table,LinkedHashMap<Integer,ArrayList<Integer>> adjacency_matrix,int num_urls,double damping_factor) {
		Iterator<Integer> ite=adjacency_matrix.keySet().iterator();
		ArrayList<Integer> target_urls_list=new ArrayList<Integer>();
		LinkedHashMap<Integer,Double> intermediate_rvt = new LinkedHashMap<Integer,Double>();
		Iterator<Integer> intermediate_rvt_iter=adjacency_matrix.keySet().iterator();
		
			/*intermediate_rvt_iter armazena o hashmap com os valores de pagerank para os nós dangling junto com
		 	*valores de classificação de página de seus nós sucessores ponderados por seus links externos
		 	*/
		 
		for(int i=0;i<num_urls;i++) {
			intermediate_rvt.put(i,0.0);	
		}
		
		int source_url=0;
		int outdegree_of_source_url=0;
		int target_url=0;
		double intermediate_rank_value=0.0;
		double dangling_value=0;
		double dangling_value_per_page=0.0;
		
			/* Itera todas as URLs no arquivo de entrada
		 	*/
		 
		while(ite.hasNext()) {
			source_url=((Integer)ite.next()).intValue();
			target_urls_list=adjacency_matrix.get(source_url);
			outdegree_of_source_url=target_urls_list.size();
			
			/* Atribuir valor de PageRank atualizado a todos os sucessores do nó atual
			 *O valor de PageRank é igual ao valor de classificação de página do nó predecessor ponderado pelo número de seus links de saída
			 */
			 
			for(int i=0;i<outdegree_of_source_url;i++) {
				target_url=target_urls_list.get(i).intValue();
				intermediate_rank_value=intermediate_rvt.get(target_url)+((rank_values_table.get(source_url))/(outdegree_of_source_url));
				intermediate_rvt.put(target_url,intermediate_rank_value);
			}
			
			/* Caso especial para o nó dangling sem link de saída
			 */
			 
			if(outdegree_of_source_url==0) {
				dangling_value+=rank_values_table.get(source_url);
			}
		}
	
		dangling_value_per_page=dangling_value/(double)num_urls;
		
		/*O PageRank de todos os links pendentes é calculado e é distribuído entre todas as páginas da Web para minimizar o efeito de nós pendentes.
		 *Sem esse recurso, o page rank médio de determinado gráfico será menor que 1 com má utilização
		 */
	 
		for(int i=0;i<num_urls;i++) {
			rank_values_table.put(i,intermediate_rvt.get(i)+dangling_value_per_page);
		}
	
		/*Valor final de PageRank para determinada página para um determinado número de iterações.
		 *O PageRank é calculado considerando-se dois cenários, primeiro é o modelo de surfista aleatório e o segundo é a possibilidade 
		 *de o surfista alcançar a página particular após clicar em um número específico de URLs no determinado pool
	 	*/
	 
		for(int i=0;i<num_urls;i++) {
			rank_values_table.put(i,damping_factor*rank_values_table.get(i)+((1-damping_factor)*((1.0)/(double)num_urls)));
		}
	}


		/*A função seguinte retorna o número de URLs de entrada. Ele pega a entrada de populated_url_list que é LinkedHashMap e conta o número de entradas nele. 
		*Como preenchemos todos os URLs nesse hashmap, o número total de URLs é o tamanho de LinkedHashMap chamado populated_url_list
		*/
 
	public static int count_url_list_size(LinkedHashMap<Integer,ArrayList<Integer>> populated_url_list) { 
		return populated_url_list.size();
	}

		//contrutor para a classe pagerank

    public pagerank() {
    	
    }
    
    public static void main(String args[]) {
    	try {
     		
    		String inputfilename="pagerank500k.txt";
    		String outputfilename="pagerank_output.txt";
    		int iteration=10;
    		double damping_factor=0.85;
    		
    
    		
	    	LinkedHashMap<Integer,Double> rank_values_table =new LinkedHashMap<Integer,Double>();
	    	LinkedHashMap<Integer,ArrayList<Integer>> adjacency_matrix_list=new LinkedHashMap<Integer,ArrayList<Integer>>();
	    	FileInputStream file=new FileInputStream(inputfilename);
	    	DataInputStream datastr = new DataInputStream(file);
	    	BufferedReader urlreader=new BufferedReader(new InputStreamReader(datastr));
	    	String adjmatrix;
	    	int url_size=0;
	    	
	    
			/*Após o while lemos o arquivo de entrada que contém o link de URLs, juntamente com os URLs para os quais ele tem link de saída. 
			*Nós lemos o arquivo linha por linha armazenando o nó fornecido no LinkedHashMap como chave e todos os nós aos quais ele está conectado em uma arraysist.
			*Arraylist é armazenado como valor em determinado LinkedHashMap
			*/

	    	long startingTime = System.currentTimeMillis();
	    	while((adjmatrix=urlreader.readLine())!=null) {
	  			ArrayList<Integer> target_urls_list=new ArrayList<Integer>();
	    		
	    	//Separa o primeiro nó do restante dos nós
	    		String[] nodelist = adjmatrix.split(" ");
	    		for(int i=1;i<nodelist.length;i++) {
	    			target_urls_list.add(Integer.valueOf(nodelist[i]));
	    		}
	    		
	    	 	adjacency_matrix_list.put(Integer.valueOf(nodelist[0]),target_urls_list);
	    	}
	    	
			/* Obtenha o número total de URLs de entrada chamando a função count_url_list_size com a 
			*entrada de LinkedHashMap que armazena todas as URLs de entrada
			*/
	    	
	    	url_size=count_url_list_size(adjacency_matrix_list);

			//Inicializa a tabela de PageRank e armazena as URLs e os pares chave-valor
	    	 
	    	init_rank_value_table(rank_values_table,url_size);
	    	
	    	
			/*Hashmap temporário para armazenar valores anteriores do par (chave, valor). 
			*Em iteração sucessiva, ele irá comparar esses valores com o valor atual da classificação da página para todas as URLs.
			*Quando essa diferença ficar abaixo do valor limite, ela sairá do while.
			*/
	    
	    	//Inicialização do hashmap temporario
	    
		    LinkedHashMap<Integer,Double> rank_values_table_temp =new LinkedHashMap<Integer,Double>();
		    
		    for(int i=0;i<url_size;i++) {
		    	rank_values_table_temp.put(i,0.0);
		    }
		    
		    int temp1=0;
		    
				/*Registra a hora em que o programa começou a executar
				*Este tempo é então comparado com o tempo de chegada
				*/
		     
		    final long startTime1 = System.nanoTime(); 
		  
		  
		  /*Nós dividimos a execução do algoritmo em duas partes
		  *A primeira parte não se importa com a contagem de iterações de entrada. 
		  *Ele interrompe a execução depois que a convergência ocorre para determinado conjunto de páginas da Web
		  *A segunda parte é bastante ineficiente. Em alguns casos, quando o valor de iterações é muito grande, 
		  *não importa quando a solução converge, ela continuará a ser executada até que o loop tenha passado sobre 
		  *todos os valores de 0 para contagem de iteração.
		  */
		   
			if(iteration<1) {
			    while(true) {
			    	temp1++;
					/*O código a seguir armazena o valor obsoleto da classificação da página em um hash rank_values_table antes de chamar 
					*a função join_rvt_am. Esses valores são então comparados com os novos valores de classificação de página em rank_values_table 
					*após essa função ter concluído a execução
					*/
			    	 
			    	Iterator rank_values_table_tempi=rank_values_table.entrySet().iterator();
			    	while(rank_values_table_tempi.hasNext()) {
			    		Map.Entry keyvaluepair=(Map.Entry)rank_values_table_tempi.next();
			    	    rank_values_table_temp.put(Integer.valueOf(keyvaluepair.getKey().toString()),Double.valueOf(keyvaluepair.getValue().toString()));	
			    	}
				
					/*Chama a join_rvt_am para calcular os valores de classificação de página para URLs de entrada com base no valor 
					*de classificação de página da URL anterior, essa função será chamada até a convergência
					*/
			    
					/*Código avalia o tempo gasto em uma única chamada para funcionar para calcular o valor de classificação 
					*de página de cada URL no arquivo de entrada.
					*O valor corresponde apenas ao tempo necessário para uma única iteração.
					*Este tempo é gravado a cada vez até a convergência de todos os valores da classificação da página
			     	*/
			      
				    final long startTime = System.nanoTime(); 
				    
				    join_rvt_am(rank_values_table,adjacency_matrix_list,url_size,damping_factor);
				    
				    final long endTime = System.nanoTime();
				    
				    final long duration = endTime - startTime;
			   
					/*O Threshold determina a estimativa pela qual as classificações atuais e anteriores da página variam. 
					O valor do threshold determina a condição de parada. baixo valor de page rank aumenta o tempo de execução. Mas dá excelente resultado final.
			     	*
			     	*O alto valor de classificação de página diminui o tempo de execução, mas produz resultados ruins do cálculo do pagerank
			     	*/
				     
				    double threshold=0.001;
					double difference=0.0;
					
					Iterator current_adj_matrix=rank_values_table.entrySet().iterator();
					Iterator previous_adj_matrix=rank_values_table_temp.entrySet().iterator();
					 
					 /*O Loop calcula a diferença entre o valor atual e antigo da classificação da página para cada URL.
					 *A soma da diferença de todas as URLs é comparada com o valor do threshold predefinido. 
					 *Quando essa diferença fica abaixo do threshold, o Loop começa
					 */
				 
					while(current_adj_matrix.hasNext() && previous_adj_matrix.hasNext())
				    {
				    	Map.Entry currentkeyvaluepair=(Map.Entry)current_adj_matrix.next();
				    	Map.Entry oldkeyvaluepair=(Map.Entry)previous_adj_matrix.next();
				    	difference+=(Math.abs(Double.parseDouble(currentkeyvaluepair.getValue().toString())-Double.parseDouble(oldkeyvaluepair.getValue().toString())));	
				    }
			   
					/*A condição de parada do loop sugere convergência ocorreu para valores de 
					*classificação de página. Não são necessárias mais iterações.*/
			   
					if(difference<threshold) {
						break;
					}
			    }
			}
			else {
				for(int count_of_loop=0;count_of_loop<iteration;count_of_loop++) {
					join_rvt_am(rank_values_table,adjacency_matrix_list,url_size,damping_factor);	
				}
				temp1=iteration;
			}   
	    	/* Código Java para medir o tempo necessário para concluir a execução do programa O tempo é calculado em termos de nano-segundos
	     	*/
	     
		    final long endTime1 = System.nanoTime();
		    final long duration1 = endTime1 - startTime1;
		    
	    	/*Seleciona as 10 URLs com maior valor de PageRank*/ 
	    
		    /*A seção a seguir converte o hashmap em dois arrays
			*As chaves são armazenadas na matriz de chaves, enquanto os valores são armazenados na matriz de valores
	     	*/
	    
		    double sum_of_probabilities=0.0;
		    for(Double val:rank_values_table.values()) {
		    	sum_of_probabilities+=val;
		    }
		    
	        int[] keys=new int[rank_values_table.size()];
	    	double[] values=new double[rank_values_table.size()];
	    	int index=0;
	    	for (Map.Entry<Integer, Double> mapEntry : rank_values_table.entrySet()) {
	    		keys[index] = Integer.parseInt(mapEntry.getKey().toString());
	    		values[index] = Double.parseDouble(mapEntry.getValue().toString());
	    		index++;
			}
			
			
			// Ordena os valores de PageRank de forma crescente
		
		    List<Double> page_rank_list=new ArrayList<Double>(rank_values_table.values());
		    Collections.sort(page_rank_list);
		    ListIterator sorted_page_rank_iterator=page_rank_list.listIterator(page_rank_list.size());
		    int number_web_pages=0;
	    
			long totalTime = System.currentTimeMillis() - startingTime;
			
			System.out.println("Tempo de computação: " + totalTime);

			/*Escrita no arquivo de saida*/
	
		    Writer output = null;
		    File toptenurllist = new File(outputfilename);
		    output = new BufferedWriter(new FileWriter(toptenurllist));
		  
	    	/* Cabeçalho do arquivo de saida
	     	*/
	     
		    output.append("\nTop 10 URLs "+"\n\n"+"--------------------------------------"+"\n");
		    output.append("|\t"+"URL"+"\t\t|\t"+"Page Rank"+"\t\t\t|\n"+"--------------------------------------"+"\n");
		    while(sorted_page_rank_iterator.hasPrevious() && number_web_pages++<10) {
		    	
		    	String str =sorted_page_rank_iterator.previous().toString();
		    	double pagerankop = Double.valueOf(str).doubleValue();
		  
		    	/*Seleciona as URLs com maior valor de PageRank
		     	*e armazena no arquivo de saída
		     	*/
		     
		    	for(int i=0;i<url_size;i++) {
		    		if((values[i]==pagerankop)) {	
		    			output.write("|\t"+keys[i]+"\t\t|\t"+String.format("%2.17f",pagerankop)+"\t|\n");
		   			 	output.write("---------------------------------------"+"\n");
		   				break;
		    		}
		    	}
			}
			
		    try {
		    	if(output!=null) {	
		    		output.close();
		    	}
		    }	
		    catch(IOException er) {
		    	er.printStackTrace();
		    }
		}
    	catch(FileNotFoundException e) {
    		e.printStackTrace();
    	}
    	catch(IOException e) {
    		e.printStackTrace();
    	}
    
    }
}
