import java.util.*;
import java.io.*;

//Sort nodes by newScore
class pageCompare implements Comparator<nodes>
{
    public int compare(nodes first, nodes second)
    {
    	return Double.compare(first.newScore, second.newScore);
    }
}

class nodes
{
	//Node Objects
	String nodeID;
	Vector<nodes> outGoing = new Vector<nodes>();
	Vector<nodes> inComing = new Vector<nodes>();
	double previousScore = 0;
	double newScore = 0;
	int pageRank = 0;
	
	nodes(String id)
	{
		nodeID = id;
	}
}

public class PageRank {

	public static double totalNodes;
	public static Vector<nodes> nodeStorage = new Vector<nodes>();
	public static HashMap<String, nodes> mapper = new HashMap<String, nodes>();
	
	//Compute matrix iteration
	public static void compute(int iterations, double beta)
	{
		double startingValue = (1/totalNodes);
		int it = 0;
		while(it < iterations)
		{
			//Initialising first iteration for each node
			if(it == 0)
			{
				for(Object objname:mapper.keySet()) 
				{
					mapper.get(objname).newScore = startingValue;
				}
			}
			
			//Calculating approximations through iterations
			else
			{
				for(Object objname:mapper.keySet()) 
				{
					// (1/n) * 1-beta
					mapper.get(objname).newScore = (startingValue*(1-beta));
					double totalSize = 0.0;
					double BMV = 0.0;
					for(int x = 0; x < mapper.get(objname).inComing.size(); x++)
					{
						totalSize = mapper.get(objname).inComing.elementAt(x).outGoing.size();
						BMV = BMV + ((mapper.get(objname).inComing.elementAt(x).previousScore)*(1/totalSize));
					}
					// (1/n) * beta * M
					mapper.get(objname).newScore = mapper.get(objname).newScore  + (BMV *beta);
					if(it == iterations-1)
					{
						nodeStorage.add(mapper.get(objname));
					}
				}
			}
			
			//Update score
			for(Object objname:mapper.keySet()) 
			{
				mapper.get(objname).previousScore = mapper.get(objname).newScore;
			}
			it++;
		}
	}
	
	public static void main(String[] args) throws IOException 
	{
		//Take record of runtime
		long startTime = System.nanoTime();
		String line;
		Vector<String> from = new Vector<String>();
		Vector<String> to = new Vector<String>();
		
		//Tokenising the .txt input
		BufferedReader in = new BufferedReader(new FileReader("src/web-Google.txt"));
		while((line = in.readLine()) != null)
		{
			if (!line.startsWith("#"))
			{
				 StringTokenizer st = new StringTokenizer(line);
				 from.add(st.nextToken());
				 to.add(st.nextToken());
			}
		}
		in.close();
		
		//Initialising nodes with its directional edges
		for(int i = 0; i < from.size(); i++)
		{
			nodes nodeFrom = new nodes(from.elementAt(i));
			nodes nodeTo = new nodes(to.elementAt(i));
			
			if(!mapper.containsKey(from.elementAt(i)))
			{
				mapper.put(from.elementAt(i), nodeFrom);
			}
			else
			{
				nodeFrom = mapper.get(from.elementAt(i));
			}
			
			if(!mapper.containsKey(to.elementAt(i)))
			{
				mapper.put(to.elementAt(i), nodeTo);
			}
			else
			{
				nodeTo = mapper.get(to.elementAt(i));
			}
			
			nodeFrom.outGoing.add(nodeTo);
			nodeTo.inComing.add(nodeFrom);
		}
		
		totalNodes = mapper.size();
		double beta = 0.8;
		int iteration = 75;
		compute(iteration,beta);
		
		//Sort the results by score and set pageRank
		Comparator<nodes> comparator = new pageCompare();
		Collections.sort(nodeStorage,comparator);
		for(int i = 0; i < nodeStorage.size(); i ++)
		{
			mapper.get(nodeStorage.elementAt(i).nodeID).pageRank = i+1;	
		}
		
		int nodes = 0;
		int edges = 0;
		
		for(Object objname:mapper.keySet()) 
		{
			nodes++;
			edges = edges + mapper.get(objname).inComing.size();
		}
		
		long endTime   = System.nanoTime();
		long totalTime = (endTime - startTime)/1000000;
		
		//Output for full list
		PrintStream out = new PrintStream(new FileOutputStream("FullList.txt"));
		out.println("RUNTIME: " + totalTime + " ms");
		out.println("NUMBER OF NODES: " + nodes);
		out.println("NUMBER OF EDGES: " + edges);
		out.println("NUMBER OF ITERATIONS: " + iteration);
		out.println("BETA VALUE: " + beta);
		out.println();
		for(Object objname:mapper.keySet()) 
		{
			out.println("ID: " + mapper.get(objname).nodeID + "  " + "pageScore: " + mapper.get(objname).newScore + "  " + "pageRank: " + mapper.get(objname).pageRank);
		}
		System.setOut(out);
		
		//Output for 10 Highest pageRank
		PrintStream out2 = new PrintStream(new FileOutputStream("Large10.txt"));
		out2.println("RUNTIME: " + totalTime + " ms");
		out2.println("NUMBER OF NODES: " + nodes);
		out2.println("NUMBER OF EDGES: " + edges);
		out2.println("NUMBER OF ITERATIONS: " + iteration);
		out2.println("BETA VALUE: " + beta);
		out2.println();
		if(nodeStorage.size() > 10)
		{
			for(int c = nodeStorage.size()-1; nodeStorage.size()-11 < c; c--)
			{
				out2.println("ID: " + mapper.get(nodeStorage.elementAt(c).nodeID).nodeID + "  " + "pageScore: " + mapper.get(nodeStorage.elementAt(c).nodeID).newScore + "  " + "pageRank: " + mapper.get(nodeStorage.elementAt(c).nodeID).pageRank);
			}
		}
		else
		{
			for(int c = 0; c < nodeStorage.size(); c++)
			{
				out2.println("ID: " + mapper.get(nodeStorage.elementAt(c).nodeID).nodeID + "  " + "pageScore: " + mapper.get(nodeStorage.elementAt(c).nodeID).newScore + "  " + "pageRank: " + mapper.get(nodeStorage.elementAt(c).nodeID).pageRank);
			}
		}
		System.setOut(out2);
		
	}

	
}