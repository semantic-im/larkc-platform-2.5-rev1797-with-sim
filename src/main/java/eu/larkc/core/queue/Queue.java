/*
   This file is part of the LarKC platform 
   http://www.larkc.eu/

   Copyright 2010 LarKC project consortium

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package eu.larkc.core.queue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A blocking queue. Not as good as Queue1 (which uses one of Doug Lea's nifty
 * queue classes), but this one does allow the putting of 'null' on the queue.
 * Of course, this doesn't make much sense if the consumer is polling, but it's
 * useful in the short term.
 * 
 * @param <E>
 *            The class of objects passed along the queue.
 */
public class Queue<E> {

	private List<QueueListener<E>> listeners;
	private final List<E> mQueue = new LinkedList<E>();

	/**
	 * Constructor that initializes listeners.
	 */
	public Queue() {
		this.listeners = new ArrayList<QueueListener<E>>();
	}

	/**
	 * Returns one element of the queue and deletes it.
	 * 
	 * @return The first element of the queue.
	 */
	public synchronized E take() {
		try {
			while (mQueue.isEmpty()) {
				wait();
			}
			return mQueue.remove(0);
		} catch (InterruptedException e) {
			// We never call interrupt, so this exception can be ignored
			return null;
		}
	}

	/**
	 * Puts one element to the queue.
	 * 
	 * @param item
	 *            The item to put in the queue.
	 */
	public synchronized void put(E item) {
		mQueue.add(item);
		notify();
		fireElementAdded(item);
	}

	/**
	 * Returns the size of the queue.
	 * 
	 * @return The size of the queue.
	 */
	public synchronized int size() {
		return mQueue.size();
	}

	/**
	 * Answers the question if the queue is empty.
	 * 
	 * @return True if the queue is empty, false otherwise.
	 */
	public synchronized boolean isEmpty() {
		return mQueue.isEmpty();
	}

	/**
	 * Returns the first element of the queue.
	 * 
	 * @return The first element of the queue.
	 */
	public synchronized E poll() {
		if (!mQueue.isEmpty()) {
			return mQueue.get(0);
		}
		return null;
	}

	/**
	 * Adds a listener to the queue.
	 * 
	 * @param theQueueListener
	 *            The listener to add.
	 */
	public synchronized void addListener(QueueListener<E> theQueueListener) {
		this.listeners.add(theQueueListener);
	}

	/**
	 * Informs all listener that a element was added.
	 * 
	 * @param e
	 *            The added element.
	 */
	public synchronized void fireElementAdded(E e) {
		for (QueueListener<E> l : listeners) {
			l.elementAdded(e);
		}
	}
}
