package core;

/**
 * Undo history stack class (generic)
 * @param <T>
 */
public class Stack <T extends Object> {

	private static int stackSize = 15;
	private T []stack = null; 

	public Stack(){
		this(stackSize);
	}
	public Stack(int size){
		stack = (T[]) new Object[size];
		stackSize = size;
	}

	public void push(T variable){
		for(int i = stackSize-1; i >= 0; i--)
			if(i > 0 )
				stack[i] = stack[i-1];
			stack[0] = variable;
	}

	public T pop(){
		T last = stack[0];
		for(int i = 0; i < stackSize; i++)
			if(i < stackSize - 1)
				stack[i] = stack[i+1];
			else
				stack[i] = null;
		return last;
	}
}
