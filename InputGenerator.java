
import java.io.*;
import java.util.Random;


public class InputGenerator {


	public static File generateInput(int row , int col) {
		File file=new File("input.txt");
		Random rand = new Random();
		int rows = rand.nextInt(10); 
		try{
			Writer writer = new BufferedWriter(new FileWriter(file));

			int sum=0;
			for(int i=0;i<rows;i++)
			{
				writer.write("R00");
				writer.write(i + "");
				writer.write(" ");

				int val = rand.nextInt(col+10)+1; 
				sum += val;
				writer.write(val + "");
				
				writer.write("\n");

			}

			


			writer.close();


		}catch(Exception e){

		}
		return file;
	}
}
