
public class ReverseVowels {

	public static void main(String args[]){
		String test = "Instance of an Object.";
		System.out.println(test);
		System.out.println(reverseVowels(test));
	}
	public static String reverseVowels(String str){
		
		String isVowel = "aeiouAEIOU";
		int low = 0;
		int high = str.length()-1;
		char[] cArray = str.toCharArray();
		
		while (low < high){
			if (!isVowel.contains(String.valueOf(str.charAt(low)))){
				low++;
				continue;
			}
			if (!isVowel.contains(String.valueOf(str.charAt(high)))){
				high--;
				continue;
			}
			
			// swapping variables
			swap(cArray, low, high);
			low++;
			high--;
		}
		return String.valueOf(cArray);
	}
	public static void swap(char[] cArray, int low, int high){
		char tempArray = cArray[low];
		cArray[low] = cArray[high];
		cArray[high] = tempArray;
	}
}
