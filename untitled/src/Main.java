import java.util.Arrays;
import java.util.Collections;


public class Main {
    public static void main(String[] args) {
        Integer[] arr2={3,1,1,2,3};
        int[] temp = new int[arr2.length];
        int length = 0;
        temp[0] = arr2[0];
        int k = 2;
        Arrays.sort(arr2, Collections.reverseOrder());
        for(int i = 1; i < arr2.length; i++){
            temp[i] = temp[i-1] + arr2[i];
        }
        for(int i =0; i < temp.length; i++){
            if(k * (i+1) < temp[i]){
                length++;
            }
        }
        System.out.println(length);
    }
}
