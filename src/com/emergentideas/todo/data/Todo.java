package com.emergentideas.todo.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Todo {

	@Id
	@GeneratedValue
	protected int id;
	protected String todoText;
	
	@ManyToOne
	protected TodoList todoList;

	public String getTodoText() {
		return todoText;
	}

	public void setTodoText(String todoText) {
		this.todoText = todoText;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TodoList getTodoList() {
		return todoList;
	}

	public void setTodoList(TodoList todoList) {
		this.todoList = todoList;
	}
	
	
}
