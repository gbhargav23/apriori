import java.util.ArrayList;
import java.util.Collections;

public class Combiner {
	
	public ArrayList<ArrayList<Integer>> combine(int n, int k) {
        ArrayList<ArrayList<Integer>> sol = new ArrayList<ArrayList<Integer>>();
        recursion(n,k,new ArrayList<Integer>(), sol);
        return sol;
    }
     
    private void recursion(int n, int k, ArrayList<Integer> partial,
        ArrayList<ArrayList<Integer>> sol) {
        if(partial.size() == k && !sol.contains(partial)) {
            Collections.sort(partial);
            sol.add(partial);
        } else if(partial.size() > k) {
            return;
        } else {
            for(int i = n; i >= 1; --i) {
                ArrayList<Integer> partial_sol = new ArrayList<Integer>();
                partial_sol.addAll(partial);
                partial_sol.add(i);
                recursion(i-1, k, partial_sol, sol);
            }
        }
    }

}
