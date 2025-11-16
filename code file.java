import java.util.*;

class Building {
    int id; String name, detail;
    Building(int i,String n,String d){id=i;name=n;detail=d;}
    public String toString(){return "("+id+":"+name+")";}
}

class BSTNode {
    Building b; BSTNode l,r;
    BSTNode(Building x){b=x;}
}

class BST {
    BSTNode root;
    void insert(Building x){
        if(root==null){root=new BSTNode(x);return;}
        BSTNode c=root;
        while(true){
            if(x.id<c.b.id){
                if(c.l==null){c.l=new BSTNode(x);return;}
                c=c.l;
            } else if(x.id>c.b.id){
                if(c.r==null){c.r=new BSTNode(x);return;}
                c=c.r;
            } else{c.b=x;return;}
        }
    }
    void in(BSTNode n,List<Building>a){if(n!=null){in(n.l,a);a.add(n.b);in(n.r,a);}}
    void pre(BSTNode n,List<Building>a){if(n!=null){a.add(n.b);pre(n.l,a);pre(n.r,a);}}
    void post(BSTNode n,List<Building>a){if(n!=null){post(n.l,a);post(n.r,a);a.add(n.b);}}
    int h(BSTNode n){return n==null?0:1+Math.max(h(n.l),h(n.r));}
}

class AVLNode {
    Building b; AVLNode l,r; int h=1;
    AVLNode(Building x){b=x;}
}

class AVL {
    AVLNode root;
    int h(AVLNode n){return n==null?0:n.h;}
    void upd(AVLNode n){n.h=1+Math.max(h(n.l),h(n.r));}
    int bf(AVLNode n){return h(n.l)-h(n.r);}
    AVLNode rr(AVLNode y){
        AVLNode x=y.l,t=x.r; x.r=y; y.l=t; upd(y); upd(x); return x;
    }
    AVLNode lr(AVLNode x){
        AVLNode y=x.r,t=y.l; y.l=x; x.r=t; upd(x); upd(y); return y;
    }
    AVLNode ins(AVLNode n,Building x){
        if(n==null) return new AVLNode(x);
        if(x.id<n.b.id) n.l=ins(n.l,x);
        else if(x.id>n.b.id) n.r=ins(n.r,x);
        else{n.b=x;return n;}
        upd(n);
        int d=bf(n);
        if(d>1){
            if(x.id<n.l.b.id) return rr(n);
            n.l=lr(n.l); return rr(n);
        }
        if(d<-1){
            if(x.id>n.r.b.id) return lr(n);
            n.r=rr(n.r); return lr(n);
        }
        return n;
    }
    void insert(Building x){root=ins(root,x);}
    void in(AVLNode n,List<Building>a){if(n!=null){in(n.l,a);a.add(n.b);in(n.r,a);}}
    int height(){return h(root);}
}

class Graph {
    Map<Integer,Building> nodes=new HashMap<>();
    Map<Integer,List<int[]>> adj=new HashMap<>();
    void add(Building b){nodes.put(b.id,b); adj.putIfAbsent(b.id,new ArrayList<>());}
    void edge(int u,int v,int w){adj.get(u).add(new int[]{v,w});adj.get(v).add(new int[]{u,w});}
    int[][] matrix(){
        int n=Collections.max(nodes.keySet())+1;
        int[][]m=new int[n][n];
        for(int u:adj.keySet())for(int[]e:adj.get(u))m[u][e[0]]=e[1];
        return m;
    }
    List<Building> bfs(int s){
        List<Building> o=new ArrayList<>(); Queue<Integer>q=new LinkedList<>();
        Set<Integer>v=new HashSet<>(); q.add(s); v.add(s);
        while(!q.isEmpty()){
            int u=q.poll(); o.add(nodes.get(u));
            for(int[]e:adj.get(u)) if(!v.contains(e[0])){v.add(e[0]);q.add(e[0]);}
        }
        return o;
    }
    List<Building> dfs(int s){
        List<Building> o=new ArrayList<>(); Stack<Integer>st=new Stack<>();
        Set<Integer>v=new HashSet<>(); st.push(s);
        while(!st.isEmpty()){
            int u=st.pop(); if(v.contains(u))continue;
            v.add(u); o.add(nodes.get(u));
            for(int[]e:adj.get(u)) st.push(e[0]);
        }
        return o;
    }
    Map<Integer,Integer> dijkstra(int s){
        Map<Integer,Integer>d=new HashMap<>(); for(int x:nodes.keySet())d.put(x,999999);
        d.put(s,0);
        PriorityQueue<int[]>pq=new PriorityQueue<>(Comparator.comparingInt(a->a[1]));
        pq.add(new int[]{s,0});
        while(!pq.isEmpty()){
            int u=pq.poll()[0];
            for(int[]e:adj.get(u)){
                int v=e[0],w=e[1];
                if(d.get(u)+w<d.get(v)){d.put(v,d.get(u)+w);pq.add(new int[]{v,d.get(v)});}
            }
        }
        return d;
    }
    List<int[]> kruskal(){
        List<int[]>E=new ArrayList<>();
        for(int u:adj.keySet())for(int[]e:adj.get(u))if(u<e[0])E.add(new int[]{u,e[0],e[1]});
        E.sort(Comparator.comparingInt(a->a[2]));
        Map<Integer,Integer>p=new HashMap<>(),r=new HashMap<>();
        for(int x:nodes.keySet()){p.put(x,x);r.put(x,0);}
        List<int[]>mst=new ArrayList<>();
        for(int[]e:E){
            int a=e[0],b=e[1];
            int pa=find(a,p),pb=find(b,p);
            if(pa!=pb){
                mst.add(e);
                if(r.get(pa)<r.get(pb))p.put(pa,pb);
                else if(r.get(pa)>r.get(pb))p.put(pb,pa);
                else{p.put(pb,pa);r.put(pa,r.get(pa)+1);}
            }
        }
        return mst;
    }
    int find(int x,Map<Integer,Integer>p){
        if(p.get(x)!=x)p.put(x,find(p.get(x),p));
        return p.get(x);
    }
}

class ExprNode {String v; ExprNode l,r; ExprNode(String x){v=x;}}

class ExprTree {
    ExprNode build(String[]t){
        Stack<ExprNode>s=new Stack<>();
        for(String x:t){
            if(x.matches("[+\\-*/]")){
                ExprNode r=s.pop(),l=s.pop(),n=new ExprNode(x);
                n.l=l;n.r=r;s.push(n);
            } else s.push(new ExprNode(x));
        }
        return s.pop();
    }
    double eval(ExprNode n){
        if(n.l==null)return Double.parseDouble(n.v);
        double a=eval(n.l),b=eval(n.r);
        return switch(n.v){case "+"->a+b;case "-"->a-b;case "*"->a*b;default->a/b;};
    }
}

public class Main{
    public static void main(String[]a){
        Building[]b={
                new Building(0,"Admin","A"),
                new Building(1,"Lib","B"),
                new Building(2,"CSE","C"),
                new Building(3,"DS","D"),
                new Building(4,"Hostel","E"),
                new Building(5,"Cafe","F"),
                new Building(6,"Gym","G")
        };

        BST bst=new BST();
        AVL avl=new AVL();
        for(Building x:b){bst.insert(x);avl.insert(x);}

        List<Building> ino=new ArrayList<>();
        bst.in(bst.root,ino);
        System.out.println("BST Inorder: "+ino);
        System.out.println("BST Height: "+bst.h(bst.root));

        List<Building> ain=new ArrayList<>();
        avl.in(avl.root,ain);
        System.out.println("AVL Inorder: "+ain);
        System.out.println("AVL Height: "+avl.height());

        Graph g=new Graph();
        for(Building x:b)g.add(x);
        int[][]E={{0,1,4},{0,2,2},{1,2,1},{2,3,3},{3,4,2},{4,6,6},{1,5,7},{5,6,5}};
        for(int[]e:E)g.edge(e[0],e[1],e[2]);

        System.out.println("Adj List: "+g.adj);
        int[][]m=g.matrix();
        System.out.println("Adj Matrix: ");
        for(int[]r:m)System.out.println(Arrays.toString(r));

        System.out.println("BFS: "+g.bfs(0));
        System.out.println("DFS: "+g.dfs(0));

        System.out.println("Dijkstra: "+g.dijkstra(0));

        System.out.println("MST: "+g.kruskal());

        ExprTree et=new ExprTree();
        ExprNode root=et.build(new String[]{"3","4","5","*","6","-","+"});
        System.out.println("Expr Value: "+et.eval(root));
    }
}
