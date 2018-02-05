import java.util.Scanner;

public class RevStr{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
Scanner sc = new Scanner(System.in);
String original = sc.nextLine() ;
System.out.println(original);
String reverse="";
//char[] s = 
for(int i=original.length()-1; i>=0 ; i--) {
	reverse+=original.charAt(i);
}
System.out.println(reverse);
	}

}

	


