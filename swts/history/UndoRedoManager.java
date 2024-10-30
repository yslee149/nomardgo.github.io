package com.history;

import java.util.Stack;

public class UndoRedoManager {
	private Stack<Command> undoStack = new Stack<>();
	private Stack<Command> redoStack = new Stack<>();

	public void executeCommand(Command command) {
		command.execute();
		undoStack.push(command);
		redoStack.clear(); // 새로운 명령이 실행되면 redo 스택을 비웁니다.
	}

	public void undo() {
		if (!undoStack.isEmpty()) {
			Command command = undoStack.pop();
			command.undo();
			redoStack.push(command);
		} else {
			System.out.println("Undo 스택이 비어 있습니다.");
		}
	}

	public void redo() {
		if (!redoStack.isEmpty()) {
			Command command = redoStack.pop();
			command.execute();
			undoStack.push(command);
		} else {
			System.out.println("Redo 스택이 비어 있습니다.");
		}
	}

	public boolean canUndo() {
		return !undoStack.isEmpty();
	}

	public boolean canRedo() {
		return !redoStack.isEmpty();
	}
}
