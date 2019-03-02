
public class CodeWars {

	public static void main(String[] args){
		//System.out.println(Tickets(new int[]{25, 25, 25, 25, 50, 100, 50}));
		//System.out.println(TripleDouble(451999277L, 41177722899L));
		//System.out.println(TripleDouble(1222345L, 12345L));
	}
	
	/* Vasya Clerk: Avenger tickets are $25, but Vasya has no cash in the register
	 * Respond with YES or NO if we have enough enough money to sell the ticket and have enough
	 * for change if it is required. Cannot skip peopleTickets(new int[]{25, 25, 25, 25, 50, 100, 50}) in line. 
	 * 
	 * Call: Tickets(new int[]{25, 25, 25, 25, 50, 100, 50}) 
	 * */
	public static String Tickets(int[] peopleInLine)
	  {
	        //Your code is here...
	        String yes = "YES";
	        String no = "NO";
	        String answer = "";
	        int cashOnHand = 0;
	        
	        for (int i = 0; i < peopleInLine.length; i++){
	          System.out.println("Cash: " + cashOnHand + " Next person has: " + peopleInLine[i]);
	          if (peopleInLine[i] == 25){
	        	  cashOnHand += 25;
	          }
	          if (cashOnHand > (peopleInLine[i] - 25)){
	            cashOnHand -= peopleInLine[i] - 25;
	            answer = yes;
	          }
	          else {
	            answer = no;
	          }
	        }
	        
	        return answer;
	  }
	/* Find a triple of a number in the first long and a double of a number on the second long
	 * return 1 if there is a triple and a double
	 * return 0 otherwise
	 * Call: TripleDouble(451999277L, 41177722899L)
	 */
	public static int TripleDouble(long num1, long num2){
		int result = 0;
		int counter = 0;
		String tripleString = Long.toString(num1);
    System.out.println(tripleString);
		for(int i = 0; i < tripleString.length(); i++){
			if (i <= tripleString.length() - 3){
				if ((tripleString.charAt(i) == tripleString.charAt(i+1)) && tripleString.charAt(i) == tripleString.charAt(i+2)){
					counter++;
					System.out.println("Found triple!");
				}
			}
		}
		String doubleString = Long.toString(num2);
    System.out.println(doubleString);
		for(int i = 0; i < doubleString.length(); i++){
			if (i <= doubleString.length() - 2){
				if (doubleString.charAt(i) == doubleString.charAt(i+1)){
					counter++;
					System.out.println("Found double!");
				}
			}
		}
		System.out.println("Counter: " + counter);
		if (counter >= 2){
			result = 1;
		}
		return result;
	}
	
	public static int TripleDouble1(long num1, long num2) {
	    
	    if ((num1 + "").matches(".*(\\d)\\1{2,}.*") && (num2 + "").matches(".*(\\d)\\1{1,}.*")) {
	      return 1;
	    } else {
	      return 0;
	    }
	  }
}
