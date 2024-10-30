package com.history;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MapUndoRedoSWTApp {

	private Map<String, String> map = new HashMap<>();
	private UndoRedoManager manager = new UndoRedoManager();

	private Text keyText;
	private Text valueText;
	private Text mapDisplay;
	private Button undoButton;
	private Button redoButton;

	public static void main(String[] args) {
		new MapUndoRedoSWTApp().open();
	}

	public void open() {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Map Undo/Redo 샘플");
		shell.setSize(500, 400);
		shell.setLayout(new GridLayout(2, false));

		createUI(shell);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private void createUI(Composite parent) {
		// Key 입력
		Label keyLabel = new Label(parent, SWT.NONE);
		keyLabel.setText("Key:");
		keyText = new Text(parent, SWT.BORDER);
		keyText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Value 입력
		Label valueLabel = new Label(parent, SWT.NONE);
		valueLabel.setText("Value:");
		valueText = new Text(parent, SWT.BORDER);
		valueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Put 버튼
		Button putButton = new Button(parent, SWT.PUSH);
		putButton.setText("Put");
		GridData putButtonGridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		putButtonGridData.horizontalSpan = 2;
		putButton.setLayoutData(putButtonGridData);
		putButton.addListener(SWT.Selection, e -> handlePut());

		// Undo 버튼
		undoButton = new Button(parent, SWT.PUSH);
		undoButton.setText("Undo");
		undoButton.setEnabled(false);
		undoButton.addListener(SWT.Selection, e -> handleUndo());

		// Redo 버튼
		redoButton = new Button(parent, SWT.PUSH);
		redoButton.setText("Redo");
		redoButton.setEnabled(false);
		redoButton.addListener(SWT.Selection, e -> handleRedo());

		// Map 내용 표시
		Label mapLabel = new Label(parent, SWT.NONE);
		mapLabel.setText("Map 내용:");
		mapDisplay = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData mapDisplayGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		mapDisplayGridData.horizontalSpan = 2;
		mapDisplay.setLayoutData(mapDisplayGridData);
		mapDisplay.setEditable(false);
	}

	private void handlePut() {
		String key = keyText.getText().trim();
		String value = valueText.getText().trim();

		if (key.isEmpty()) {
			showMessage("Error", "Key는 비어 있을 수 없습니다.");
			return;
		}

		Command putCommand = new PutCommand<>(map, key, value);
		manager.executeCommand(putCommand);
		updateMapDisplay();
		updateButtons();
		clearInputFields();
	}

	private void handleRemove() {
		String key = keyText.getText().trim();

		if (key.isEmpty()) {
			showMessage("Error", "Key는 비어 있을 수 없습니다.");
			return;
		}

		if (!map.containsKey(key)) {
			showMessage("Info", "해당 Key가 존재하지 않습니다.");
			return;
		}

		updateMapDisplay();
		updateButtons();
		clearInputFields();
	}

	private void handleUndo() {
		manager.undo();
		updateMapDisplay();
		updateButtons();
	}

	private void handleRedo() {
		manager.redo();
		updateMapDisplay();
		updateButtons();
	}

	private void updateMapDisplay() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			sb.append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
		}
		mapDisplay.setText(sb.toString());
	}

	private void updateButtons() {
		undoButton.setEnabled(manager.canUndo());
		redoButton.setEnabled(manager.canRedo());
	}

	private void clearInputFields() {
		keyText.setText("");
		valueText.setText("");
	}

	private void showMessage(String title, String message) {
		MessageBox box = new MessageBox(getShell(), SWT.ICON_INFORMATION | SWT.OK);
		box.setText(title);
		box.setMessage(message);
		box.open();
	}

	private Shell getShell() {
		return keyText.getShell();
	}
}
