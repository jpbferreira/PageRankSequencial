/**
 * @(#)pagerank.java
 *
 *
 * @author Group 1 - Jayesh Kawli and Shubhada Karavinokappa 
 * @version 1.00 2012/9/2
 */
 
 
import java.util.*;
import java.io.*;


public class pagerank {

		/*'rank_values_table' is the hashmap which stores webpage(Sequence Number) as key and its Page rank value as value.
		In the following function, we initialize this table. Following function sets initial page rank value for all the
		Web pages as 1/N where N is the total number of input web pages in the given space */

	public static void init_rank_value_table(HashMap<Integer,Double> rank_values_table,int num_urls)
	{	
		
		double initpagerankvalue=1.0/(double)num_urls;
		for(int i=0;i<num_urls;i++)
		{
			rank_values_table.put(i,initpagerankvalue);
		}
	}


		/* Function to calculate page rank value for the list of all input URLs
 		*This is an iterative process until convergence.
 		*Once convergence occurs, that is there is no difference between current
 		*and earlier values of page rank, this method will no longer be called
 		*/
 
	public static void join_rvt_am(LinkedHashMap<Integer,Double> rank_values_table,LinkedHashMap<Integer,ArrayList<Integer>> adjacency_matrix,int num_urls,double damping_factor)
	{
		Iterator<Integer> ite=adjacency_matrix.keySet().iterator();
		ArrayList<Integer> target_urls_list=new ArrayList<Integer>();
		LinkedHashMap<Integer,Double> intermediate_rvt = new LinkedHashMap<Integer,Double>();
		Iterator<Integer> intermediate_rvt_iter=adjacency_matrix.keySet().iterator();
		
			/*intermediate_rvt_iter hashmap stores pagerank values for dangling nodes along
		 	*with page rank values of its successor nodes weighed by their outbound links
		 	*/
		 
		for(int i=0;i<num_urls;i++)
		{
			intermediate_rvt.put(i,0.0);	
		}
		
		int source_url=0;
		int outdegree_of_source_url=0;
		int target_url=0;
		double intermediate_rank_value=0.0;
		double dangling_value=0;
		double dangling_value_per_page=0.0;
		
			/* Iterate over all the URLs in given input file
		 	*/
		 
		while(ite.hasNext())
		{
			source_url=((Integer)ite.next()).intValue();
			target_urls_list=adjacency_matrix.get(source_url);
			outdegree_of_source_url=target_urls_list.size();
			
			/* Assign updated page rank value to all the successors of current node
			 *Page rank value is equal to page rank value of predecessor node
			 *weighed by the number of its outbound links
			 */
			 
			for(int i=0;i<outdegree_of_source_url;i++)
			{
				target_url=target_urls_list.get(i).intValue();
				intermediate_rank_value=intermediate_rvt.get(target_url)+((rank_values_table.get(source_url))/(outdegree_of_source_url));
				intermediate_rvt.put(target_url,intermediate_rank_value);
			}
			
			/* Special case for dangling node with no outbound link
			 */
			 
			if(outdegree_of_source_url==0)
			{
				dangling_value+=rank_values_table.get(source_url);
			}
		}
	
		dangling_value_per_page=dangling_value/(double)num_urls;
		
		/*Page rank of all the dangling links is calculated and is distributed
		 *among all the webpages to minimize effect of dangling nodes.
		 *Without this facility, average page rank of given graph will be less
		 *than 1 with poor utilization
		 */
	 
		for(int i=0;i<num_urls;i++)
		{
			rank_values_table.put(i,intermediate_rvt.get(i)+dangling_value_per_page);
		}
	
	
		/*Final page rank value for given page for given number of iteration.
	 	*Page rank is caluclated by considering two scenarios, first is random surfer
	 	*model and second is the possibility that surfer might reach particual page
	 	*after clicking specific number of URLs in the given pool
	 	*/
	 
		for(int i=0;i<num_urls;i++)
		{
			rank_values_table.put(i,damping_factor*rank_values_table.get(i)+((1-damping_factor)*((1.0)/(double)num_urls)));
		}
	}



		/*Following function returns the number of input URLs. It takes the input of populated_url_list which 
 		*is LinkedHashMap and counts the number of entries in it. Since we populated all the URLs in this
 		*hash map, total number of URLs is the size of LinkedHashMap named populated_url_list
 		*/
 
	public static int count_url_list_size(LinkedHashMap<Integer,ArrayList<Integer>> populated_url_list)
	{	
		return populated_url_list.size();
	}

		//constructor for pagerank class

    public pagerank() 
    {
    	
    }
    
    public static void main(String args[])
    {
    	try
    	{
    
     		/*
     		*Takes command line arguements as an input
     		*Command line arguements can be interpreted as follows
     		*args[0] Name of the input files which contains description of all the input URLs
     		*args[1] Name of the Output file which will store the list of top 10 URLs
     		*args[2] Total number of iterations program will do. However, it is not mandatory
     		*		 or even suggested to use those number of iterations. Solution will converge after
     		*		 fixed number of steps.
     		*		 When this input value is given as -1, progrma will execute until convergence occurs
     		*		 Latter method is more effcient in most of the cases
     		*args[3] Damping factor. Represents the probability that user will continue clicking the
     		*	     sequence of links to go from one page to another.
     		*		 Value is generally set to 0.85
     		**/
     		
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
	    	
	    	
	    	/*Follwing while loop reads input file which contains the link of URLs 
	    	 *along with the URLs to which it has outbound link. We read the file 
	    	 *line by line storing given node in LinkedHashMap as key and all the 
	    	 *nodes to which it is connected in a arraylist.
	    	 *Arraylist is stored as value in given LinkedHashMap
	    	 */
	    	 
	    	long startingTime = System.currentTimeMillis();
	    	while((adjmatrix=urlreader.readLine())!=null)
	    	{
	  			ArrayList<Integer> target_urls_list=new ArrayList<Integer>();
	    		
	    	//Separates first node from rest of the nodes
	    		String[] nodelist = adjmatrix.split(" ");
	    		for(int i=1;i<nodelist.length;i++)
	    		{
	    			target_urls_list.add(Integer.valueOf(nodelist[i]));
	    		}
	    		
	    	 	adjacency_matrix_list.put(Integer.valueOf(nodelist[0]),target_urls_list);
	    	}
	    	
	    	/*Get total number of input URLs by calling the
	    	 *function count_url_list_size with input of
	    	 *LinkedHashMap which stores all the input URLs
	    	*/
	    	
	    	url_size=count_url_list_size(adjacency_matrix_list);
	    	
	    	/*
	    	 *Initialise page rank table to store URLs and
	    	 *their page ranks as a key value pair
	    	 */
	    	 
	    	init_rank_value_table(rank_values_table,url_size);
	    	
	    	
	    	/*Temporary hash map to store previous values of (key,value) pair.
	     	*In successive iteration, it will compare those values with 
	     	*current value of page rank for all the URLs.
	     	*When this difference goes below threshold value, it will break out
	     	*of while loop.
	     	*
	     	**/
	    
	    	//Initialization of temporary hash map table
	    
		    LinkedHashMap<Integer,Double> rank_values_table_temp =new LinkedHashMap<Integer,Double>();
		    
		    for(int i=0;i<url_size;i++)
		    {
		    	rank_values_table_temp.put(i,0.0);
		    }
		    
		    int temp1=0;
		    
		    	/*Records the time at which program started executing
		     	*This time is then matched against finishing time
		     	*Difference is equal to time for which program 
		     	*remianed in while loop
		     	*/
		     
		    final long startTime1 = System.nanoTime(); 
		  
		  
		  /* We divide algorithm execution in two parts
		   *First part does not care about the input 
		   *iteration count. It stops execution
		   *once convergence has occurred for given set of 
		   *web pages
		   *
		   *Second part is rather inefficient.In some cases
		   *when value of iterations is very large where no 
		   *matter when solution converges, it will continue
		   *its to execute until loop has ran over all the
		   *values from 0 to iteration count.
		   */
		   
			if(iteration<1)
			{
			    while(true)
			    {
			    	temp1++;
			    	/*Following code store the stale value of page rank in a 
			    	 *hashmap rank_values_table before it calls the function 
			    	 *join_rvt_am. These values are then compared against 
			    	 *new page rank values in rank_values_table after this 
			    	 *function has finished execution
			    	 */
			    	 
			    	Iterator rank_values_table_tempi=rank_values_table.entrySet().iterator();
			    	while(rank_values_table_tempi.hasNext())
			    	{
			    		Map.Entry keyvaluepair=(Map.Entry)rank_values_table_tempi.next();
			    	    rank_values_table_temp.put(Integer.valueOf(keyvaluepair.getKey().toString()),Double.valueOf(keyvaluepair.getValue().toString()));	
			    	}
				
			    	/*Call to join_rvt_am to calculate page rank values for
			    	 *input URLs based upon page rank value of preceeding 
			    	 *URL this function will get called until convergence
			    	 */
			    
			    	/*Code evaluates time spent in a single call to function to calculate
			     	*page rank value of each URL in input file.
			     	*Value only corresponds to time required for single iteration.
			     	*This time gets recorded each time until convergence of all the page
			     	*rank values
			     	*/
			      
				    final long startTime = System.nanoTime(); 
				    
				    join_rvt_am(rank_values_table,adjacency_matrix_list,url_size,damping_factor);
				    
				    final long endTime = System.nanoTime();
				    
				    final long duration = endTime - startTime;
			   
			    	/*Threshold determines the estimate by which current and previous 
			     	*page ranks vary. Value of threshold determines stopping condition.
			     	*low value of page rank increases execution time. But gives excellent
			     	*final result.
			     	*
			     	*High page rank value decreases execution time, but gives poor results
			     	*of page rank calculation
			     	*/
				     
				    double threshold=0.001;
					double difference=0.0;
					
					Iterator current_adj_matrix=rank_values_table.entrySet().iterator();
					Iterator previous_adj_matrix=rank_values_table_temp.entrySet().iterator();
				
				
					/*Loop calculates difference between current and old page rank value
				 	*for each URL.
				 	*Sum of difference of all the URLs is compared against predefined
				 	*Threshold value. When this difference goes below threshold,
				 	*Loop breaks out
				 	*/
				 
					while(current_adj_matrix.hasNext() && previous_adj_matrix.hasNext())
				    {
				    	Map.Entry currentkeyvaluepair=(Map.Entry)current_adj_matrix.next();
				    	Map.Entry oldkeyvaluepair=(Map.Entry)previous_adj_matrix.next();
				    	difference+=(Math.abs(Double.parseDouble(currentkeyvaluepair.getValue().toString())-Double.parseDouble(oldkeyvaluepair.getValue().toString())));	
				    }
			   
			    	/*Terminating condition for while loop
			     	*suggests convergence has occurred
			     	*for page rank values. No more iterations
			     	*are necessary.
			     	*/
			   
					if(difference<threshold)
					{
						break;
					}
			    }
			}
			else
			{
				for(int count_of_loop=0;count_of_loop<iteration;count_of_loop++)
				{
					join_rvt_am(rank_values_table,adjacency_matrix_list,url_size,damping_factor);	
				}
				temp1=iteration;
			}   
	    	/*Java code to measure time required to complete program execution
	     	*Time is calculated in terms of nano-seconds
	     	*/
	     
		    final long endTime1 = System.nanoTime();
		    final long duration1 = endTime1 - startTime1;
		    
	    	/*Getting top 10 URLs with highest page rank value*/ 
	    
		    /*Following section converts hashmap into two arrays
	     	*Keys are stored in keys array while Values are stored 
	     	*in values array
	     	*/
	    
		    double sum_of_probabilities=0.0;
		    for(Double val:rank_values_table.values())
		    {
		    	sum_of_probabilities+=val;
		    }
		    
	        int[] keys=new int[rank_values_table.size()];
	    	double[] values=new double[rank_values_table.size()];
	    	int index=0;
	    	for (Map.Entry<Integer, Double> mapEntry : rank_values_table.entrySet()) 
	    	{
	    		keys[index] = Integer.parseInt(mapEntry.getKey().toString());
	    		values[index] = Double.parseDouble(mapEntry.getValue().toString());
	    		index++;
			}
			
			
			/* Sort the page rank values in ascending order */
		
		    List<Double> page_rank_list=new ArrayList<Double>(rank_values_table.values());
		    Collections.sort(page_rank_list);
		    ListIterator sorted_page_rank_iterator=page_rank_list.listIterator(page_rank_list.size());
		    int number_web_pages=0;
	    
			long totalTime = System.currentTimeMillis() - startingTime;
			
			System.out.println("Tempo de computação: " + totalTime);

	    	/*File operation to write result to output file */
	    
	
		    Writer output = null;
		    File toptenurllist = new File(outputfilename);
		    output = new BufferedWriter(new FileWriter(toptenurllist));
		  
	    	/* Header Line in Output File
	     	*/
	     
		    output.append("\nTop 10 URLs "+"\n\n"+"--------------------------------------"+"\n");
		    output.append("|\t"+"URL"+"\t\t|\t"+"Page Rank"+"\t\t\t|\n"+"--------------------------------------"+"\n");
		    while(sorted_page_rank_iterator.hasPrevious() && number_web_pages++<10)
		    {
		    	
		    	String str =sorted_page_rank_iterator.previous().toString();
		    	double pagerankop = Double.valueOf(str).doubleValue();
		  
		    	/*Get top 10 URLs along with their Page Rank values
		     	*and store this list into external output file
		     	*/
		     
		    	for(int i=0;i<url_size;i++)
		    	{
		    		if((values[i]==pagerankop))
		    		{	
		    			output.write("|\t"+keys[i]+"\t\t|\t"+String.format("%2.17f",pagerankop)+"\t|\n");
		   			 	output.write("---------------------------------------"+"\n");
		   				break;
		    		}
		    	}
		    }
		    output.append("\n"+"Cumulative Sum of Page Rank values\n");
		    output.append("--------------------------------------"+"\n");
		    output.write(String.format("%1.16f",sum_of_probabilities)+"\n");
		    output.append("--------------------------------------"+"\n");
		    output.append("\n"+"Number of Iterations Performed\n");
		    output.append("--------------------------------------"+"\n");
		    output.write(String.format("%d",temp1)+"\n");
		    output.append("--------------------------------------"+"\n");
		    
		    /* Additional piece of code to close the output file
	     	*once it has been written.
	     	*First check for null file pointer. If not null, then
	     	*close the file handle
	     	*/
	     
		    try
		    {
		    	if(output!=null)
		    	{	
		    		output.close();
		    	}
		    }	
		    catch(IOException er)
		    {
		    	er.printStackTrace();
		    }
		}
  
  		/* Throws FileNotFoundException if specified file is not found
   		*/
   
    	catch(FileNotFoundException e)
    	{
    		e.printStackTrace();
    	}
   
  		/* Throws IOException if there is some problem with input
	   *output functionality
   		*/ 
   	
    	catch(IOException e)
    	{
    		e.printStackTrace();
    	}
    
    }
}
