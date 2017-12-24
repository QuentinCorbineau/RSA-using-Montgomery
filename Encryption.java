import java.math.BigInteger;
import java.util.Random;
import java.io.File;
import java.io.FileWriter;

public class Encryption  {
	 
	private static int V;
	static BigInteger k,r,v,n = BigInteger.ZERO;
	 
	public static void main(String args[]) {
	
		 int intp = Generator();
		 int intq = Generator();
		 int inte = Generator();
		 		
		 
		 
		 BigInteger p = BigInteger.valueOf(23/*intp*/);
		 BigInteger q = BigInteger.valueOf(47/*intq*/);
		 n = p.multiply(q);
		 BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
		 BigInteger e = BigInteger.valueOf(173/*inte*/);
		 BigInteger d = BigInteger.valueOf(myModInverse(e.intValue(), phi.intValue()));
		 //BigInteger d = e.modInverse(phi);
		 
		 /*System.out.println(p);
		 System.out.println(q);
		 System.out.println(n);
		 System.out.println(phi);
		 System.out.println(e);
		 System.out.println(d);*/
		 
		 //Init Montgomery
		 k = BigInteger.valueOf((int) (Math.log(n.longValue())/Math.log(2))+1);
		 r = BigInteger.valueOf(1<<k.longValue());
		 //System.out.println("r : "+r); 
		 v = BigInteger.valueOf(0).subtract(BigInteger.valueOf(myModInverse(n.intValue(),r.intValue())));
		 //v= BigInteger.valueOf(myModInverse(n.intValue(),r.intValue()-1));
		 //System.out.println("v : "+v); 
		 V = v.intValue();
		 
		 
		
		 String message = "It's a test";
		 int length = message.length();
		 int ASCIImessage[] = new int[length];
		 
		 
		 char charmessage[] = message.toCharArray();
		 System.out.println("ASCII Message");
		 BigInteger msg[] = new BigInteger[length];
		 String msg_mong ="";
		 for (int i = 0; i < length; i++){
			 ASCIImessage[i] = (int) charmessage[i];
			 System.out.print(String.valueOf(ASCIImessage[i]));
			 msg[i] = BigInteger.valueOf(ASCIImessage[i]);
			 msg[i] = BigInteger.valueOf((msg[i].multiply(r)).intValue()%n.intValue());
			 msg_mong += msg[i];
			 
		 }
		 
		 System.out.println("\nmsg_mong = " + msg_mong);
		 
		 
		 String messagecoder = "";
		 String messagecode[] = new String[length];
		 System.out.println("");
		 System.out.println("Encrypted Message");
		 for (int i=0; i< length; i++){
			 BigInteger code = msg[i];
			 messagecode[i] = Integer.toString(myModPow(code,e,n).intValue());
			 //messagecode[i] = Integer.toString(myModPow(code.intValue(),e.intValue(),n.intValue()));
			 //messagecode[i] = Integer.toString(code.modPow(e, n).intValue());
			 while(messagecode[i].length()<4){
					messagecode[i]="0"+messagecode[i];
				}
			 System.out.print(String.valueOf(messagecode[i]));
			 messagecoder += messagecode[i];
		 }
		// d = Montgomery(msg, r, v, n);
		 
		 System.out.println("\n");
		 System.out.println("Decrypted Message");
		 String messagedecoder = "";
		 String messagedecoder_fin = "";
		 int ASCIId[]= convertStringToInt(messagecoder);
		 int ASCIIdecode[]=new int[length];
		 BigInteger message_return;
		 BigInteger msgd[] = new BigInteger[length];
		 for (int i=0; i<length; i++){
			 BigInteger decode = BigInteger.valueOf(ASCIId[i]);
			 ASCIIdecode[i] = myModPow(decode,d,n).intValue();
			// ASCIIdecode[i] = decode.modPow(d, n).intValue();
			 //msgd[i] = Montgomery(BigInteger.valueOf(ASCIIdecode[i]), d, n);
			 //messagedecoder += msgd[i].intValue();
			 messagedecoder += ASCIIdecode[i];
			 message_return = BigInteger.valueOf(ASCIIdecode[i]);
			 message_return = Montgomery(message_return,BigInteger.valueOf(1),n);
			 messagedecoder_fin += (char)message_return.intValue();
		 }
		 System.out.println(messagedecoder_fin);
		 
		 try{
			 File file = new File("D:\\Prog\\Java\\File_Encryption\\Encryption.txt");
			 file.createNewFile();
			 FileWriter filewrite = new FileWriter(file);
			 filewrite.write(String.valueOf(messagecoder));
			 filewrite.write("\r\n");
			 filewrite.write(String.valueOf(messagedecoder));
			 filewrite.write("\r\n");
			 filewrite.close();
		 }
		 catch (Exception e1) {}
	
		 
	 }
	
	public static int Generator() {
        int num = 0;
        Random rand = new Random(); // generate a random number
        num = rand.nextInt(9999) + 1;

        while (!isPrime(num) && num>9000) {          
            num = rand.nextInt(9999) + 1;
        }
        return num;
    }

    /**
     * Checks to see if the requested value is prime.
     */
    private static boolean isPrime(int inputNum){
        if (inputNum <= 3 || inputNum % 2 == 0) 
            return inputNum == 2 || inputNum == 3; //this returns false if number is <=1 & true if number = 2 or 3
        int divisor = 3;
        while ((divisor <= Math.sqrt(inputNum)) && (inputNum % divisor != 0)) 
            divisor += 2; //iterates through all possible divisors
        return inputNum % divisor != 0; //returns true/false
    }
    
    private static int[]convertStringToInt (String from){
		int to[]= new int [from.length()/4];
		int j=0;
		int k=0;
		String tmp="";
		for(int i=0;i<from.length();i++){
			if(j<4){
				
				tmp = tmp+from.charAt(i);
				j++;
			}
			if(j>=4){
				
				j=0;
				to[k]=Integer.parseInt(tmp);
				tmp="";
				k++;
			}
		}
		
		
		//
		return to;
		
	}
    
	private static BigInteger myModPow(BigInteger a,BigInteger b,BigInteger c) {
		BigInteger X = r.subtract(n); //Neutre pour le produit de Montgomery
		BigInteger Y = BigInteger.valueOf((long) (Math.log(b.longValue())/Math.log(2))+1);
		for(long i = Y.longValue()-1; i >= 0 ; i--){
			long cpt = b.longValue()>>>i & 1;
			X = Montgomery(X,X,c);
			if(cpt == 1){
				X = Montgomery(X,a,c);
			}
		}
		return X;
	}
    
    private static long myModInverse(int w, int m)
    {
    	int ww = w;
	     int mm = m;
	     int t0 = 0;
	     int t = 1;
	     int q = (int) mm/ww;
	     int r = mm - q * ww;
	     int tmp = 0;
	 
	   while(r > 0)
	   {
	        tmp = t0 - q * t;
	 
	        if (tmp >= 0)
		 {
	              tmp = tmp % m;
	         }
	         else
	         {
	             tmp = m - ((-tmp) % m);
	         }
	 
		 t0 = t;
		 t = tmp;
		 mm = ww;
		 ww = r;
		 q = (int) mm/ww;
		 r = mm - q * ww;
	    }
	   
	   return t;
    }
    
    private static BigInteger Montgomery(BigInteger a,BigInteger b,BigInteger n){
		 BigInteger s,t,m,u=BigInteger.ZERO;
		 BigInteger v = BigInteger.valueOf(V);
		 s=a.multiply(b);
		 //System.out.println("s : " + s);
		 t=BigInteger.valueOf((s.longValue()*v.longValue()) & (r.longValue()-1));
		 //System.out.println("t : " + t);
		 m= BigInteger.valueOf(s.longValue()+(t.longValue()*n.longValue()));
		 ///System.out.println("m : " + m);
		 u=BigInteger.valueOf(m.longValue() >> k.longValue());
		 //System.out.println("u : " + u +"\n");
		 if(u.compareTo(n)>=0){
			 return u.subtract(n);
		 
		 };
		 /*int x= u.longValue()%n.longValue();
		 return BigInteger.valueOf(x);*/
		 return u;
	}
    

    
   /* public static BigInteger modulo (BigInteger a, BigInteger b){
		if(a.compareTo(b)<0){
			return a;
		}
		return modulo(a.subtract(b),b);
	}*/
    
    
	
}


//A faire : génération des paramètres (done) et modePow (to do)
// ajouter les 0 avant, fixer la taille à 8 (done)