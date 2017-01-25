package lib280.tree;

import lib280.base.Cursor280;
import lib280.base.CursorPosition280;
import lib280.dictionary.Dict280;
import lib280.exception.AfterTheEnd280Exception;
import lib280.exception.ContainerEmpty280Exception;
import lib280.exception.ContainerFull280Exception;
import lib280.exception.DuplicateItems280Exception;
import lib280.exception.InvalidArgument280Exception;
import lib280.exception.InvalidState280Exception;
import lib280.exception.ItemNotFound280Exception;
import lib280.exception.NoCurrentItem280Exception;

public class ArrayedBinaryTreeWithCursors280<I> extends
		ArrayedBinaryTree280<I> implements Dict280<I>, Cursor280<I> {

	protected boolean searchesRestart;
	
	public ArrayedBinaryTreeWithCursors280(int cap) {
		super(cap);
		searchesRestart = true;
	}

	/**
	 * Does the container contain the element 'y'?.
	 *
	 * @param y item whose presence is to be determined
	 * @return True if y is in the container, false otherwise.
	 */
	@Override
	public boolean has(I y) {
		if(count != 0) {
			for (int i = 0; i < count; i++) {
				if (items[i+1].equals(y)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Move the current position to the first (the default) or the next x, if it exists.
	 *
	 * @param x item being sought
	 */
	@Override
	public void search(I x) {
		if(count != 0) {
			for (int i = 0; i < count; i++) {
				if (items[i+1].equals(x)) {
					currentNode = i+1;
				}
			}
		}
	}

	/**
	 * Is the current position before the start of the structure?.
	 */
	@Override
	public boolean before() {
		return currentNode==0;
	}

	/**
	 * Is the current position after the end of the structure?.
	 */
	@Override
	public boolean after() {
		return currentNode>count;
	}

	/**
	 * Advance one item in the data structure.
	 *
	 * @throws AfterTheEnd280Exception if the cursor is already in the after position and cannot be advanced.
	 * @precond !after()
	 */
	@Override
	public void goForth() throws AfterTheEnd280Exception {
		if (currentNode+1>capacity || after() ) throw new AfterTheEnd280Exception("Cannot go forth when reach end.");
		currentNode++;
	}

	/**
	 * Insert x into the dictionary.
	 *
	 * @param x item to be inserted into the dictionary
	 * @throws ContainerFull280Exception  if the dictionary is full.
	 * @throws DuplicateItems280Exception if the dictionary already contains the item x.
	 * @precond !isFull() and !has(x)
	 */
	@Override
	public void insert(I x) throws ContainerFull280Exception, DuplicateItems280Exception {
		if (count == capacity)throw new ContainerFull280Exception("The tree can not insert more item");
		if(has(x)) throw new DuplicateItems280Exception("Object already exist inside tree");
		items[count+1] = x;
		count++;
	}

	/**
	 * Go to the first item in the structure.
	 *
	 * @throws ContainerEmpty280Exception if this container is empty.
	 * @precond !isEmpty()
	 */
	@Override
	public void goFirst() throws ContainerEmpty280Exception {
		if(isEmpty()) throw new ContainerEmpty280Exception("Can not go to first node of an empty tree");
		currentNode = 1;
	}

	/**
	 * Delete current item from the data structure.
	 *
	 * @throws NoCurrentItem280Exception if the cursor is not currently positioned at a valid item.
	 * @precond itemExists()
	 */
	@Override
	public void deleteItem() throws NoCurrentItem280Exception {
		if(currentNode > capacity || currentNode <= 0) throw new NoCurrentItem280Exception("Can nor delete an item not inside tree");
		delete(items[currentNode]);
	}

	/**
	 * Move to the position prior to the first element in the structure.
	 */
	@Override
	public void goBefore() {
		currentNode = 0;
	}

	/**
	 * Move to the position after the last element in the structure.
	 */
	@Override
	public void goAfter() {
		currentNode = capacity;
	}

	/**
	 * Delete the item x.
	 *
	 * @param x item to be deleted from the dictionary
	 * @throws ItemNotFound280Exception if the item x is not in the dictionary.
	 * @precond has(x)
	 */
	@Override
	public void delete(I x) throws ItemNotFound280Exception {
		if(!has(x)) throw new ItemNotFound280Exception("Item not exist inside current tree");
		search(x);
		if (currentNode != count) {
			items[currentNode] = items[count];
			items[count + 1] = null;
			count--;
		}
		else{
			items[currentNode] = null;
			currentNode--;
			count--;
		}
	}

	/**
	 * Retrieve an item from the dictionary with membershipEquals(item,y).
	 *
	 * @param y item to obtain from the dictionary
	 * @throws ItemNotFound280Exception if there is no item that matches y.
	 * @precond has(y)
	 */
	@Override
	public I obtain(I y) throws ItemNotFound280Exception {
		CursorPosition280 saved = this.currentPosition();
		this.goFirst();
		while(this.itemExists()) {
			if( membershipEquals(this.item(), y)) {
				I found = this.item();
				this.goPosition(saved);
				return found;
			}
			this.goForth();
		}
		this.goPosition(saved);
		throw new ItemNotFound280Exception("The given item could not be found.");
	}

	/**
	 * A method that determines whether two elements of type I
	 * are equal.  Comparison must be based on the contents
	 * of x and y (do not use reference comparison).
	 *
	 * @param x item to be compared to y
	 * @param y item to be compared to x
	 * @return Returns true if x is the same element as y, false otherwise.
	 */
	@Override
	public boolean membershipEquals(I x, I y) {
	    return x.equals(y);
	}

	/**
	 * Set searches to always start over.
	 */
	@Override
	public void restartSearches() {
		this.searchesRestart = true;
	}

	/**
	 * Set searches to continue from the next item.
	 */
	@Override
	public void resumeSearches() {
		this.searchesRestart = false;
	}

	/**
	 * The current position.
	 */
	@Override
	public CursorPosition280 currentPosition() {
		return new ArrayedBinaryTreePosition280(this.currentNode);
	}

	/**
	 * Go to position c.
	 *
	 * @param c position to which to go
	 */
	@Override
	public void goPosition(CursorPosition280 c) {
		if (!(c instanceof ArrayedBinaryTreePosition280))
			throw new InvalidArgument280Exception("The cursor position parameter"
					    + " must be a ArrayedBinaryTreePosition280<I>");

		this.currentNode = ((ArrayedBinaryTreePosition280)c).currentNode;
	}

	/**
	 * Move the cursor to the parent of the current node.
	 * @precond Current node is not the root.
	 * @throws InvalidState280Exception when the cursor is on the root already.
	 */
	public void parent() throws InvalidState280Exception {
        // TODO - Implement this method
		if (currentNode == 1) throw new InvalidState280Exception("CurrentNode(root) have not parent");
		if(currentNode %2 == 0) currentNode = currentNode/2;
		else currentNode = (currentNode-1)/2;
	}

	/**
	 * Move the cursor to the left child of the current node.
	 * 
	 * @precond The tree must not be empty and the current node must have a left child.
	 * @throws ContainerEmpty280Exception if the tree is empty.
	 * @throws InvalidState280Exception if the current node has no left child.
	 */
	public void goLeftChild()  throws InvalidState280Exception, ContainerEmpty280Exception {
        // TODO - Implement this method
		if(isEmpty())throw new ContainerEmpty280Exception("Can not go to child for an empty tree");
		if(currentNode*2 > capacity ) throw new InvalidState280Exception("Can not go to child when not exist");
		currentNode = currentNode * 2;
	}
	
	/**
	 * Move the cursor to the right child of the current node.
	 * 
	 * @precond The tree must not be empty and the current node must have a right child.
	 * @throws ContainerEmpty280Exception if the tree is empty.
	 * @throws InvalidState280Exception if the current item has no right child.
	 */
	public void goRightChild() throws InvalidState280Exception, ContainerEmpty280Exception {
        // TODO - Implement this method
		if(isEmpty())throw new ContainerEmpty280Exception("Can not go to child for an empty tree");
		if((currentNode*2+1) > capacity ) throw new InvalidState280Exception("Can not go to child when not exist");
		currentNode = currentNode * 2 + 1;
	}	
	
	/**
	 * Move the cursor to the sibling of the current node.
	 * 
	 * @precond The current node must have a sibling.  The tree must not be empty.
	 * @throws ContainerEmpty280Exception if the tree is empty.
	 * @throws InvalidState280Exception if the current item has no sibling.
	 */	
	public void goSibling() throws InvalidState280Exception, ContainerEmpty280Exception {
        // TODO - Implement this method
		if(isEmpty())throw new ContainerEmpty280Exception("Can not go to child for an empty tree");
		if((currentNode+1) > capacity || currentNode == 1 ) throw new InvalidState280Exception("Can not go to child when not exist");
		if(currentNode %2 == 0) currentNode++;
		else currentNode --;
	}
	
	/**
	 * Move the cursor to the root of the tree.
	 * 
	 * @precond The tree must not be empty.
	 * @throws ContainerEmpty280Exception if the tree is empty.
	 */
	public void root() throws ContainerEmpty280Exception {
        // TODO - Implement this method
		if(isEmpty())throw new ContainerEmpty280Exception("Can not go to child for an empty tree");
		currentNode = 1;
	}	
	

	public static void main(String[] args) {
		ArrayedBinaryTreeWithCursors280<Integer> T = new ArrayedBinaryTreeWithCursors280<Integer>(10);
		
		// IsEmpty on empty tree.
		if(!T.isEmpty()) System.out.println("Test of isEmpty() on empty tree failed.");
		
		// Test root() on empty tree.
		Exception x = null;
		try {
			T.root();
		}
		catch(ContainerEmpty280Exception e) {
			x = e;
		}
		finally {
			if( x == null ) System.out.println("Expected exception moving to root of empty tree.  Got none.");
		}
		
		// test goFirst() on empty tree
		x = null;
		try {
			T.goFirst();
		}
		catch(ContainerEmpty280Exception e) {
			x = e;
		}
		finally {
			if( x == null ) System.out.println("Expected exception moving to first elelement of empty tree.  Got none.");
		}
	
		
		
		// Test goLeftChild() on empty tree.
		x = null;
		try {
			T.goLeftChild();
		}
		catch(ContainerEmpty280Exception e) {
			x = e;
		}
		finally {
			if( x == null ) System.out.println("Expected exception moving to left child in empty tree.  Got none.");
		}
		
		// Test goLeftChild() on empty tree.
		x = null;
		try {
			T.goRightChild();
		}
		catch(ContainerEmpty280Exception e) {
			x = e;
		}
		finally {
			if( x == null ) System.out.println("Expected exception moving to right child in empty tree.  Got none.");
		}
		
		
		// Check itemExists on empty tree
		if(T.itemExists() ) System.out.println("itemExists() returned true on an empty tree.");
		
		// Insert on empty tree.
		T.insert(1);

		// Check ItemExists on tree with one element.
		T.root();
		if(!T.itemExists() ) System.out.println("itemExists() returned false on a tree with one element with cursor at the root.");
		
		// isEmpty on tree with 1 element.
		if(T.isEmpty()) System.out.println("Test of isEmpty() on non-empty tree failed.");

		// Insert on tree with 1 element
		T.insert(2);
		
		// Insert some more elements
		for(int i=3; i <= 10; i++) T.insert(i);

		if(T.count() != 10  ) System.out.println("Expected tree count to be 10, got "+ T.count());

		
		// Test for isFull on a full tree.
		if(!T.isFull()) System.out.println("Test of isFull() on a full tree failed.");
		
		// Test insert on a full tree
		x = null;
		try {
			T.insert(11);
		}
		catch(ContainerFull280Exception e) {
			x = e;
		}
		finally {
			if( x == null ) System.out.println("Expected exception inserting into a full tree.  Got none.");
		}
		
		

		
		// Test positioning methods
		
		// Test root()
		T.root();
		//System.out.print(T.count);
		if( T.item() != 1 ) System.out.println("Expected item at root to be 1, got " + T.item());
		
		T.goLeftChild();
		
		if( T.item() != 2 ) System.out.println("Expected current item to be 2, got " + T.item());
		
		T.goRightChild();
		if( T.item() != 5 ) System.out.println("Expected current item to be 5, got " + T.item());

		
		T.goLeftChild();
		if( T.item() != 10 ) System.out.println("Expected current item to be 10,  got " + T.item());
		
		// Current node now has no children.
		x = null;
		try {
			T.goLeftChild();
		}
		catch( InvalidState280Exception e ) {
			x = e;
		}
		finally {
			if( x == null) System.out.println("Expected exception moving to left child of a leaf.  Got none.");
		}
		
		x = null;
		try {
			T.goRightChild();
		}
		catch( InvalidState280Exception e ) {
			x = e;
		}
		finally {
			if( x == null) System.out.println("Expected exception moving to right child of a leaf.  Got none.");
		}

		// Remove the last item ( a leaf)
		T.deleteItem();
		if( T.item() != 9 ) System.out.println("Expected current item to be 9, got " + T.item());

		T.parent();
		
		
		
		// Remove a node with 2 children.  The right child 9 gets promoted.
		T.deleteItem();
		if( T.item() != 9 ) System.out.println("Expected current item to be 9, got " + T.item());
		
		
		// Remove a node with 1 child.  The left child 8 gets promoted.
		T.deleteItem();
		if( T.item() != 8 ) System.out.println("Expected current item to be 8, got " + T.item());
		
		// Remove the root successively.  There are 7 items left.
		T.root();
		T.deleteItem();
		if( T.item() != 7 ) System.out.println("Expected root to be 7, got " + T.item());

		T.deleteItem();
		if( T.item() != 6 ) System.out.println("Expected root to be 6, got " + T.item());

		T.deleteItem();
		if( T.item() != 5 ) System.out.println("Expected root to be 5, got " + T.item());
		
		T.deleteItem();
		if( T.item() != 8 ) System.out.println("Expected root to be 8, got " + T.item());

		T.deleteItem();
		if( T.item() != 3 ) System.out.println("Expected root to be 3, got " + T.item());

		T.deleteItem();
		if( T.item() != 2 ) System.out.println("Expected root to be 2, got " + T.item());

		// Tree has one item.  Try parent() on one item.
		x = null;
		try {
			T.parent();
		}
		catch( InvalidState280Exception e ) {
			x = e;
		}
		finally {
			if( x == null) System.out.println("Expected exception moving to parent of root.  Got none.");
		}
		
		
		// Try to go to the sibling
		x = null;
		try {
			T.goSibling();
		}
		catch(InvalidState280Exception e) {
			x = e;
		}
		finally {
			if( x == null ) System.out.println("Expected exception moving to sibling when at the root.  Got none.");
		}
		
		
		
		T.deleteItem(); 
		
		
		// Tree should now be empty
		if(!T.isEmpty()) System.out.println("Expected empty tree.  isEmpty() returned false.");

		if(T.capacity() != 10) System.out.println("Expected capacity to be 10, got "+ T.capacity());
		
		if(T.count() != 0  ) System.out.println("Expected tree count to be 0, got "+ T.count());
		
		// Remove from empty tree.
		x = null;
		try {
			T.deleteItem();
		}
		catch(NoCurrentItem280Exception e) {
			x = e;
		}
		finally {
			if( x == null ) System.out.println("Expected exception deleting from an empty tree.  Got none.");
		}
		
		
		
		// Try to go to the sibling
		x = null;
		try {
			T.goSibling();
		}
		catch(ContainerEmpty280Exception e) {
			x = e;
		}
		finally {
			if( x == null ) System.out.println("Expected exception moving to sibling in empty tree tree.  Got none.");
		}
		
		
		T.insert(1);
		T.root();
		
		// Try to go to the sibling when there is no child.
		x = null;
		try {
			T.goSibling();
		}
		catch(InvalidState280Exception e) {
			x = e;
		}
		finally {
			if( x == null ) System.out.println("Expected exception moving to sibling of node with no sibling.  Got none.");
		}
		
		T.goBefore();
		if(!T.before()) System.out.println("Error: Should be in 'before' position, but before() reports otherwise.");
		if(T.after()) System.out.println("Error: T.after() reports cursor in the after position when it should not be.");
		
		T.goForth();
		//System.out.println(T.currentNode);
		if(T.before()) System.out.println("Error: T.before() reports cursor in the before position when it should not be.");
		if(T.after()) System.out.println("Error: T.after() reports cursor in the after position when it should not be.");

		T.goForth();
		//System.out.println(T.currentNode);
		if(!T.after()) System.out.println("Error: Should be in 'after' position, but after() reports otherwise.");
		if(T.before()) System.out.println("Error: T.before() reports cursor in the before position when it should not be.");
		
		x=null;
		try {
			T.goForth();
		}
		catch(AfterTheEnd280Exception e) {
			x = e;
		}
		finally {
			if( x == null ) System.out.println("Expected exception advancing cursor when already after the end.  Got none.");
		}

		
		int y=-1;
		T.goBefore();
		try {
			y =  T.obtain(1); 
		}
		catch( ItemNotFound280Exception e ) {
			System.out.println("Error: Unexpected exception occured when attempting T.obtain(1).");
		}
		finally {
			if(y != 1 ) System.out.println("Obtained item should be 1 but it isn't.");
			if(!T.before()) System.out.println("Error: cursor should still be in the before() position after T.obtain(1), but it isn't.");
		}
		
		if(!T.has(1)) System.out.println("Error: Tree has element 1, but T.has(1) reports that it does not.");
		
		
		
		System.out.println("Regression test complete.");
	}
}
