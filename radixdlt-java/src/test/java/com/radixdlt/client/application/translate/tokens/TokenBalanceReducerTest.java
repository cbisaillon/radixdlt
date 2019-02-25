package com.radixdlt.client.application.translate.tokens;

import java.math.BigDecimal;

import com.radixdlt.client.atommodel.tokens.MintedTokensParticle;
import org.junit.Test;
import org.radix.utils.UInt256;

import com.radixdlt.client.core.atoms.RadixHash;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.radixdlt.client.core.ledger.TransitionedParticle;

public class TokenBalanceReducerTest {

	@Test
	public void testSimpleBalance() {
		MintedTokensParticle minted = mock(MintedTokensParticle.class);
		RadixHash hash = mock(RadixHash.class);
		when(minted.getAmount()).thenReturn(UInt256.TEN);
		when(minted.getGranularity()).thenReturn(UInt256.ONE);
		when(minted.getHash()).thenReturn(hash);
		TokenTypeReference token = mock(TokenTypeReference.class);
		when(minted.getTokenTypeReference()).thenReturn(token);

		TokenBalanceReducer reducer = new TokenBalanceReducer();
		TokenBalanceState tokenBalance = reducer.reduce(new TokenBalanceState(), TransitionedParticle.n2u(minted));
		BigDecimal tenSubunits = TokenTypeReference.subunitsToUnits(UInt256.TEN);
		assertThat(tokenBalance.getBalance().get(token).getAmount().compareTo(tenSubunits)).isEqualTo(0);
	}
}