package hello;

import org.eclipse.swt.graphics.Image;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class SynchronizedTreeViewers {
	private static boolean isSelecting = false;
	private static boolean isExpanding = false;
	private static boolean isScrolling = false;

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Synchronized TreeViewers");
		shell.setLayout(new GridLayout(3, false));

		// ���� TreeViewer
        TreeViewer leftTreeViewer = new TreeViewer(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		GridData leftTreeGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
//      leftTreeGridData.widthHint = 400;
		leftTreeViewer.getTree().setLayoutData(leftTreeGridData);
		leftTreeViewer.setContentProvider(new MyContentProvider());
		leftTreeViewer.setLabelProvider(new MyLabelProvider());

		  // �߰� TreeViewer (�� ���� ǥ��)
        TreeViewer middleTreeViewer = new TreeViewer(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
        GridData middleTreeGridData = new GridData(SWT.FILL, SWT.FILL, false, true);
        middleTreeGridData.widthHint = 50;
        middleTreeViewer.getTree().setLayoutData(middleTreeGridData);
        middleTreeViewer.setContentProvider(new MyContentProvider() {
            @Override
            public Object[] getChildren(Object parentElement) {
                return new Object[0]; // ���� ��� �����
            }

            @Override
            public boolean hasChildren(Object element) {
                return false; // ���� ��� �����
            }
        });
        middleTreeViewer.setLabelProvider(new MyLabelProvider());
        
		// ���� TreeViewer
        TreeViewer rightTreeViewer = new TreeViewer(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		GridData rightTreeGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
//      rightTreeGridData.widthHint = 400;
		rightTreeViewer.getTree().setLayoutData(rightTreeGridData);
		rightTreeViewer.setContentProvider(new MyContentProvider());
		rightTreeViewer.setLabelProvider(new MyLabelProvider());

		// �� ���� (����)
		MyModel model = createSampleModel();
		leftTreeViewer.setInput(model);
		rightTreeViewer.setInput(model);

		// ��ũ�� ����ȭ ����
		synchronizeScrollBars(leftTreeViewer, rightTreeViewer);
		synchronizeScrollBars(rightTreeViewer, leftTreeViewer);

		// ���� ����ȭ ����
		synchronizeSelections(leftTreeViewer, rightTreeViewer);
		synchronizeSelections(rightTreeViewer, leftTreeViewer);

		// Ȯ��/��� ����ȭ ���� �߰�
		synchronizeExpansions(leftTreeViewer, rightTreeViewer);
		synchronizeExpansions(rightTreeViewer, leftTreeViewer);

		// ��Ŀ�� �̵� �� ����Ŭ��/���� ó�� ���� �߰�
		addFocusAndExpandCollapseHandlers(leftTreeViewer, rightTreeViewer);
		addFocusAndExpandCollapseHandlers(rightTreeViewer, leftTreeViewer);

		shell.setSize(800, 600);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	private static void synchronizeScrollBars(TreeViewer source, TreeViewer target) {
		ScrollBar sourceVBar = source.getTree().getVerticalBar();
		ScrollBar targetVBar = target.getTree().getVerticalBar();

		sourceVBar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (!isScrolling) {
					isScrolling = true;
					int sourceSelection = sourceVBar.getSelection();
					targetVBar.setSelection(sourceSelection);
					target.getTree().redraw();
					target.getTree().update();
					isScrolling = false;
				}
			}
		});

		ScrollBar sourceHBar = source.getTree().getHorizontalBar();
		ScrollBar targetHBar = target.getTree().getHorizontalBar();

		sourceHBar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (!isScrolling) {
					isScrolling = true;
					int sourceSelection = sourceHBar.getSelection();
					targetHBar.setSelection(sourceSelection);
					target.getTree().redraw();
					target.getTree().update();
					isScrolling = false;
				}
			}
		});

		// ���콺 �� ����ȭ
		source.getTree().addListener(SWT.MouseWheel, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (!isScrolling) {
					isScrolling = true;
					int count = event.count;
					int newSelection = sourceVBar.getSelection() - count * sourceVBar.getIncrement();
					sourceVBar.setSelection(newSelection);
					targetVBar.setSelection(newSelection);
					target.getTree().redraw();
					target.getTree().update();
					isScrolling = false;
				}
			}
		});
	}

	private static void synchronizeSelections(TreeViewer source, TreeViewer target) {
		source.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (!isSelecting) {
					isSelecting = true;
					IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					target.setSelection(selection);
					isSelecting = false;
				}
			}
		});
	}

	private static void synchronizeExpansions(TreeViewer source, TreeViewer target) {
		source.addTreeListener(new ITreeViewerListener() {
			@Override
			public void treeCollapsed(TreeExpansionEvent event) {
				if (!isExpanding) {
					isExpanding = true;
					Object element = event.getElement();
					target.getControl().getDisplay().asyncExec(() -> {
						if (target.testFindItem(element) != null) {
							target.collapseToLevel(element, AbstractTreeViewer.ALL_LEVELS);
						}
					});
					isExpanding = false;
				}
			}

			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				if (!isExpanding) {
					isExpanding = true;
					Object element = event.getElement();
					target.getControl().getDisplay().asyncExec(() -> {
						if (target.testFindItem(element) != null) {
							target.expandToLevel(element, 1);
						}
					});
					isExpanding = false;
				}
			}
		});
	}

	private static void addFocusAndExpandCollapseHandlers(TreeViewer source, TreeViewer target) {
		// �� Ű�� ��Ŀ�� �̵�
		source.getTree().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.TAB) {
					e.doit = false; // �⺻ �� ���� ����
					target.getTree().setFocus();
				} else if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					// ���� Ű�� Ȯ��/��� �� �α� ���
					handleExpandCollapse(source, target);
				}
			}
		});

		// ����Ŭ������ Ȯ��/��� �� �α� ���
		source.getTree().addListener(SWT.DefaultSelection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				handleExpandCollapse(source, target);
			}
		});
	}

	private static void handleExpandCollapse(TreeViewer source, TreeViewer target) {
		IStructuredSelection selection = (IStructuredSelection) source.getSelection();
		Object selectedElement = selection.getFirstElement();

		if (selectedElement instanceof MyModelNode) {
			MyModelNode node = (MyModelNode) selectedElement;
			if (node.hasChildren()) {
				if (source.getExpandedState(node)) {
					source.collapseToLevel(node, AbstractTreeViewer.ALL_LEVELS);
					target.getControl().getDisplay().asyncExec(() -> {
						if (target.testFindItem(node) != null) {
							target.collapseToLevel(node, AbstractTreeViewer.ALL_LEVELS);
						}
					});
				} else {
					source.expandToLevel(node, 1);
					target.getControl().getDisplay().asyncExec(() -> {
						if (target.testFindItem(node) != null) {
							target.expandToLevel(node, 1);
						}
					});
				}
			} else {
				System.out.println("Selected file: " + node.getName());
			}
		}
	}

	private static MyModel createSampleModel() {
		MyModelNode root1 = new MyModelNode("Root 1");
		MyModelNode root2 = new MyModelNode("Root 2");

		for (int i = 1; i <= 10; i++) {
			MyModelNode child = new MyModelNode("Child " + i);
			root1.addChild(child);

			for (int j = 1; j <= 5; j++) {
				child.addChild(new MyModelNode("Grandchild " + i + "." + j));
			}
		}

		for (int i = 11; i <= 20; i++) {
			MyModelNode child = new MyModelNode("Child " + i);
			root2.addChild(child);

			for (int j = 1; j <= 5; j++) {
				child.addChild(new MyModelNode("Grandchild " + i + "." + j));
			}
		}

		MyModel model = new MyModel();
		model.addRoot(root1);
		model.addRoot(root2);

		return model;
	}
}

// ������ ContentProvider ���� ����
class MyContentProvider implements ITreeContentProvider {
	@Override
	public Object[] getElements(Object inputElement) {
		return ((MyModel) inputElement).getRootElements();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return ((MyModelNode) parentElement).getChildren();
	}

	@Override
	public Object getParent(Object element) {
		return ((MyModelNode) element).getParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		return ((MyModelNode) element).hasChildren();
	}
}

// ������ LabelProvider ���� ����
class MyLabelProvider extends LabelProvider {
	@Override
	public String getText(Object element) {
		return ((MyModelNode) element).getName();
	}

	@Override
	public Image getImage(Object element) {
		return null;
	}
}

// ���� �� Ŭ����
class MyModel {
	private MyModelNode[] roots = new MyModelNode[0];

	public void addRoot(MyModelNode root) {
		MyModelNode[] newRoots = new MyModelNode[roots.length + 1];
		System.arraycopy(roots, 0, newRoots, 0, roots.length);
		newRoots[roots.length] = root;
		roots = newRoots;
	}

	public MyModelNode[] getRootElements() {
		return roots;
	}
}

// ���� �� ��� Ŭ����
class MyModelNode {
	private String name;
	private MyModelNode parent;
	private MyModelNode[] children = new MyModelNode[0];

	public MyModelNode(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addChild(MyModelNode child) {
		child.parent = this;
		MyModelNode[] newChildren = new MyModelNode[children.length + 1];
		System.arraycopy(children, 0, newChildren, 0, children.length);
		newChildren[children.length] = child;
		children = newChildren;
	}

	public MyModelNode[] getChildren() {
		return children;
	}

	public MyModelNode getParent() {
		return parent;
	}

	public boolean hasChildren() {
		return children.length > 0;
	}
}
