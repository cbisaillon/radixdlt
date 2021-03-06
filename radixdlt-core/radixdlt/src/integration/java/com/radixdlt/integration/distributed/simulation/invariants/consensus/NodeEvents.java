/*
 * (C) Copyright 2020 Radix DLT Ltd
 *
 * Radix DLT Ltd licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.  See the License for the specific
 * language governing permissions and limitations under the License.
 */

package com.radixdlt.integration.distributed.simulation.invariants.consensus;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.radixdlt.consensus.bft.BFTNode;
import com.radixdlt.environment.EventProcessor;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

/**
 * Temporary class for simulation tests.
 * TODO: Replace use of this class with the NodeEvents class in deterministic tests.
 */
public final class NodeEvents {
	private final ConcurrentMap<Class<?>, Set<BiConsumer<BFTNode, Object>>> consumers = Maps.newConcurrentMap();

	public <T> void addListener(BiConsumer<BFTNode, T> eventConsumer, Class<T> eventClass) {
		this.consumers.computeIfAbsent(eventClass, k -> Sets.newConcurrentHashSet())
			.add((node, e) -> eventConsumer.accept(node, eventClass.cast(e)));
	}

	public <T> EventProcessor<T> processor(BFTNode node, Class<T> eventClass) {
		return t -> this.consumers.getOrDefault(eventClass, Set.of()).forEach(c -> c.accept(node, t));
	}
}
