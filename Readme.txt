
Approach:

Since the homework requires balancing both customer satisfaction and maximizing theatre utilization, the problem is categorized as np-complete which can be reduced to sum-subset problem of finding subsets whose sum is equal to K.
In our situation sum-subset is related to finding most optimal subsets of bookings which can leave as minimum as possible vacant seats ( called holes) for each.

So, for each row, the algorithm is designed to navigate through all bookings with mixed BFS-DFS structure and backtrack on hard constraint; of sum of subsets not more than seats in a row.(splitting is default not allowed until count for an individual booking exceeds row itself).

Note: If the total booking count is less then the seats in one row itself, then the seating arrangement is done on Fist Come First Basis by assigning seats linearly).

Complexity:
The tree structure navigation always has worst-case scenario of 2 pow n , but since we apply backtracking each time a constraint is not met, we make the tree sparse and complexity close to n!.


Assumptions:
-Last rows are considered the best for customer experience
-There is no explicit/implicit preference taken from customer whether they want to sit together or split them up.
-Since as per the homework all the booking are given collectively in a file. The algorithm is not designed to run every time a new booking occurs. It is assumed to run after all the bookings are collected as input file.

Rules:
- First priority is always to making sure that seats assigned together to each booking group.
 (Exception : if the group has booking count more that column then split the booking with first taking up the whole row.)
- Second priority is given to the Theatre maximum utilization by determining best subset   of bookings for each row minimizing the scatter
- Last priority is set for assigning the rows on first come first basis. 

Instructions to run the program:
- Open the command prompt.
- Navigate(cd) till the directory path in which the files reside.
- Run the following commands: 
   
If Mac :
 ->javac  MovieTheatre.java  
 ->java  MovieTheatre [row] [col]

If Windows:
 ->javac -cp MovieTheatre.java  
 ->java -cp MovieTheatre row] [col]


Note: Input file will be generated via InputGenerator.java file and Output will be generated as txt file named "output.txt" in the same folder. 

