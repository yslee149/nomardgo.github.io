package com.test.tree;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class LazyTreeViewerExample {

	private static TreeViewer treeViewer;
	private static TreeDataGenerator dataGenerator;

	public static void main(String[] args) {
		// SWT 기본 설정
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Lazy Loading TreeViewer Example");
		shell.setSize(800, 1050);

		// TreeViewer 설정
		treeViewer = new TreeViewer(shell, SWT.VIRTUAL | SWT.BORDER);
		treeViewer.getTree().setBounds(10, 10, 700, 900);

		treeViewer.setUseHashlookup(true);

		// 데이터 생성
		dataGenerator = new TreeDataGenerator();
		Map<Integer, ArrayList<Integer>> data = dataGenerator.getData();

		// 트리 컨텐츠 제공자 설정
		treeViewer.setContentProvider(new LazyTreeContentProvider(treeViewer, data));

		// 트리 레이블 제공자 설정
		treeViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Integer) {
					return "Node ID: " + element;
				}
				return super.getText(element);
			}
		});

		// 트리 초기화
		treeViewer.setInput(1); // 루트 노드의 ID를 입력으로 설정

		// 새로고침 버튼 추가
		Button refreshButton = new Button(shell, SWT.PUSH);
		refreshButton.setText("Refresh Tree");
		refreshButton.setBounds(10, 920, 100, 30);

		// 버튼 클릭 이벤트 처리
		refreshButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				refreshTree();
			}
		});

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	// 트리 데이터를 새로고침하는 함수
	public static void refreshTree() {
		System.out.println("Refreshing tree...");
		Map<Integer, ArrayList<Integer>> newData = dataGenerator.getData();
		treeViewer.setContentProvider(new LazyTreeContentProvider(treeViewer, newData));
		treeViewer.refresh();

		// 특정 레벨까지만 트리를 확장 (예: 2단계)
		int levelToExpand = 2;
		treeViewer.expandToLevel(levelToExpand);
	}
}
