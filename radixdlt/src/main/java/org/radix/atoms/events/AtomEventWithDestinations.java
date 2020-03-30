/*
 * (C) Copyright 2020 Radix DLT Ltd
 *
 * Radix DLT Ltd licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the
 * License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.  See the License for the specific
 * language governing permissions and limitations under the License.
 */

package org.radix.atoms.events;

import com.radixdlt.atommodel.Atom;
import com.radixdlt.identifiers.EUID;

import java.util.Set;
import java.util.function.Supplier;

public class AtomEventWithDestinations extends AtomEvent {
	private Set<EUID> destinations;

	public AtomEventWithDestinations(Atom atom, Supplier<Set<EUID>> destinationsSupplier) {
		super(atom);
		this.destinations = destinationsSupplier.get();
	}

	public Set<EUID> getDestinations() {
		return destinations;
	}

}
