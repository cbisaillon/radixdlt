/*
 * (C) Copyright 2021 Radix DLT Ltd
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

package com.radixdlt.sync.messages.local;

/**
 * A message indicating that the sync service should start the sync check.
 */
public final class SyncCheckTrigger {

	public static SyncCheckTrigger create() {
		return new SyncCheckTrigger();
	}

	private SyncCheckTrigger() {
	}

	@Override
	public String toString() {
		return String.format("%s{}", this.getClass().getSimpleName());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		return o != null && getClass() == o.getClass();
	}

	@Override
	public int hashCode() {
		return 1;
	}
}