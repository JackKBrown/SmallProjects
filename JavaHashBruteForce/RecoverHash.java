import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

//todo remove hashes from list once finding them.
public class RecoverHash{
	public static final String NAMES_FILEPATH = "names.txt";
	public static final String ENGLISH_DICTIONARY = "english_words.txt";
	//71 characters means a key space of 71^4
	public static final char[] alphanumeribet = 
		"abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ_!@#$%^&*".toCharArray();
	public static final String newline = System.getProperty("line.separator");
	
	//NoSuchAlgorithmException, IOException
	public static void main(String args[]) throws Exception{
		if (args.length != 2){
			System.out.println("Usage RecoverHash <input txt filepath> <output txt filepath.");
		}else{
			List<String> hashes = readFile(args[0]);
			
			//get names dictionary
			List<String> names = readFile(NAMES_FILEPATH);
			
			//get english dictionary
			List<String> dictionary = readFile(ENGLISH_DICTIONARY);

			//generate the alphanumeric 4 letter dictionary
			List<String> permutations = new ArrayList<String>();
			//71 characters means a key space of 71^4 = 25411681
			//CHANGE BACK TO 4!!!!
			generate_permutations(alphanumeribet, 4, "", permutations);
			
			//generate the name permutations
			List<String> name_permutations = new ArrayList<String>();
			for (String name: names) generate_upper_lower_perm("",name,name_permutations);
			//generate the numbers on the end of the name permutations
			List<String> name_permutation_numbers = new ArrayList<String>();
			for(String password: name_permutations){
				for(int i = 0; i<=9999; i++){
					name_permutation_numbers.add(password+i);
				}
			}
			
			//create the output list
			List<String> outList = new ArrayList<String>();
			
			//test the dictionaries
			testDictionary(names, hashes, outList);
			testDictionary(dictionary, hashes, outList);
			testDictionary(permutations, hashes, outList);
			testDictionary(name_permutation_numbers, hashes, outList);
			
			//generate output file
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(args[1])));
			for (String str: outList)out.write(str+newline);
			out.close();
		}
		
	}
	
	/*
	 *
	 *
	 */
	private static List<String> readFile(String filepath) throws Exception{
		List<String> contents = new ArrayList<String>();
		File file = new File(filepath);
		BufferedReader buffRead = new BufferedReader(new FileReader(file));
		String line;
		while((line = buffRead.readLine()) != null) contents.add(line);
		buffRead.close();
		return contents;
	}
	
	//should be able to generate an exhaustive list of any alphabet of any length
	private static void generate_permutations(char[] alphabet, int length, String start, List<String> results){
		for (int i = 0; i<alphabet.length; i++){
			String str = start+alphabet[i];
			if (length == 1){
				results.add(str);
			}else{
				generate_permutations(alphabet, length-1, str, results);
			}
		}
	}
	
	//should be able to generate an exhaustive list of any alphabet of any length
	private static void generate_upper_lower_perm(String front, String end, List<String> results){
		String lower = front+end.charAt(0);
		String upper = front+Character.toUpperCase(end.charAt(0));
		if (end.length()==1){
			results.add(lower);
			results.add(upper);
		}else{
			generate_upper_lower_perm(lower, end.substring(1,end.length()), results);
			generate_upper_lower_perm(upper, end.substring(1,end.length()), results);
		}
	}
	
	private static String hash(String password) throws NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashInBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));

		// bytes to hex
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
		return sb.toString();
	}
	
	private static void testDictionary(List<String> dictionary, List<String> hashes, List<String> outList)throws NoSuchAlgorithmException{
		for(String password: dictionary){
			String hash = hash(password);
			//System.out.println(hash);
			for(String str: hashes){
				if(str.equalsIgnoreCase(hash)){
					System.out.println("password found: "+password+" = "+str);
					outList.add(password + " " + str);
				}
			}
		}
	}

	
	
}