public class LFUCache {
    
    private int capacity;
    private PriorityQueue queue;
    private HashMap cache;
    
    public LFUCache(int capacity) {
        Comparator<Node> comparator = new NodeComparator();
        this.capacity = capacity;
        int initialCapacity = 1;
        float loadFactor = 1.0f;
        if (capacity > 0) {
            initialCapacity = (int) Math.ceil(capacity / loadFactor);   
        }
        this.queue = new PriorityQueue<Node>(initialCapacity, comparator);
        this.cache = new HashMap(initialCapacity);
    }
    
    public int get(int key) {
        if (this.cache.containsKey(key)) {
            // fetch the cached node
            Node node = (Node) this.cache.get(key);
            // remove node from priority queue
            this.queue.remove(node);
            // update the ts and hits values
            int val = node.getValue();
            // add node back to priority queue. queue will arrange itself in correct order
            this.queue.add(node);
            // return the cached value
            return val;
        } else {
            return -1;   
        }
    }
    
    public void set(int key, int value) {
        Node node;
        // dont honor any set operations when the capacity has been set to 0
        if (this.capacity == 0) {
            return;
        }
        
        if (this.cache.containsKey(key)) {
            node = (Node) this.cache.get(key);
            this.queue.remove(node);
            // update the ts and hits values for the node
            node.setValue(value);
            // update the priority queue ordering
            this.queue.add(node);
        } else {
            // at capacity, evict the candidate node - this is the first item in the priority queue
            if (this.cache.size() == this.capacity) {
                Node evictNode = (Node) this.queue.poll();
                // remove this key from the cache
                this.cache.remove(evictNode.key);
            }
            // create a new node object and add to cache
            node = new Node(key, value);
            this.cache.put(key, node);
            // update the priority queue ordering
            this.queue.add(node);
        }
    }
    
    private class NodeComparator implements Comparator<Node> {
        @Override
        public int compare(Node a, Node b) {
            // a is older than b
            if (a.ts < b.ts) {
                // if frequency of a is less than b, move a back
                if (a.hits < b.hits) {
                    return -1;
                // if frequency of a is greater than b, move a forward
                } else if (a.hits > b.hits) {
                    return 1;
                // default to timestamp comparison
                } else {
                    return -1;
                }
            }
            // a is newer than b
            if (a.ts > b.ts) {
                // if frequency of a is greater than b, move a forward
                if (a.hits > b.hits) {
                    return 1;
                // if frequency of a is less than b, move a back
                } else if (a.hits < b.hits) {
                    return -1;
                // default to timestamp comparison
                } else {
                    return 1;
                }
            }
            return 0;
        }
    }
    
    private class Node {
        private int value;
        public int hits;
        public long ts;
        public int key;
        
        public Node(int key, int value) {
            this.key = key;
            this.value = value;
            this.hits = 0;
            this.ts = System.nanoTime();
        }
        
        public int getValue() {
            this.hits += 1;
            this.ts = System.nanoTime();
            return this.value;
        }
        
        public void setValue(int value) {
            this.hits += 1;
            this.ts = System.nanoTime();
            this.value = value;
        }
        
        public String toString() {
            return "{value: " + Integer.toString(this.value) + ", " +
                "key: " + Integer.toString(this.key) + ", " +
                "hits: " + Integer.toString(this.hits) + ", " +
                "ts: " + Long.toString(this.ts) + "}";
        }
    }
}