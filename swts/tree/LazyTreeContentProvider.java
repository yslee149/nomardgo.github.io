package com.test.tree;

import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import java.util.List;
import java.util.Map;

public class LazyTreeContentProvider implements ILazyTreeContentProvider {

    private TreeViewer treeViewer;
    private Map<Integer, Integer[]> dataMap;

    public LazyTreeContentProvider(TreeViewer treeViewer, Map<Integer, Integer[]> dataMap) {
        this.treeViewer = treeViewer;
        this.dataMap = dataMap;
    }

    @Override
    public void updateElement(Object parent, int index) {
        if (parent == null) {
            Integer rootId = 1; // 루트 노드 ID 설정
            treeViewer.replace(null, index, rootId);
            treeViewer.setChildCount(rootId, dataMap.get(rootId).length);
        } else if (parent instanceof Integer) {
            Integer parentId = (Integer) parent;
            Integer[] children = dataMap.get(parentId);
            if (children != null && index < children.length) {
                Integer childId = children[index];
                treeViewer.replace(parentId, index, childId);
                treeViewer.setChildCount(childId, dataMap.get(childId).length);
            }
        }
    }

    @Override
    public Object getParent(Object element) {
        return null; // 부모를 찾는 로직이 필요하면 구현
    }

    @Override
    public void updateChildCount(Object element, int currentChildCount) {
        if (element == null) {
            treeViewer.setChildCount(null, 1); // 루트 노드는 하나만 존재
        } else if (element instanceof Integer) {
            Integer id = (Integer) element;
            Integer[] children = dataMap.get(id);
            treeViewer.setChildCount(id, children != null ? children.length : 0);
        }
    }

    @Override
    public void dispose() {
        // 자원 해제 로직이 필요하면 구현
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // 입력이 변경될 때 처리할 내용
    }
}
