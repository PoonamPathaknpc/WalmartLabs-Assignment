import java.io.BufferedReader;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;


public class MovieTheatre {

	public static LinkedHashMap<Integer, Map<String,Integer>> reserveSeats = new  LinkedHashMap<Integer, Map<String,Integer>>();
	public static LinkedHashMap<String,Integer> reservationMap = new LinkedHashMap<String,Integer>();
	public static List<String> CKeys = new ArrayList<String>();
	static int row;
	static int col;

	public static void main(String[] args) {


		try {
			/* File is read and stored in a hash map called reservationMap
			 * 
			 * bookings are parsed and taken in first come first service 
			 * but seats are not assigned based on that
			 * Assumption:
			 * -Last rows are considered the best for customer experience
			 * -There is no explicit/implicit preference taken from customer whether they want to sit together or split them up.
			 * -Since as per the homework all the booking are given collectively in a file. The algorithm is not designed to run everytime a new booking occurs. 
			 *   It is assumed to run after all the bookings are collected as input file.
			 * Rules:
			 * - First priority is always to making sure that seats assigned together to each booking group;
			 * - Exception : if the group has booking count more that column then split the booking with first taking up the whole row.
			 * - Second priority is given to the Theatre maximum utilization by determining best subset of bookings for each row minimizing the scatter
			 * - Last priority is set for assigning the rows on first come first basis. 
			 * - Although it is not necessary that 
			 * 
			 */

			row = Integer.parseInt(args[0]); 
			col = Integer.parseInt(args[1]);

			File inputFile = InputGenerator.generateInput(row, col);

			BufferedReader reader = new BufferedReader(new FileReader(inputFile));

			String line = "";
			int totalBookings = 0;
			while ((line = reader.readLine()) != null) {
				String[] w = line.split(" ");
				if(Integer.parseInt(w[1])>col)
				{
					/*
					 * This is to check whether a booking exceeds the number of seats in a row.
					 * if so split them and store as two separate keys in the hashmap with first annotated with 'C'
					 * The key C annotation will signify this key to be ignored while considering optimization as it would anyways take up the whole row 
					 */
					reservationMap.put(w[0]+"C", col);
					CKeys.add(w[0]+"C");
					reservationMap.put(w[0], (Integer.parseInt(w[1])-col));
				}
				else
				{
					reservationMap.put(w[0], Integer.parseInt(w[1]));
				}

				totalBookings += Integer.parseInt(w[1]);
			}

			seatAssignment(totalBookings,  reservationMap);
			String input = inputFile.getAbsolutePath().split("/")[inputFile.getAbsolutePath().split("/").length-1];
			String output = inputFile.getAbsolutePath().replace(input , "") + "output.txt";
			writeOutPut(output);
			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		} 

	}

	public static void seatAssignment( int totalBookings, Map<String,Integer> reservationMap)
	{
		int bl = totalBookings; // keep assigning the seats
		int index=0;

		if(totalBookings<col)
		{
			/*
			 * If the total number of booking are coming in one row then 
			 * assign as per First come first basis
			 */
			LinkedHashMap<String,Integer> rowListMap = new LinkedHashMap<String,Integer>();
			Iterator<Entry<String,Integer >> it = reservationMap.entrySet().iterator();
			while(it.hasNext())
			{
				Entry<String, Integer> obj = it.next();
				int bookingCount = obj.getValue();
				rowListMap.put(obj.getKey(), bookingCount);
			}
			reserveSeats.put(index, rowListMap);
			return;

		}

		LinkedHashMap<String,Integer> tempSeat = new LinkedHashMap<String,Integer>();
		Iterator<Entry<String,Integer >> it = reservationMap.entrySet().iterator();


		while(bl!=0)
		{

			int totalSum = 0;	
			boolean flag = false;
			String cKey = "";
			tempSeat.clear();
			tempSeat.putAll(reservationMap);
			// to make sure all 'C' annotated seats are not included in optimation
			Iterator<String> k  =  CKeys.iterator();
			while(k.hasNext())
				tempSeat.remove(k.next());

			List<String> reservs = new ArrayList<String>(reservationMap.size());
			int maxSum =0;

			it = reservationMap.entrySet().iterator();
			List<String> reservst = new ArrayList<String>(reservationMap.size());
			Iterator<Entry<String,Integer >> ittemp = tempSeat.entrySet().iterator();
			while(it.hasNext())
			{
				Entry<String, Integer> obj = it.next();
				if(obj.getKey().endsWith("C")) 
				{
					// set the flag true 
					flag = true;
					cKey = obj.getKey();
					if(it.hasNext())
						obj = it.next();


				}

				// add first seat booking in the assignment of the iteration	
				reservst.add(obj.getKey()); 
				tempSeat.remove(obj.getKey());
				ittemp = tempSeat.entrySet().iterator();
				totalSum += obj.getValue(); 

				SeatAssign SA = new SeatAssign(totalSum,reservst,ittemp ,tempSeat); 
				// Call this function to do depth first search given a starting node
				bookTicket(SA);
				/*
				 * Check if the optimized path and maxsum is better if not replace with the current results
				 */
				if(maxSum < SA.maxSum)
				{
					maxSum = SA.maxSum;
					reservs.clear();
					reservs.addAll(SA.optSubA); // to be changed the logic
				}

				if(reservs.size() == 0) // no path found for the only element
					reservs.add(obj.getKey());

				// clear and reset all the temporary objects everything 
				tempSeat.clear();
				tempSeat.putAll(reservationMap);
				tempSeat.remove(obj.getKey());
				k  =  CKeys.iterator();
				while(k.hasNext())
					tempSeat.remove(k.next());
				reservst.clear();
				totalSum = 0;

			}

			bl = bl - maxSum; // keep updating the bookings left to assign

			LinkedHashMap<String,Integer> rowListMap;
			if(flag==true && reservs.contains(cKey.replace("C", "")))
			{
				/*
				 * If the flag is true Assign this booking the whole row.
				 * In this case we will assign the current row and split the rest 
				 */
				rowListMap = new LinkedHashMap<String,Integer>();
				rowListMap.put(cKey.replace("C", ""),col);
				reserveSeats.put(index, rowListMap);
				bl = bl - col;
				reservationMap.remove(cKey);
				index++;
				flag = false;
			}

			// add seat allocation to output map
			rowListMap = new LinkedHashMap<String,Integer>();
			for(int i=0;i<reservs.size();i++)
			{
				int bookingCount = reservationMap.get(reservs.get(i));
				rowListMap.put(reservs.get(i), bookingCount);
			}

			reserveSeats.put(index, rowListMap);

			// removing reservations which are now assigned in existing row
			for(int i=0;i<reservs.size();i++)
			{
				reservationMap.remove(reservs.get(i));
			}
			index++;
		}

	}


	public static void  bookTicket(SeatAssign SA)
	{
		if(SA.tempSeats.size() != 0)
		{
			if(SA.seatIterator.hasNext())  
			{
				Entry<String,Integer> nextitem = SA.seatIterator.next();
				String bookingNum  = nextitem.getKey();
				int bookingCount = nextitem.getValue();
				int temp = SA.tSum + bookingCount;
				if(temp<=col) // to check if the addition has not exceeded the row limit
				{ 
					SA.subA.add(bookingNum);  // add the corresponding booking reference
					SA.tSum += bookingCount; // add the e booking count
					SA.seatIterator.remove(); // remove the element
				}
			}
			else // done with one path in dfs search
			{
				//assign the path to most optimum is the max sum is greater 
				if(SA.maxSum < SA.tSum) {
					SA.maxSum = SA.tSum;
					SA.optSubA.clear();
					SA.optSubA.addAll( SA.subA);

				}

				// reset the iterator again and call recursion on new iterator
				SA.seatIterator = SA.tempSeats.entrySet().iterator();
				SA.tSum = 0;
				SA.subA.clear();

			}

			bookTicket(SA);
		}
		else // all the nodes are used so return with max value and list 
		{
			//assign the path to most optimum is the max sum is greater 
			if(SA.maxSum < SA.tSum) {
				SA.maxSum = SA.tSum;
				SA.optSubA.clear();
				SA.optSubA.addAll( SA.subA);
			}
			return;
		}

	}

	public static void writeOutPut(String outputFile)
	{

		try{
			File file=new File(outputFile);
			Writer writer = new BufferedWriter(new FileWriter(file));

			Map<String, String> rSeats = new TreeMap<String, String>();

			for(Entry<Integer, Map<String,Integer>> obj :reserveSeats.entrySet())
			{
				char SeatA = (char)(col-(obj.getKey()+1) + 65);
				int totalColSeats = 1;
				for(Entry<String,Integer> seats :obj.getValue().entrySet())
				{
					String str = "";
					int i = 1;
					while(i<=seats.getValue())
					{
						str += SeatA ;
						str+=totalColSeats; 

						if(i!=seats.getValue())
							str += ",";
						i++;
						totalColSeats++;
					}
					
					
					if(seats.getKey().contains("C"))
					{
						String temp = rSeats.get(seats.getKey().replace("C",""));
						rSeats.put(seats.getKey().replace("C",""), temp + "," + str);
					}
					else
						rSeats.put(seats.getKey(), str);
				}


			}

			for(Entry<String,String> obj :rSeats.entrySet())
			{
				writer.write(obj.getKey());
				writer.write(" ");
				writer.write(obj.getValue());
				writer.write("\n");
			}


			writer.close();

		}catch(Exception e){

		}


	}



}


