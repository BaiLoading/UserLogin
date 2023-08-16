import java.util.*;

public class test2{
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = 3;
        String s = "def";
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < s.length(); i++){
            int index = s.charAt(i);
            for(int j = 0; j < 3; j++){
                if(index == 97){
                    index = 122;
                }
                else{
                    index--;
                }
            }
            sb.append((char) index);
        }
        String ans = sb.toString();
        System.out.print(ans);
    }
}
