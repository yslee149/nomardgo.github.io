import java.util.List;

public class Hello {
    public static void main(String[] args) {
        

        list = new ArrayList<>();
        Node node = new Node("1");
        list.add(node);

        Node node2 = new Node("2");
        node.next = node2;
        
        Node node3 = new Node("3");
        node2.next = node3;

    }

    // 리스트 구조인데 하위 목록이 많은 데이터 
    
    List<Node> list;

    class Node {

        Node(String data) {
            this.data = data;
        }

        String data;
        Node next;

        List<Node> childList;

        public void addChild(Node node) {
            if (childList == null) {
                childList = new ArrayList<>();
            }
            childList.add(node);
        }
    }
}
