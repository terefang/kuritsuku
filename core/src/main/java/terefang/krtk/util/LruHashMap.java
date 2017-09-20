/*
 * Copyright (c) 2017. terefang@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package terefang.krtk.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A HashMap that will only store a limited number of the most-recently-
 * accessed key/value pairs. For more information on the exact behavior, have
 * a look at Java's LinkedHashMap.
 *
 * @param <K>
 * @param <V>
 */
public class LruHashMap<K, V> extends LinkedHashMap<K, V> {
	
	private static final float LOAD_FACTOR = 0.75f;
	private final int sizeLimit;
	private Map.Entry<K, V> lastRemoved;
	
	// A limit < 0 means no limit
	public LruHashMap(int sizeLimit) {
		this(sizeLimit, 16);
	}
	
	// A limit < 0 means no limit
	public LruHashMap(int sizeLimit, int initialCapacity) {
		// Note: the super constructor's third argument specifies
		// access-ordering rather than default insertion-ordering
		super(initialCapacity, LOAD_FACTOR, true);
		this.sizeLimit = sizeLimit;
	}
	
	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		if (sizeLimit < 0) {
			return false;
		} else {
			if (size() > sizeLimit) {
				lastRemoved = eldest;
				return true;
			}
			return false;
		}
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		throw new UnsupportedOperationException("putAll() not supported on LruHashMap due to data loss when retrieving evicted records via getAndClearEldestRemoved");
	}
	
	public Map.Entry<K, V> getAndClearEldestRemoved() {
		Map.Entry<K, V> result = lastRemoved;
		lastRemoved = null;
		return result;
	}
	
	public int getMaxCapacity() {
		return sizeLimit;
	}
	
}