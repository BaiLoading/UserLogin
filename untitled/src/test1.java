import com.sun.tools.javac.Main;

import java.util.*;

import java.util.Scanner;

public class test1{
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] weight = new int[n];
        for(int i = 0; i < n; i++){
            weight[i] = sc.nextInt();
        }
        List<List<Integer>> graph = new ArrayList<>();
        for(int i = 0; i <= n; i++){
            graph.add(new ArrayList<>());
        }

        for(int i = 0; i < n - 1; i++){
            int u = sc.nextInt();
            int v = sc.nextInt();

            graph.get(u).add(v);
            graph.get(v).add(u);
        }

        int[] redNodes = new int[n+1];
        dfs(graph, weight, 1, redNodes);

        int maxRedNodes = 0;
        for(int i = 0; i <= n; i++){
            maxRedNodes = Math.max(maxRedNodes, redNodes[i]);
        }

        System.out.println(maxRedNodes);
    }

    private static boolean isPerfectSquare(int num){
        int sqrt = (int) Math.sqrt(num);
        return sqrt*sqrt == num;
    }

    private static void dfs(List<List<Integer>> graph, int[] weight, int node, int[] redNodes){
        redNodes[node] = isPerfectSquare(weight[node-1]) ? 1 : 0;

        for(int neighbor : graph.get(node)){
            if(redNodes[neighbor] == 0){
                dfs(graph, weight, neighbor, redNodes);
            }
            redNodes[node] = Math.max(redNodes[node], redNodes[neighbor]);
        }
    }
}