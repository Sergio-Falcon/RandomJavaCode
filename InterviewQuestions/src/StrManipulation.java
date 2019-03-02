import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class StrManipulation {

	public static void main(String[] args){
		 printDuplicateCharacters("Programming");
	     printDuplicateCharacters("Combination");
	     printDuplicateCharacters("Java");
	     System.out.println(replaceVowelsWithNextVowelIteration("Aaaabbyyy"));
	}
	

	/*
     * Find all duplicate characters in a String and print each of them.
     */
    public static void printDuplicateCharacters(String word) {
    	char[] characters = word.toCharArray();

        // build HashMap with character and number of times they appear in String
        Map<Character, Integer> charMap = new HashMap<Character, Integer>();
        for (Character ch : characters) {
            if (charMap.containsKey(ch)) {
                charMap.put(ch, charMap.get(ch) + 1);
            } else {
                charMap.put(ch, 1);
            }
        }

        // Iterate through HashMap to print all duplicate characters of String
        Set<Map.Entry<Character, Integer>> entrySet = charMap.entrySet();
        System.out.printf("List of duplicate characters in String '%s' %n", word);
        for (Map.Entry<Character, Integer> entry : entrySet) {
            if (entry.getValue() > 1) {
                System.out.printf("%s : %d %n", entry.getKey(), entry.getValue());
            }
        }
    }
    
    public static String replaceVowelsWithNextVowelIteration(String sentence){
    	//base case
    	if (sentence.isEmpty() == true){
    		return "Cannot input an empty string.";
    	}

    	//traverse letter by letter in the sentence
    	for (int i = 0; i < sentence.length(); i++){
    		
    		//replace vowels with next iteration of the vowel
    		//vowels = 'a' 'e' 'i' 'o' 'u' 'y' 
    		//if (sentence.charAt(i) == 'a' || sentence.charAt(i) == 'A')
    		switch(sentence.charAt(i)){
    			case 'a': //replace with e
    				sentence.replace('a', 'y');
    				break;
    			case 'A': //replace with e
    				break;
    			case 'e': //replace with i
    				break;
    			case 'E': //replace with i
    				break;
    			case 'i': //replace with o
    				break;
    			case 'I': //replace with o
    				break;
    			case 'o': //replace with u
    				break;
    			case 'O': //replace with u
    				break;
    			case 'u': //replace with y
    				break;
    			case 'U': //replace with y
    				break;
    			case 'y': //replace with a
    				break;
    			case 'Y': //replace with a
    				break;
    		}
    	}
    	return sentence;
    }
    
    
	public static String deleteCharacterAtIndex(String original, int index){
		StringBuilder sb = new StringBuilder(original);
		sb.deleteCharAt(index);
		String newString = sb.toString();
		return newString;
	}
	public static String deleteVowels(String original){
		String str= original.replaceAll("[AEIOUaeiou]", "");
		return str;
	}
}
