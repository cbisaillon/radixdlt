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

package com.radixdlt.atommodel.tokens;

import com.radixdlt.atomos.RriId;
import com.radixdlt.identifiers.RadixAddress;
import com.radixdlt.constraintmachine.Particle;
import com.radixdlt.utils.UInt256;
import java.util.Objects;

/**
 *  A particle which represents an amount of transferrable fungible tokens
 *  owned by some key owner and stored in an account.
 */
public final class TokensParticle implements Particle {
	private final RriId rriId;
	private final RadixAddress address;
	private final UInt256 amount;

	public TokensParticle(
		RadixAddress address,
		UInt256 amount,
		RriId rriId
	) {
		this.address = Objects.requireNonNull(address);
		this.rriId = Objects.requireNonNull(rriId);
		this.amount = Objects.requireNonNull(amount);
	}

	public RadixAddress getAddress() {
		return this.address;
	}

	public RriId getRriId() {
		return this.rriId;
	}

	@Override
	public String toString() {
		return String.format("%s[%s:%s:%s]",
			getClass().getSimpleName(),
			rriId,
			amount,
			address
		);
	}

	public UInt256 getAmount() {
		return this.amount;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof TokensParticle)) {
			return false;
		}
		TokensParticle that = (TokensParticle) o;
		return Objects.equals(address, that.address)
			&& Objects.equals(rriId, that.rriId)
			&& Objects.equals(amount, that.amount);
	}

	@Override
	public int hashCode() {
		return Objects.hash(address, rriId, amount);
	}
}